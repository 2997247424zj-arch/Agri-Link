# Agri-Link 任务交接记录 2026-07-07

## 当前目标

继续完善“基于数字经济的农产品融销一体平台”的前后端业务闭环。前端已经完成主要业务页面扩展，本轮重点是把前端新增字段和接口路径对齐到 Spring Boot 后端。

## 项目位置

前端：

```text
A:\基于数字经济的农产品融销一体平台(Spring Boot ,Vue3)\项目源码\项目前端\Agri-Link
```

后端：

```text
A:\基于数字经济的农产品融销一体平台(Spring Boot ,Vue3)\项目源码\项目后端\Agri-Link-main
```

## 本轮已完成的后端改动

### 交易货源

- `TradeOrder`、`TradeOrderRequest` 增加 `stock`、`spec`、`unit`、`minPurchase`。
- `TradeOrderService` 创建/编辑货源时写入上述字段。
- `TradeOrderMapper` 查询、插入、更新 SQL 增加 `stock`、`spec`、`unit`、`min_purchase`。

### 采购订单

- `Purchase` 增加 `cancelReason`、`deliveryNo`。
- `PurchaseStatusRequest` 增加 `cancelReason`、`deliveryNo`。
- `PurchaseService` 状态更新时同步取消原因和物流号。
- `PurchaseMapper` 查询、插入、更新 SQL 增加 `cancel_reason`、`delivery_no`。

### 融资申请

- `Finance` 增加内部字段 `materialText`，并通过 JSON 输出 `materials: string[]`。
- 新增 `FinanceMaterialsRequest`。
- `FinanceService` 增加 `updateFinanceMaterials`。
- `FinanceController` 新增 `PATCH /api/finance/applications/{financeId}/materials`。
- `FinanceMapper` 增加 `materials` 查询、插入和更新 SQL。

### 专家问答

- `Question` 增加内部字段 `attachmentsText`，并通过 JSON 输出 `attachments: string[]`。
- `QuestionRequest` 增加 `attachments`。
- `QuestionService` 创建问答时保存附件列表。
- `QuestionMapper` 查询和插入 SQL 增加 `attachments`。

### 专家预约

- `Reserve`、`ReserveRequest` 增加 `appointmentTime`、`serviceMode`。
- 新增 `ReserveStatusRequest`。
- `ReserveService` 增加 `updateReserveStatus`。
- `ReserveController` 新增 `PATCH /api/consultation/reserves/{id}/status`。
- `ReserveMapper` 查询、插入 SQL 增加 `appointment_time`、`service_mode`，并新增只更新状态的 SQL。

### 后台知识库

- 新增后台适配 DTO：
  - `AdminKnowledgeItem`
  - `AdminKnowledgeRequest`
  - `AdminKnowledgeStatusRequest`
- `AdminService` 增加知识库列表、创建、编辑、状态兼容更新、删除方法。
- `AdminController` 新增 `/api/admin/knowledge` 系列接口：
  - `GET /api/admin/knowledge`
  - `POST /api/admin/knowledge`
  - `PATCH /api/admin/knowledge/{knowledgeId}/status`
  - `PUT /api/admin/knowledge/{knowledgeId}`
  - `DELETE /api/admin/knowledge/{knowledgeId}`
- 注意：原 `tb_knowledge` 没有状态列，后台状态接口当前只是兼容前端调用并返回当前知识项，不持久化上下架状态。

### 数据库脚本

- `src/test/resources/schema-test.sql` 已同步新增字段。
- `rongxiaotong.sql` 已同步新增可空字段，历史 insert 仍可按原列清单导入。

## 本轮涉及的后端文件

```text
src/main/java/com/example/agrilinkback/module/trade/entity/TradeOrder.java
src/main/java/com/example/agrilinkback/module/trade/dto/TradeOrderRequest.java
src/main/java/com/example/agrilinkback/module/trade/service/TradeOrderService.java
src/main/java/com/example/agrilinkback/module/trade/mapper/TradeOrderMapper.java
src/main/java/com/example/agrilinkback/module/trade/entity/Purchase.java
src/main/java/com/example/agrilinkback/module/trade/dto/PurchaseStatusRequest.java
src/main/java/com/example/agrilinkback/module/trade/service/PurchaseService.java
src/main/java/com/example/agrilinkback/module/trade/mapper/PurchaseMapper.java
src/main/java/com/example/agrilinkback/module/finance/entity/Finance.java
src/main/java/com/example/agrilinkback/module/finance/dto/FinanceMaterialsRequest.java
src/main/java/com/example/agrilinkback/module/finance/service/FinanceService.java
src/main/java/com/example/agrilinkback/module/finance/controller/FinanceController.java
src/main/java/com/example/agrilinkback/module/finance/mapper/FinanceMapper.java
src/main/java/com/example/agrilinkback/module/consultation/entity/Question.java
src/main/java/com/example/agrilinkback/module/consultation/dto/QuestionRequest.java
src/main/java/com/example/agrilinkback/module/consultation/service/QuestionService.java
src/main/java/com/example/agrilinkback/module/consultation/mapper/QuestionMapper.java
src/main/java/com/example/agrilinkback/module/consultation/entity/Reserve.java
src/main/java/com/example/agrilinkback/module/consultation/dto/ReserveRequest.java
src/main/java/com/example/agrilinkback/module/consultation/dto/ReserveStatusRequest.java
src/main/java/com/example/agrilinkback/module/consultation/service/ReserveService.java
src/main/java/com/example/agrilinkback/module/consultation/controller/ReserveController.java
src/main/java/com/example/agrilinkback/module/consultation/mapper/ReserveMapper.java
src/main/java/com/example/agrilinkback/module/admin/dto/AdminKnowledgeItem.java
src/main/java/com/example/agrilinkback/module/admin/dto/AdminKnowledgeRequest.java
src/main/java/com/example/agrilinkback/module/admin/dto/AdminKnowledgeStatusRequest.java
src/main/java/com/example/agrilinkback/module/admin/service/AdminService.java
src/main/java/com/example/agrilinkback/module/admin/controller/AdminController.java
src/main/java/com/example/agrilinkback/module/knowledge/service/KnowledgeService.java
src/test/resources/schema-test.sql
rongxiaotong.sql
```

## 尚未验证

本轮后端改动尚未运行后端测试或编译。原因：用户中断继续执行并要求保存当前进度。

下次优先在后端目录运行：

```powershell
cmd /c mvnw.cmd test
```

如果失败，优先检查：

- record 构造器参数数量是否还有遗漏；
- MyBatis `@Select` 字段别名是否和 record 字段一致；
- H2 测试库是否支持当前 SQL 写法；
- `AdminKnowledgeStatusRequest request` 当前只用于兼容接口，若触发检查问题可在服务层读取 `request.status()`。

后端通过后，再回到前端目录运行：

```powershell
npm.cmd run type-check
npm.cmd run build-only
npm.cmd run test:unit -- --run
```

## 前端上一轮验证状态

此前前端已通过：

```powershell
npm.cmd run type-check
npm.cmd run build-only
npm.cmd run test:unit -- --run
```

本轮没有继续修改前端业务代码。

## 下次继续建议

1. 先跑后端 `mvnw.cmd test`，修复编译或测试问题。
2. 后端通过后，重新跑前端 type-check/build/unit test。
3. 若知识库上下架状态需要真实持久化，给 `tb_knowledge` 增加 `status` 字段并更新 `Knowledge`、`KnowledgeRequest`、`KnowledgeMapper`。
4. 若采购列表需要直接携带明细，可新增采购聚合 DTO；当前前端仍能通过 `/api/trade/purchases/{purchaseId}/details` 展开详情。
