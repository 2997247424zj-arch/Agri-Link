package com.example.agrilinkback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AgriLinkBackApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthEndpointReturnsUnifiedResponse() throws Exception {
        mockMvc.perform(get("/api/system/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.application").value("Agri-Link-back"))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void rolesEndpointReturnsBusinessAndSystemAdminRoles() throws Exception {
        mockMvc.perform(get("/api/system/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].code").value("BUYER"))
                .andExpect(jsonPath("$.data[1].code").value("FARMER"))
                .andExpect(jsonPath("$.data[2].code").value("EXPERT"))
                .andExpect(jsonPath("$.data[3].code").value("BANK"))
                .andExpect(jsonPath("$.data[4].code").value("SYSTEM_ADMIN"));
    }

    @Test
    void protectedInterfacesRequireExpectedRole() throws Exception {
        mockMvc.perform(get("/api/users/farmer001"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/finance/applications").with(role("BUYER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(get("/api/finance/applications").with(role("BANK")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/admin/overview").with(role("FARMER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(get("/api/admin/overview").with(role("SYSTEM_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void authAndImageUploadInterfacesCoverLoginRegistrationAndFileTask() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "farmer001",
                                  "password": "secret",
                                  "role": "FARMER"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("farmer001"))
                .andExpect(jsonPath("$.data.role").value("FARMER"))
                .andExpect(jsonPath("$.data.token").value("FARMER"))
                .andExpect(jsonPath("$.data.headerName").value("X-User-Role"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "admin001",
                                  "password": "secret",
                                  "role": "SYSTEM_ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("SYSTEM_ADMIN"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "farmer001",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "bank002",
                                  "password": "secret",
                                  "nickName": "Bank Two",
                                  "phone": "13800000902",
                                  "identityNum": "430000199701010002",
                                  "address": "Jishou",
                                  "role": "BANK",
                                  "realName": "Bank User"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("bank002"))
                .andExpect(jsonPath("$.data.role").value("BANK"));

        MockMultipartFile image = new MockMultipartFile(
                "file",
                "orange.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47}
        );
        mockMvc.perform(multipart("/api/files/images")
                        .file(image)
                        .with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.originalName").value("orange.png"))
                .andExpect(jsonPath("$.data.contentType").value("image/png"))
                .andExpect(jsonPath("$.data.url").value(org.hamcrest.Matchers.startsWith("/files/")));

        mockMvc.perform(delete("/api/users/bank002").with(role("BANK")))
                .andExpect(status().isOk());
    }

    @Test
    void systemAdminInterfacesWorkWithoutBusinessRoleConflict() throws Exception {
        mockMvc.perform(get("/api/admin/overview").with(role("SYSTEM_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userCount").value(2))
                .andExpect(jsonPath("$.data.orderCount").value(1))
                .andExpect(jsonPath("$.data.usersByRole.FARMER").value(1))
                .andExpect(jsonPath("$.data.usersByRole.SYSTEM_ADMIN").value(1));

        mockMvc.perform(get("/api/admin/users").with(role("SYSTEM_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userName").value("farmer001"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "admin-register-blocked",
                                  "password": "secret",
                                  "nickName": "Blocked Admin",
                                  "phone": "13800000999",
                                  "identityNum": "430000199901010001",
                                  "address": "Jishou",
                                  "role": "SYSTEM_ADMIN",
                                  "realName": "Blocked"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "roleuser001",
                                  "password": "secret",
                                  "nickName": "Role User",
                                  "phone": "13800000901",
                                  "identityNum": "430000199701010001",
                                  "address": "Jishou",
                                  "role": "FARMER",
                                  "realName": "Role User"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("FARMER"));

        mockMvc.perform(patch("/api/admin/users/roleuser001/role")
                        .with(role("SYSTEM_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "role": "BUYER"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("BUYER"));

        mockMvc.perform(patch("/api/admin/users/admin001/role")
                        .with(role("SYSTEM_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "role": "FARMER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        mockMvc.perform(get("/api/admin/trade/orders").with(role("SYSTEM_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderId").value(1));

        String orderResponse = mockMvc.perform(post("/api/trade/orders")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Admin status oranges",
                                  "price": 8.20,
                                  "content": "Temporary order for admin test",
                                  "type": "1",
                                  "picture": "/files/admin-orange.png",
                                  "ownName": "farmer001",
                                  "address": "Jishou"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int orderId = objectMapper.readTree(orderResponse).path("data").path("orderId").asInt();

        mockMvc.perform(patch("/api/admin/trade/orders/{orderId}/status", orderId)
                        .with(role("SYSTEM_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderStatus": 2,
                                  "cooperationName": "roleuser001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value(2))
                .andExpect(jsonPath("$.data.cooperationName").value("roleuser001"));

        mockMvc.perform(get("/api/admin/trade/purchases").with(role("SYSTEM_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].purchaseId").value(1));

        String purchaseBody = """
                {
                  "ownName": "roleuser001",
                  "purchaseType": 1,
                  "address": "Jishou",
                  "details": [
                    {
                      "orderId": %d,
                      "unitPrice": 8.20,
                      "count": 1
                    }
                  ]
                }
                """.formatted(orderId);
        String purchaseResponse = mockMvc.perform(post("/api/trade/purchases")
                        .with(role("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int purchaseId = objectMapper.readTree(purchaseResponse).path("data").path("purchaseId").asInt();

        mockMvc.perform(patch("/api/admin/trade/purchases/{purchaseId}/status", purchaseId)
                        .with(role("SYSTEM_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "purchaseStatus": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.purchaseStatus").value(2));

        mockMvc.perform(get("/api/admin/finance/applications").with(role("SYSTEM_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].financeId").value(1));

        String financeResponse = mockMvc.perform(post("/api/finance/applications")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bankId": 1001,
                                  "ownName": "farmer001",
                                  "realName": "Zhang San",
                                  "phone": "13800000001",
                                  "idNum": "430000199001010001",
                                  "money": 12000.00,
                                  "rate": 3.50,
                                  "repayment": "12 months",
                                  "fileInfo": "/files/admin-finance.pdf"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int financeId = objectMapper.readTree(financeResponse).path("data").path("financeId").asInt();

        mockMvc.perform(patch("/api/admin/finance/applications/{financeId}/status", financeId)
                        .with(role("SYSTEM_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": 1,
                                  "remark": "admin approved"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.remark").value("admin approved"));

        mockMvc.perform(delete("/api/finance/applications/{financeId}", financeId).with(role("BANK")))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/trade/purchases/{purchaseId}", purchaseId).with(role("BUYER")))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/trade/orders/{orderId}", orderId).with(role("FARMER")))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/users/roleuser001").with(role("BUYER")))
                .andExpect(status().isOk());
    }

    @Test
    void personalCenterAndMatchingInterfacesWork() throws Exception {
        mockMvc.perform(get("/api/addresses/owners/farmer001").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"));

        mockMvc.perform(get("/api/trade/orders/owners/farmer001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"));

        mockMvc.perform(get("/api/trade/shopping-cart/owners/farmer001").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"));

        mockMvc.perform(get("/api/trade/purchases/owners/farmer001").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"));

        mockMvc.perform(patch("/api/users/farmer001/password")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "new-secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("farmer001"))
                .andExpect(jsonPath("$.data.password").value("new-secret"));

        mockMvc.perform(get("/api/finance/banks/matches?amount=30000"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/finance/banks/matches?amount=30000").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bankId").value(1001));

        mockMvc.perform(get("/api/finance/matches/farmers/1001").with(role("BANK")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userName").value("farmer001"));
    }

    @Test
    void userAddressAndTradeWriteInterfacesWork() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "buyer002",
                                  "password": "secret",
                                  "nickName": "Buyer Two",
                                  "phone": "13800000022",
                                  "identityNum": "430000199501010001",
                                  "address": "Jishou",
                                  "role": "BUYER",
                                  "realName": "Buyer Li"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("buyer002"))
                .andExpect(jsonPath("$.data.role").value("BUYER"));

        String addressResponse = mockMvc.perform(post("/api/addresses")
                        .with(role("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownName": "buyer002",
                                  "consignee": "Buyer Li",
                                  "phone": "13800000022",
                                  "addressDetail": "Jishou new address",
                                  "isDefault": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownName").value("buyer002"))
                .andExpect(jsonPath("$.data.isDefault").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int addressId = objectMapper.readTree(addressResponse).path("data").path("id").asInt();

        String orderResponse = mockMvc.perform(post("/api/trade/orders")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "New oranges",
                                  "price": 7.50,
                                  "content": "Fresh fruit from Jishou",
                                  "type": "1",
                                  "picture": "/files/new-orange.png",
                                  "ownName": "farmer001",
                                  "address": "Jishou"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("New oranges"))
                .andExpect(jsonPath("$.data.orderStatus").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int orderId = objectMapper.readTree(orderResponse).path("data").path("orderId").asInt();

        String cartBody = """
                {
                  "orderId": %d,
                  "count": 2,
                  "ownName": "buyer002"
                }
                """.formatted(orderId);
        String cartResponse = mockMvc.perform(post("/api/trade/shopping-cart")
                        .with(role("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.count").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int shoppingId = objectMapper.readTree(cartResponse).path("data").path("shoppingId").asInt();

        String purchaseBody = """
                {
                  "ownName": "buyer002",
                  "purchaseType": 1,
                  "address": "Jishou new address",
                  "details": [
                    {
                      "orderId": %d,
                      "unitPrice": 7.50,
                      "count": 2
                    }
                  ]
                }
                """.formatted(orderId);
        String purchaseResponse = mockMvc.perform(post("/api/trade/purchases")
                        .with(role("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownName").value("buyer002"))
                .andExpect(jsonPath("$.data.totalPrice").value(15.00))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int purchaseId = objectMapper.readTree(purchaseResponse).path("data").path("purchaseId").asInt();

        mockMvc.perform(patch("/api/trade/orders/{orderId}/status", orderId)
                        .with(role("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderStatus": 1,
                                  "cooperationName": "buyer002"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value(1))
                .andExpect(jsonPath("$.data.cooperationName").value("buyer002"));

        mockMvc.perform(patch("/api/trade/purchases/{purchaseId}/status", purchaseId)
                        .with(role("BUYER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "purchaseStatus": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.purchaseStatus").value(1));

        mockMvc.perform(delete("/api/trade/purchases/{purchaseId}", purchaseId).with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/trade/shopping-cart/{shoppingId}", shoppingId).with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/trade/orders/{orderId}", orderId).with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/addresses/{addressId}", addressId).with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/users/buyer002").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void financeWriteInterfacesWork() throws Exception {
        String bankResponse = mockMvc.perform(post("/api/finance/banks")
                        .with(role("BANK"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bankName": "Rural Credit Bank",
                                  "introduce": "Loan for seasonal production",
                                  "bankPhone": "07430000002",
                                  "money": 200000.00,
                                  "rate": 3.20,
                                  "repayment": "24 months"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bankName").value("Rural Credit Bank"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int bankId = objectMapper.readTree(bankResponse).path("data").path("bankId").asInt();

        String intentionResponse = mockMvc.perform(post("/api/finance/intentions")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "farmer001",
                                  "realName": "Zhang San",
                                  "address": "Jishou",
                                  "amount": 45000.00,
                                  "application": "greenhouse expansion",
                                  "item": "vegetable base",
                                  "repaymentPeriod": "18 months",
                                  "area": "30 mu",
                                  "phone": "13800000001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("farmer001"))
                .andExpect(jsonPath("$.data.amount").value(45000.00))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int intentionId = objectMapper.readTree(intentionResponse).path("data").path("id").asInt();

        String financeResponse = mockMvc.perform(post("/api/finance/applications")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bankId": %d,
                                  "ownName": "farmer001",
                                  "realName": "Zhang San",
                                  "phone": "13800000001",
                                  "idNum": "430000199001010001",
                                  "money": 45000.00,
                                  "rate": 3.20,
                                  "repayment": "24 months",
                                  "combinationName1": "Li Si",
                                  "combinationPhone1": "13800000003",
                                  "combinationIdnum1": "430000199101010001",
                                  "fileInfo": "/files/new-finance.pdf"
                                }
                                """.formatted(bankId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bankId").value(bankId))
                .andExpect(jsonPath("$.data.status").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int financeId = objectMapper.readTree(financeResponse).path("data").path("financeId").asInt();

        mockMvc.perform(patch("/api/finance/applications/{financeId}/status", financeId)
                        .with(role("BANK"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": 1,
                                  "remark": "approved"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.remark").value("approved"));

        mockMvc.perform(delete("/api/finance/applications/{financeId}", financeId).with(role("BANK")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/finance/intentions/{id}", intentionId).with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/finance/banks/{bankId}", bankId).with(role("BANK")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void expertKnowledgeAndConsultationWriteInterfacesWork() throws Exception {
        mockMvc.perform(post("/api/experts")
                        .with(role("EXPERT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "expert002",
                                  "realName": "Wang Expert",
                                  "phone": "13800000032",
                                  "profession": "soil improvement",
                                  "position": "agronomist",
                                  "belong": "Jishou Service Center"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("expert002"))
                .andExpect(jsonPath("$.data.profession").value("soil improvement"));

        String knowledgeResponse = mockMvc.perform(post("/api/knowledge")
                        .with(role("EXPERT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Soil care",
                                  "content": "Improve soil organic matter.",
                                  "picPath": "/files/soil.png",
                                  "ownName": "expert002"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Soil care"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int knowledgeId = objectMapper.readTree(knowledgeResponse).path("data").path("knowledgeId").asInt();

        String discussResponse = mockMvc.perform(post("/api/knowledge/{knowledgeId}/discusses", knowledgeId)
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownName": "farmer001",
                                  "content": "Helpful knowledge."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.knowledgeId").value(knowledgeId))
                .andExpect(jsonPath("$.data.content").value("Helpful knowledge."))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int discussId = objectMapper.readTree(discussResponse).path("data").path("discussId").asInt();

        String questionResponse = mockMvc.perform(post("/api/consultation/questions")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "expertName": "expert002",
                                  "questioner": "farmer001",
                                  "phone": "13800000001",
                                  "plantName": "orange",
                                  "title": "Fruit drop",
                                  "question": "How to reduce fruit drop?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int questionId = objectMapper.readTree(questionResponse).path("data").path("id").asInt();

        mockMvc.perform(patch("/api/consultation/questions/{id}/answer", questionId)
                        .with(role("EXPERT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "answer": "Improve watering and nutrition.",
                                  "status": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value("Improve watering and nutrition."))
                .andExpect(jsonPath("$.data.status").value(1));

        String reserveResponse = mockMvc.perform(post("/api/consultation/reserves")
                        .with(role("FARMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "expertName": "expert002",
                                  "questioner": "farmer001",
                                  "area": "15 mu",
                                  "address": "Jishou",
                                  "plantName": "orange",
                                  "soilCondition": "low organic matter",
                                  "plantCondition": "fruit drop",
                                  "plantDetail": "young orchard",
                                  "phone": "13800000001",
                                  "message": "Need visit"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int reserveId = objectMapper.readTree(reserveResponse).path("data").path("id").asInt();

        mockMvc.perform(patch("/api/consultation/reserves/{id}/answer", reserveId)
                        .with(role("EXPERT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "answer": "Visit arranged.",
                                  "status": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value("Visit arranged."))
                .andExpect(jsonPath("$.data.status").value(1));

        mockMvc.perform(delete("/api/consultation/reserves/{id}", reserveId).with(role("EXPERT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/consultation/questions/{id}", questionId).with(role("EXPERT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/knowledge/{knowledgeId}/discusses/{discussId}", knowledgeId, discussId)
                        .with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/knowledge/{knowledgeId}", knowledgeId).with(role("EXPERT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(delete("/api/experts/expert002").with(role("EXPERT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void openApiDocumentationIncludesAllImplementedEndpoints() throws Exception {
        String apiDocs = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode paths = objectMapper.readTree(apiDocs).path("paths");
        Set<String> documentedPaths = new TreeSet<>();
        paths.fieldNames().forEachRemaining(documentedPaths::add);

        assertThat(documentedPaths).containsExactlyInAnyOrder(
                "/api/addresses",
                "/api/addresses/{id}",
                "/api/addresses/owners/{ownName}",
                "/api/admin/finance/applications",
                "/api/admin/knowledge",
                "/api/admin/knowledge/{knowledgeId}",
                "/api/admin/knowledge/{knowledgeId}/status",
                "/api/admin/finance/applications/{financeId}/status",
                "/api/admin/overview",
                "/api/admin/trade/orders",
                "/api/admin/trade/orders/{orderId}/status",
                "/api/admin/trade/purchases",
                "/api/admin/trade/purchases/{purchaseId}/status",
                "/api/admin/users",
                "/api/admin/users/{userName}/role",
                "/api/auth/login",
                "/api/auth/register",
                "/api/consultation/questions",
                "/api/consultation/questions/{id}",
                "/api/consultation/questions/{id}/answer",
                "/api/consultation/reserves",
                "/api/consultation/reserves/{id}",
                "/api/consultation/reserves/{id}/answer",
                "/api/consultation/reserves/{id}/status",
                "/api/experts",
                "/api/experts/{userName}",
                "/api/files/images",
                "/api/finance/applications",
                "/api/finance/applications/{financeId}",
                "/api/finance/applications/{financeId}/materials",
                "/api/finance/applications/{financeId}/status",
                "/api/finance/banks",
                "/api/finance/banks/{bankId}",
                "/api/finance/banks/matches",
                "/api/finance/intentions",
                "/api/finance/intentions/{id}",
                "/api/finance/matches/farmers/{bankId}",
                "/api/knowledge",
                "/api/knowledge/{knowledgeId}",
                "/api/knowledge/{knowledgeId}/discusses",
                "/api/knowledge/{knowledgeId}/discusses/{discussId}",
                "/api/system/health",
                "/api/system/roles",
                "/api/trade/orders",
                "/api/trade/orders/cooperators/{cooperationName}",
                "/api/trade/orders/{orderId}",
                "/api/trade/orders/{orderId}/status",
                "/api/trade/orders/owners/{ownName}",
                "/api/trade/purchases",
                "/api/trade/purchases/{purchaseId}",
                "/api/trade/purchases/{purchaseId}/details",
                "/api/trade/purchases/{purchaseId}/status",
                "/api/trade/purchases/owners/{ownName}",
                "/api/trade/shopping-cart",
                "/api/trade/shopping-cart/{shoppingId}",
                "/api/trade/shopping-cart/owners/{ownName}",
                "/api/users",
                "/api/users/{userName}",
                "/api/users/{userName}/password"
        );
    }

    @Test
    void userInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/users").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userName").value("farmer001"))
                .andExpect(jsonPath("$.data[0].nickName").value("Farmer One"))
                .andExpect(jsonPath("$.data[0].identityNum").value("430000199001010001"));

        mockMvc.perform(get("/api/users/farmer001").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName").value("farmer001"))
                .andExpect(jsonPath("$.data.role").value("FARMER"))
                .andExpect(jsonPath("$.data.realName").value("Zhang San"));
    }

    @Test
    void bankInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/finance/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].bankId").value(1001))
                .andExpect(jsonPath("$.data[0].bankName").value("Agri Bank"))
                .andExpect(jsonPath("$.data[0].bankPhone").value("07430000001"));

        mockMvc.perform(get("/api/finance/banks/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bankId").value(1001))
                .andExpect(jsonPath("$.data.money").value(100000.00))
                .andExpect(jsonPath("$.data.rate").value(3.50));
    }

    @Test
    void knowledgeInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/knowledge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].knowledgeId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Rice planting"))
                .andExpect(jsonPath("$.data[0].picPath").value("/files/rice.png"));

        mockMvc.perform(get("/api/knowledge/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.knowledgeId").value(1))
                .andExpect(jsonPath("$.data.content").value("Water and fertilizer management"))
                .andExpect(jsonPath("$.data.ownName").value("expert001"));
    }

    @Test
    void tradeOrderInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/trade/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].orderId").value(1))
                .andExpect(jsonPath("$.data[0].orderStatus").value(0))
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"));

        mockMvc.perform(get("/api/trade/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.title").value("Fresh oranges"))
                .andExpect(jsonPath("$.data.address").value("Jishou"));
    }

    @Test
    void questionInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/consultation/questions").with(role("EXPERT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].expertName").value("expert001"))
                .andExpect(jsonPath("$.data[0].plantName").value("rice"));

        mockMvc.perform(get("/api/consultation/questions/1").with(role("EXPERT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Leaf yellowing"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    void addressAndExpertInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/addresses").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"))
                .andExpect(jsonPath("$.data[0].addressDetail").value("Jishou address detail"));

        mockMvc.perform(get("/api/addresses/1").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.consignee").value("Zhang San"))
                .andExpect(jsonPath("$.data.isDefault").value(1));

        mockMvc.perform(get("/api/experts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userName").value("expert001"))
                .andExpect(jsonPath("$.data[0].profession").value("plant protection"));

        mockMvc.perform(get("/api/experts/expert001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.realName").value("Li Expert"))
                .andExpect(jsonPath("$.data.belong").value("Jishou University"));
    }

    @Test
    void financeApplicationAndIntentionInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/finance/applications").with(role("BANK")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].financeId").value(1))
                .andExpect(jsonPath("$.data[0].bankId").value(1001))
                .andExpect(jsonPath("$.data[0].idNum").value("430000199001010001"));

        mockMvc.perform(get("/api/finance/applications/1").with(role("BANK")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.money").value(50000.00))
                .andExpect(jsonPath("$.data.combinationName1").value("Li Si"))
                .andExpect(jsonPath("$.data.fileInfo").value("/files/finance.pdf"));

        mockMvc.perform(get("/api/finance/intentions").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].userName").value("farmer001"))
                .andExpect(jsonPath("$.data[0].repaymentPeriod").value("12 months"));

        mockMvc.perform(get("/api/finance/intentions/1").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.application").value("expand production"))
                .andExpect(jsonPath("$.data.item").value("orange orchard"));
    }

    @Test
    void purchaseAndShoppingCartInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/trade/purchases").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].purchaseId").value(1))
                .andExpect(jsonPath("$.data[0].purchaseType").value(1))
                .andExpect(jsonPath("$.data[0].purchaseStatus").value(0));

        mockMvc.perform(get("/api/trade/purchases/1").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.purchaseId").value(1))
                .andExpect(jsonPath("$.data.totalPrice").value(65.00))
                .andExpect(jsonPath("$.data.address").value("Jishou"));

        mockMvc.perform(get("/api/trade/purchases/1/details").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].detailId").value(1))
                .andExpect(jsonPath("$.data[0].uninPrice").value(6.50))
                .andExpect(jsonPath("$.data[0].count").value(10));

        mockMvc.perform(get("/api/trade/shopping-cart").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].shoppingId").value(1))
                .andExpect(jsonPath("$.data[0].ownName").value("farmer001"));

        mockMvc.perform(get("/api/trade/shopping-cart/1").with(role("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.count").value(3));
    }

    @Test
    void discussAndReserveInterfacesReturnDocumentedFields() throws Exception {
        mockMvc.perform(get("/api/knowledge/1/discusses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].discussId").value(1))
                .andExpect(jsonPath("$.data[0].knowledgeId").value(1))
                .andExpect(jsonPath("$.data[0].content").value("Useful article"));

        mockMvc.perform(get("/api/consultation/reserves").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].soilCondition").value("acid soil"))
                .andExpect(jsonPath("$.data[0].plantCondition").value("leaf yellowing"));

        mockMvc.perform(get("/api/consultation/reserves/1").with(role("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.plantDetail").value("seedling stage"))
                .andExpect(jsonPath("$.data.message").value("Need onsite guidance"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    private RequestPostProcessor role(String role) {
        return request -> {
            request.addHeader("X-User-Role", role);
            return request;
        };
    }
}
