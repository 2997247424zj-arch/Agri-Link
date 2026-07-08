package com.example.agrilinkback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AgriLink平台后端Spring Boot应用程序的主入口类。
 *
 * <p>该类是整个农产品融销一体平台后端服务的启动引导类（Bootstrap Class）。
 * 它负责：</p>
 * <ul>
 *   <li>通过 {@link SpringApplication#run(Class, String...)} 启动内嵌的Web服务器（默认Tomcat）；</li>
 *   <li>触发Spring Boot的自动配置机制，完成数据源、事务管理、Web MVC等基础设施的装配；</li>
 *   <li>扫描并注册MyBatis-Plus的Mapper接口，建立数据库表与Java对象的映射关系。</li>
 * </ul>
 *
 * <p>关键注解说明：</p>
 * <ul>
 *   <li>{@link SpringBootApplication @SpringBootApplication} —— 组合了
 *       {@code @SpringBootConfiguration}、{@code @EnableAutoConfiguration} 和
 *       {@code @ComponentScan}，实现组件的自动扫描与配置；</li>
 *   <li>{@link MapperScan @MapperScan}("com.example.agrilinkback.module") ——
 *       指定MyBatis-Plus要扫描的Mapper接口包路径，所有位于该包（及其子包）下的Mapper接口
 *       将被自动注册为Spring Bean，无需在每个Mapper上单独标注{@code @Mapper}。</li>
 * </ul>
 *
 * <p>该平台基于数字经济技术，围绕农产品的融资与销售一体化场景设计，
 * 为农户、采购商、金融机构等角色提供统一的服务入口。</p>
 *
 * @author AgriLink开发团队
 * @version 1.0
 * @since 1.0
 * @see SpringBootApplication
 * @see MapperScan
 */
@MapperScan("com.example.agrilinkback.module")
@SpringBootApplication
public class AgriLinkBackApplication {

    /**
     * 应用程序的入口主方法，启动Spring Boot应用上下文并初始化所有服务组件。
     *
     * <p>该方法通过 {@link SpringApplication#run(Class, String...)} 执行以下核心流程：</p>
     * <ol>
     *   <li>创建并配置 {@link org.springframework.context.ApplicationContext ApplicationContext}；</li>
     *   <li>触发自动配置（Auto-configuration），根据classpath中的依赖自动装配Bean；</li>
     *   <li>启动内嵌的Servlet容器（默认Tomcat），绑定监听端口并对外提供服务；</li>
     *   <li>初始化所有Spring管理的Bean，包括服务层、控制器层、数据访问层以及中间件连接等。</li>
     * </ol>
     *
     * @param args 命令行参数，由JVM传入，可传递给Spring Boot用于覆盖默认配置
     *             （例如 {@code --server.port=9090} 可修改服务端口号）
     */
    public static void main(String[] args) {
        // 启动Spring Boot应用，加载当前类作为配置源并执行自动装配
        SpringApplication.run(AgriLinkBackApplication.class, args);
    }

}