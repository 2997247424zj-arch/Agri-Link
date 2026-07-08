package com.example.agrilinkback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring配置类，用于定制化OpenAPI（Swagger/SpringDoc）文档的基本信息。
 *
 * <p>该类通过{@link Configuration @Configuration}注解声明为一个Spring配置源，
 * 并在应用启动时向容器中注册一个{@link OpenAPI} Bean。SpringDoc（基于OpenAPI 3规范）
 * 会自动检测该Bean，并使用其定义的标题、描述和版本信息生成在线API文档界面
 * （如Swagger UI），方便前后端开发人员查阅和调试接口。</p>
 *
 * <p>主要职责包括：</p>
 * <ul>
 *   <li>定义API文档的标题（{@code Agri-Link API}），用于标识整个平台的接口集合。</li>
 *   <li>提供API的简要描述，说明该后端服务于农产品融资与销售一体化平台。</li>
 *   <li>声明API版本号（{@code 0.0.1}），便于版本管理和迭代追踪。</li>
 * </ul>
 *
 * <p>如果需要扩展文档配置（例如添加联系人信息、许可证、服务器地址、安全方案等），
 * 可以直接在该类中补充相应的{@link Info}或{@link OpenAPI}字段。</p>
 *
 * @author Agri-Link Team
 * @version 0.0.1
 * @see OpenAPI
 * @see Info
 * @since 0.0.1
 */
@Configuration
public class OpenApiConfig {

    /**
     * 向Spring容器注册一个自定义的{@link OpenAPI} Bean，
     * 用于配置SpringDoc生成的API文档元数据。
     *
     * <p>该方法返回一个{@link OpenAPI}实例，其中包含：</p>
     * <ul>
     *   <li><b>标题 (title):</b> "Agri-Link API" — 对应农产品融销一体平台的API集合名称。</li>
     *   <li><b>描述 (description):</b> 简要说明该后端服务为农产品融资与销售平台提供接口支持。</li>
     *   <li><b>版本 (version):</b> "0.0.1" — 当前API的初始开发版本号。</li>
     * </ul>
     *
     * <p>SpringDoc框架在启动时会自动扫描该Bean，
     * 并将其信息渲染到Swagger UI和OpenAPI JSON端点（如{@code /v3/api-docs}）中。</p>
     *
     * <p>Bean名称默认为方法名{@code agriLinkOpenAPI}，
     * 如有需要可通过{@code @Bean("customName")}显式指定。</p>
     *
     * @return 包含Agri-Link平台API元数据的{@link OpenAPI}对象
     */
    @Bean
    public OpenAPI agriLinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agri-Link API")
                        .description("Backend API for the agricultural financing and sales platform.")
                        .version("0.0.1"));
    }
}
