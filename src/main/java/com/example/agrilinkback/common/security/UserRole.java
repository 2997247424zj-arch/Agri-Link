package com.example.agrilinkback.common.security;

import java.util.Arrays;
import java.util.Optional;

/**
 * 平台用户角色枚举，定义了农产品融销一体化平台中所有参与者的身份类型。
 *
 * <p>该枚举封装了角色的唯一编码（用于数据库存储和API传输）、
 * 中文显示标签（用于前端UI展示）以及Spring Security权限标识符。</p>
 *
 * <p><b>角色体系说明：</b></p>
 * <ul>
 *   <li><b>BUYER</b> — 买家：浏览、订购农产品，参与交易流程</li>
 *   <li><b>FARMER</b> — 农户：发布农产品信息，管理供货与订单</li>
 *   <li><b>EXPERT</b> — 技术专家：提供农业技术咨询与指导服务</li>
 *   <li><b>BANK</b> — 银行：提供金融贷款、资金结算等服务</li>
 *   <li><b>SYSTEM_ADMIN</b> — 系统管理员：平台全局管理，拥有最高权限</li>
 * </ul>
 *
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>用户实体中存储角色 {@code code} 字段</li>
 *   <li>Spring Security认证时生成 {@code authority()} 权限字符串（格式：{@code ROLE_XXX}）</li>
 *   <li>{@code fromCode(String)} 用于将数据库存储的角色编码反向解析为枚举实例</li>
 *   <li>{@code isBusinessRole()} 用于区分业务角色与系统管理角色</li>
 * </ul>
 *
 * <p><b>编码映射规则：</b></p>
 * 通过 {@code fromCode} 解析时，字符串 {@code "ADMIN"} 会映射为 {@code SYSTEM_ADMIN}，
 * {@code "USER"} 会映射为 {@code BUYER}，以兼容历史数据或简化前端传参。
 *
 * @author Agri-Link 开发团队
 * @since 1.0
 * @see com.example.agrilinkback.domain.user.User
 */
public enum UserRole {

    /** 买家 — 浏览和购买农产品，参与平台交易 */
    BUYER("BUYER", "买家"),

    /** 农户 — 发布农产品信息，管理供货与订单履约 */
    FARMER("FARMER", "农户"),

    /** 技术专家 — 提供农业技术咨询与指导服务 */
    EXPERT("EXPERT", "技术专家"),

    /** 银行 — 提供贷款、资金结算、保险等金融服务 */
    BANK("BANK", "银行"),

    /** 系统管理员 — 拥有平台最高管理权限，负责全局配置与维护 */
    SYSTEM_ADMIN("SYSTEM_ADMIN", "系统管理员");

    /**
     * 角色的唯一编码标识，用于数据库存储和API交互。
     * <p>该字段区分大小写，通常为大写英文字符串。</p>
     */
    private final String code;

    /**
     * 角色的中文显示名称，用于前端UI渲染和报表展示。
     */
    private final String label;

    /**
     * 构造角色枚举常量。
     *
     * @param code  角色编码，用于系统内部标识
     * @param label 角色中文名称，用于前端展示
     */
    UserRole(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取角色的唯一编码标识。
     *
     * @return 角色编码字符串（如 {@code "BUYER"}、{@code "FARMER"} 等）
     */
    public String code() {
        return code;
    }

    /**
     * 获取角色的中文显示标签。
     *
     * @return 角色中文名称字符串（如 {@code "买家"}、{@code "农户"} 等）
     */
    public String label() {
        return label;
    }

    /**
     * 获取Spring Security权限标识符。
     *
     * <p>返回格式为 {@code "ROLE_"} 前缀拼接角色编码，
     * 例如买家返回 {@code "ROLE_BUYER"}。
     * 该值可直接用于 {@code @PreAuthorize}、{@code hasRole()} 等Spring Security注解和表达式。</p>
     *
     * @return Spring Security兼容的权限字符串
     */
    public String authority() {
        return "ROLE_" + code;
    }

    /**
     * 判断当前角色是否为业务角色（非系统管理员）。
     *
     * <p>业务角色包括买家、农户、技术专家和银行。
     * 该方法用于在业务逻辑中快速判定当前角色是否参与业务流转，
     * 而非执行系统管理任务。</p>
     *
     * @return {@code true} 如果当前角色不是 {@code SYSTEM_ADMIN}；
     *         {@code false} 如果当前角色是系统管理员
     */
    public boolean isBusinessRole() {
        return this != SYSTEM_ADMIN;
    }

    /**
     * 根据角色编码字符串查找对应的枚举实例。
     *
     * <p>该方法提供了一层编码容错与兼容映射：</p>
     * <ul>
     *   <li>输入为 {@code null} 或空白字符串时，返回 {@code Optional.empty()}</li>
     *   <li>输入经过去空白和 {@code toUpperCase()} 标准化处理</li>
     *   <li>字符 {@code "ADMIN"} 会映射为 {@code SYSTEM_ADMIN} 的编码，
     *       以兼容外部系统或历史数据使用简短编码的场景</li>
     *   <li>字符 {@code "USER"} 会映射为 {@code BUYER} 的编码，
     *       以兼容将普通用户简称为 {@code "USER"} 的传参习惯</li>
     *   <li>其他值按标准化后的字符串与各角色 {@code code} 字段精确匹配</li>
     * </ul>
     *
     * <p>底层实现通过 {@code Arrays.stream(values())} 流式遍历所有枚举常量，
     * 并取第一个 {@code code} 匹配的实例。</p>
     *
     * @param code 待解析的角色编码字符串，可为 {@code null}
     * @return 匹配成功的 {@link Optional}{@code <UserRole>} 实例；
     *         未匹配到对应角色时返回 {@code Optional.empty()}
     */
    public static Optional<UserRole> fromCode(String code) {
        // 空值或空白输入直接返回空Optional，避免后续无意义的流匹配操作
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        // 标准化输入：去除前后空白并统一转为大写
        String normalized = code.trim().toUpperCase();
        // 兼容映射：将“ADMIN”和“USER”简短编码映射到平台标准角色编码
        String roleCode = switch (normalized) {
            case "ADMIN" -> SYSTEM_ADMIN.code;
            case "USER" -> BUYER.code;
            default -> normalized;
        };
        // 遍历所有枚举值，匹配code字段后返回第一个结果
        return Arrays.stream(values())
                .filter(role -> role.code.equals(roleCode))
                .findFirst();
    }
}