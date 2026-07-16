/*
 Navicat Premium Data Transfer

 Source Server         : SpringBoot
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : rongxiaotong

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 09/07/2026 17:41:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_address
-- ----------------------------
DROP TABLE IF EXISTS `tb_address`;
CREATE TABLE `tb_address`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '属于哪个用户的地址',
  `consignee` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货人',
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `address_detail` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '收货地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认，0，不是，默认是1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19004 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_address
-- ----------------------------
INSERT INTO `tb_address` VALUES (121, 'zwr', '张文瑞', '15030589961', '青岛市即墨区青岛湾琴湾C区', 1);
INSERT INTO `tb_address` VALUES (129, 'zhangxukun', '张绪昆', '18354648787', '山东省青岛市', 0);
INSERT INTO `tb_address` VALUES (146, 'zhangxu', '张绪昆', '18396833008', '山东省青岛市', 0);
INSERT INTO `tb_address` VALUES (147, 'zhangxukun', '张绪昆', '18354648787', '山东省济南市', 1);
INSERT INTO `tb_address` VALUES (153, 'gaoge', '高歌', '18977771439', '山东省枣庄市', 0);
INSERT INTO `tb_address` VALUES (154, 'gaoge', '高歌', '18977771439', '山东省德州市夏津县', 1);
INSERT INTO `tb_address` VALUES (171, 'gaoge', '高歌', '18977771439', '山东省德州市夏津县', 0);
INSERT INTO `tb_address` VALUES (172, 'admin', '成吉思汗', '18766661438', '山东青岛市崂山区', 1);
INSERT INTO `tb_address` VALUES (173, 'admin', '不朽大帝', '18766661438', '山东青岛市崂山区', 0);
INSERT INTO `tb_address` VALUES (178, 'lzh', '11', '11', '11', 1);
INSERT INTO `tb_address` VALUES (184, 'wyn3', '李增虎', '11111111111', '海信财智谷', 0);
INSERT INTO `tb_address` VALUES (186, 'lisi', '李四', '15623652365', '山东省临沂市河东区', 1);
INSERT INTO `tb_address` VALUES (187, 'lisi', '李四', '15623652365', '山东省青岛市崂山区', 0);
INSERT INTO `tb_address` VALUES (188, 'lisi', '李四', '15662352365', '山东省青岛市黄岛区', 0);
INSERT INTO `tb_address` VALUES (189, 'wangya', '王娅', '13792449255', '青岛市李沧区', 0);
INSERT INTO `tb_address` VALUES (190, 'admin001', 'aaa', '15266884488', 'aaa', 0);
INSERT INTO `tb_address` VALUES (19001, 'dev_buyer', 'Chen Buyer', '13800009002', 'Changsha Yuhua Agri Purchase Warehouse A1', 1);
INSERT INTO `tb_address` VALUES (19002, 'dev_buyer', 'Chen Buyer', '13800009002', 'Jishou Cold Chain Distribution Center B2', 0);
INSERT INTO `tb_address` VALUES (19003, 'dev_farmer', 'Shi Xiaoman', '13800009001', 'Shibadong Kiwi Demonstration Base', 1);

-- ----------------------------
-- Table structure for tb_bank
-- ----------------------------
DROP TABLE IF EXISTS `tb_bank`;
CREATE TABLE `tb_bank`  (
  `bank_id` int NOT NULL AUTO_INCREMENT,
  `bank_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `introduce` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `bank_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `money` decimal(65, 2) NOT NULL,
  `rate` decimal(65, 2) NOT NULL,
  `repayment` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`bank_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_bank
-- ----------------------------
INSERT INTO `tb_bank` VALUES (1001, '青岛银行', '青易贷，是青岛银行小微企业融资产品服务品牌。为助力小微企业成长发展，青岛银行为广大小微企业打造全面金融服务品牌体系——“青易贷”，紧贴小微企业融资需求特点，紧跟市场经济发展变化，不断创新完善融资产品，形成特色产品体系，力争全方位地满足不同行业、不同成长阶段的小微企业各类融资需求。', '96588', 100000.00, 1.10, 24);
INSERT INTO `tb_bank` VALUES (1002, '中国银行', '农业贷，中国银行金融市场业务，包括国内、国际本外币金融市场相关交易、投资、理财、托管等业务，为公司、个人以及金融同业提供全面、完善、专业的各项金融市场服务。', '95566', 100000.00, 1.20, 36);
INSERT INTO `tb_bank` VALUES (1004, '中国工商银行', '助农贷，工商银行向小微企业主发放的，用于满足其生产经营资金需求或置换生产经营过程中产生的负债性资金的人民币贷款。', '95588', 150000.00, 1.10, 30);
INSERT INTO `tb_bank` VALUES (1005, '日照银行', '阳光贷，“阳光”系列个人消费贷款业务是日照银行向符合规定条件的自然人发放的用于个人消费用途的人民币贷款业务，旨在以快捷方便的审批流程和优质的服务满足客户在大件商品或服务消费等个人资金需求，如住房装修、购家具家电、购车、婚庆、学习进修、旅游等。“阳光”系列个人消费贷款业务包含“阳光贷”、“金领贷”、“阳光·房易贷”、“阳光·保易贷”、“阳光·车易贷”“阳光·出国贷”等。', '96588', 100000.00, 2.00, 24);
INSERT INTO `tb_bank` VALUES (1006, '华夏银行', '华夏贷，华夏银行作为债务融资工具全国首批主承销商之一，已从业十余载，连续十三年获“全国银行间同业拆借中心优秀交易成员”称号，承销团队经验丰富，专业高效，竭诚为广大企业服务。', '95577', 160000.00, 1.80, 36);
INSERT INTO `tb_bank` VALUES (1007, '中国建设银行', '诚贷通，小额无抵押贷款“诚贷通”小额无抵押贷款是建设银行为小企业客户发放，由企业主或企业实际控制人提供个人连带责任保证，无需抵（质）押物，用于小企业客户生产经营资金周转的人民币循环额度贷款。', '95533', 200000.00, 3.60, 36);
INSERT INTO `tb_bank` VALUES (1008, '浦发银行', '点贷，点贷是我行面向符合条件的客户通过互联网在线受理、在线签约，并即时发放贷款的业务模式。', '95528', 150000.00, 1.10, 24);
INSERT INTO `tb_bank` VALUES (1009, '中国平安银行', '数保贷，平安银行与担保公司、担保基金、保险公司等联合开发的面向诚信纳税的中小企业法人或企业主的互联网信用贷款', '95511', 160000.00, 1.20, 36);
INSERT INTO `tb_bank` VALUES (1010, '中国民生银行', '农贷通，为进一步丰富金融服务乡村振兴的内涵，民生银行在持续迭代“农贷通”产品的基础上，通过与中国银联强强联合，将借记卡产品与贷款产品相融合，以科技赋能，将现代金融服务延伸到广大农村区域。“农贷通”卡满载10项特色涉农权益，为农户提供多样化增值服务。', '95568', 200000.00, 1.60, 24);
INSERT INTO `tb_bank` VALUES (19001, 'Xiangxi AgriBank Dev Branch', 'Credit loan for spring farming, seed, fertilizer, cold-chain and order-based cash flow.', '0743-1900190', 300000.00, 3.85, 12);
INSERT INTO `tb_bank` VALUES (19002, 'Jishou Inclusive Finance Dev Branch', 'Short-term working capital loan for green agricultural product purchase orders.', '0743-1900290', 150000.00, 4.20, 6);

-- ----------------------------
-- Table structure for tb_bank_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_bank_user`;
CREATE TABLE `tb_bank_user`  (
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `bank_id` int NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`user_name`) USING BTREE,
  INDEX `bank_id`(`bank_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_bank_user
-- ----------------------------
INSERT INTO `tb_bank_user` VALUES ('buser01', 1001, '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '王家明', '13236963696', 'BANK', '2022-04-02 16:19:10', '2022-04-02 16:19:13');
INSERT INTO `tb_bank_user` VALUES ('buser02', 1006, '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '刘璐', '13236963696', 'BANK', '2022-04-02 16:19:10', '2022-04-02 16:19:13');
INSERT INTO `tb_bank_user` VALUES ('buser03', 1001, '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '李涵', '13236963696', 'BANK', '2022-04-02 16:19:10', '2022-04-02 16:19:13');
INSERT INTO `tb_bank_user` VALUES ('dev_bank', 19001, '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Lin Manager', '13800009004', 'BANK', '2026-07-08 11:34:29', '2026-07-08 11:34:29');

-- ----------------------------
-- Table structure for tb_discuss
-- ----------------------------
DROP TABLE IF EXISTS `tb_discuss`;
CREATE TABLE `tb_discuss`  (
  `discuss_id` int NOT NULL AUTO_INCREMENT,
  `knowledge_id` int NOT NULL,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`discuss_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_discuss
-- ----------------------------
INSERT INTO `tb_discuss` VALUES (100, 100, 'wyn', '评论内容123', '2022-03-29 14:35:06');
INSERT INTO `tb_discuss` VALUES (101, 22, 'zhangxukun', '1111', '2022-07-22 09:56:49');
INSERT INTO `tb_discuss` VALUES (102, 22, 'admin', '22', '2022-07-29 14:59:21');
INSERT INTO `tb_discuss` VALUES (103, 22, 'admin', '44444444', '2022-07-29 15:31:06');
INSERT INTO `tb_discuss` VALUES (104, 22, 'wyn3', '22', '2022-08-19 16:09:12');
INSERT INTO `tb_discuss` VALUES (105, 23, 'gaoge', '不错不错', '2022-08-31 16:24:10');
INSERT INTO `tb_discuss` VALUES (106, 23, 'gaoge', '学到了，感谢！', '2022-08-31 16:24:18');
INSERT INTO `tb_discuss` VALUES (107, 23, 'gaoge', '长知识了', '2022-08-31 16:26:02');
INSERT INTO `tb_discuss` VALUES (108, 22, 'wyn3', '太简单', '2022-09-06 16:19:37');

-- ----------------------------
-- Table structure for tb_expert
-- ----------------------------
DROP TABLE IF EXISTS `tb_expert`;
CREATE TABLE `tb_expert`  (
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `profession` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `position` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `belong` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_expert
-- ----------------------------
INSERT INTO `tb_expert` VALUES ('dev_expert', 'Zhou Expert', '13800009003', 'Kiwi pest control and soil improvement', 'Senior Agronomist', 'Hunan Agricultural University');
INSERT INTO `tb_expert` VALUES ('fengj', '冯洁', '13755625577', '农业设施学', '中级工程师', '农业资源研究所');
INSERT INTO `tb_expert` VALUES ('gaoge', '高歌', '15649599256', '生物学', '高级职称', '山东省农科院');
INSERT INTO `tb_expert` VALUES ('lijj', '李家俊', '15953278854', '农学', '中级工程师', '省农科院');
INSERT INTO `tb_expert` VALUES ('liuxu', '刘旭', '13552635266', '农作物基因学', '高级工程师', '农业资源研究所');
INSERT INTO `tb_expert` VALUES ('liyiyi', '李依依', '13349277488', '农学', '副高级工程师', '县农村经营合作站');
INSERT INTO `tb_expert` VALUES ('wuhan', '吴晗', '13795929249', '农业设施学', '中级工程师', '市农田农村管理站');
INSERT INTO `tb_expert` VALUES ('wyn', '王玉娜', '17892322499', '生物化学', '高级工程师', '青岛生物研究所');
INSERT INTO `tb_expert` VALUES ('wyn2', '王艳安', '13192924932', '生物化学', '高级工程师', '青岛生物研究所');
INSERT INTO `tb_expert` VALUES ('zhangxu', '张旭', '18224995956', '生物学', '中级职称', '山东省农科院');
INSERT INTO `tb_expert` VALUES ('zhaoqm', '赵清明', '13323562356', '灾害防治', '副高级工程师', '青岛农科所');
INSERT INTO `tb_expert` VALUES ('zhengxin', '郑鑫', '15866886233', '农学', '高级工程师', '青岛农科所');

-- ----------------------------
-- Table structure for tb_finance
-- ----------------------------
DROP TABLE IF EXISTS `tb_finance`;
CREATE TABLE `tb_finance`  (
  `finance_id` int NOT NULL AUTO_INCREMENT,
  `bank_id` int NOT NULL,
  `own_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `real_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `phone` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `id_num` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `status` int NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `money` decimal(65, 2) NOT NULL,
  `rate` decimal(65, 2) NOT NULL,
  `repayment` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `combination_name1` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `combination_phone1` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `combination_idnum1` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `combination_name2` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `combination_phone2` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `combination_idnum2` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `file_info` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `materials` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  PRIMARY KEY (`finance_id`) USING BTREE,
  INDEX `bank_id`(`bank_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 142 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_finance
-- ----------------------------
INSERT INTO `tb_finance` VALUES (100, 1001, 'wyn', '王亚楠', '22222222222', '222222222222222222', 1, 're', 100.00, 12.00, 1, '2022-03-28 17:14:32', '2022-03-28 17:14:36', NULL, NULL, NULL, NULL, '', '', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (102, 1001, 'wyn', '王亚楠', '22222222222', '222222222222222222', 2, 're', 109.00, 12.00, 1, '2022-03-28 17:14:32', '2022-03-28 17:14:36', NULL, NULL, NULL, NULL, '', '', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (103, 1004, 'zhangxukun', '张旭坤', '22222222222', '222222222222222222', 0, NULL, 160000.00, 1.80, 36, '2022-07-22 09:59:12', '2022-07-22 09:59:12', '张龙', NULL, NULL, '赵虎', '22222222222', '222222222222222222', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (104, 1006, 'zhangxukun', '张旭坤', '13176879739', '370827199001142213', 2, NULL, 150000.00, 1.10, 30, '2022-07-22 10:02:04', '2022-07-22 10:02:04', NULL, NULL, NULL, NULL, '', '', 'dd4f92b790dc4f36964888c98169ce66.jpg 45f9675d11a34dd3a6df296b8bc4b7e5.jpg dd4f92b790dc4f36964888c98169ce66.jpg dd4f92b790dc4f36964888c98169ce66.jpg dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (105, 1005, 'zhangxukun', '张旭坤', '22222222222', '222222222222222222', 0, NULL, 100000.00, 2.00, 24, '2022-07-22 10:02:29', '2022-07-22 10:02:29', NULL, NULL, NULL, NULL, '', '', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (106, 1004, 'zhangxukun', '张旭坤', '22222222222', '222222222222222222', 0, NULL, 150000.00, 1.10, 30, '2022-07-22 10:26:02', '2022-07-22 10:26:02', NULL, NULL, NULL, NULL, '', '', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (133, 1006, 'lzh', '李增虎', '15621367568', '373312199801032719', 2, NULL, 60000.00, 1.00, 6, '2022-08-17 17:18:14', '2022-08-17 17:18:14', '冯德林', '22222222222', '222222222222222222', '李思', '22222222222', '222222222222222222', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (137, 1007, 'lisi', '李思', '15623652365', '371323199601062719', 0, NULL, 100000.00, 1.20, 6, '2022-08-29 14:38:27', '2022-08-29 14:38:27', '李增虎', '17814382372', '372823199005162816', '何海涵', '13176852739', '373833198910073526', 'dd4f92b790dc4f36964888c98169ce66.jpg', NULL);
INSERT INTO `tb_finance` VALUES (141, 1006, 'wyn3', '王亚楠', '13696859685', '370201196501026352', 2, NULL, 120000.00, 1.80, 36, '2022-09-08 10:12:35', '2022-09-08 10:12:35', '沙发', '13754125623', '370203199802036513', '的撒', '13895212195', '370213199009160203', '47fc92e1068d4c20833e4e197aec0b0d.jpg 20e7a0d77ecf4731b28ebc1d6ca22587.jpg ', NULL);

-- ----------------------------
-- Table structure for tb_financing_intention
-- ----------------------------
DROP TABLE IF EXISTS `tb_financing_intention`;
CREATE TABLE `tb_financing_intention`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount` int NOT NULL,
  `application` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `item` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `repayment_period` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `area` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_financing_intention
-- ----------------------------
INSERT INTO `tb_financing_intention` VALUES (5, 'zhangxukun', '张旭坤', '山东省菏泽市', 80000, '种植农作物', '苹果', 60, 90, '13176879739', '2022-08-09 14:56:31', '2022-08-10 14:56:35');
INSERT INTO `tb_financing_intention` VALUES (6, 'wyn3', '王亚楠', '威海市', 1000000, '芒果种植', '芒果', 36, 1, '13792499256', '2022-09-08 09:58:14', '2022-09-08 10:01:15');

-- ----------------------------
-- Table structure for tb_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_knowledge`;
CREATE TABLE `tb_knowledge`  (
  `knowledge_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `pic_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '发布者名字',
  `create_time` time NOT NULL,
  `update_time` datetime NOT NULL,
  `status` int NULL DEFAULT 1 COMMENT '内容状态：1=已发布 2=已下架',
  PRIMARY KEY (`knowledge_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_knowledge
-- ----------------------------
INSERT INTO `tb_knowledge` VALUES (22, '水稻种植全过程', '水稻种植第一步：晒种\n水稻种植第二步：选种\n水稻种植第三步：整秧版\n水稻种植第四步：播种\n水稻种植第五步：插秧\n水稻种植第六步：缓苗\n水稻种植第七步：田间管理（关键）', '2cc2479866734c8980d88c86db7dbdc7.webp', 'gaoge', '16:33:59', '2021-08-27 16:33:59', 1);
INSERT INTO `tb_knowledge` VALUES (23, '玉米种植过程详解 ', '玉米一直都被誉为长寿食品，含有丰富的蛋白质、脂肪、维生素、微量元素、纤维素等，具有开发高营养、高生物学功能食品的巨大潜力。但由于其遗传性较为复杂，变异种类丰富，在常规的育种过程中存在着周期过长、变异系数过大、影响子代生长发育的缺点，而现代生物育种技术不但克服了上述缺点和不足，同时也提高了育种速度和质量。玉米出苗后，要及时检查出苗情况，发现缺苗断垄要及时补种、补栽。3叶期前缺苗，用饱满种子浸种催芽后浇水补种。3叶期后缺苗用带土移栽法补苗(播种时可在行间播预备苗)，另外，缺苗处也可在附近留双株补救。', 'cb0d06358f8c40628b6dca273f881875.jpeg', 'gaoge', '09:31:37', '2021-08-30 09:31:37', 1);
INSERT INTO `tb_knowledge` VALUES (24, '大豆种植', '大豆可分为黄豆、青豆和黑豆。可大家都认为大豆只是黄豆。富含蛋白质,大豆磷脂由大豆提取出来的精华,大豆中提取的纯磷脂精华物质,对人体健康有着极大的帮助，并无副作用。对于黄大豆，它需要较长的生产时间，也非常得能耐寒冷，北方地区的气候条件适合种植;然而青豆的生长时间较短，适宜把', '12be19984e374bcfbf06561571365d07.jpg', 'gaoge', '09:37:43', '2021-08-30 09:37:43', 1);
INSERT INTO `tb_knowledge` VALUES (25, '永泰李干的制作过程', '福州特色农产品，永泰李干的制作过程，100％还原！暑期在家帮父母晒李干，永泰李干虽是福建名产，但是市场占有率很低，“养在深山人未识”。我家李干的口感、品质都是不错的，欢迎大家购买品尝！', '7765f8705bc54a2086bc295f3bd7217c.jpg', 'zhangxukun', '09:39:56', '2021-08-30 09:39:56', 1);
INSERT INTO `tb_knowledge` VALUES (26, '葡萄种植', '葡萄是我们生活中最常见的水果之一。如今，市场上出现了许多葡萄品种，一些葡萄正处于管理的关键阶段。这种葡萄管理说简单其实也简单，说复杂也复杂，会者不难，难者不会。掌握基本要点，就能实现“一年树，两年果，三年高产”的愿望。', 'd50a95115e7d4b2d832fbcc50e35944b.jpg', 'gaoge', '09:44:24', '2021-08-30 09:44:24', 1);
INSERT INTO `tb_knowledge` VALUES (27, '【农业种植 • 园艺】《天天学农（农技知识）》', '天天学农创始团队在过去的数年中走遍中国广大农村，与农民深入交谈，了解越多就越觉得必须要去为农民做点实事。我们中很多人是农民的孩子，知道中国农民真是一群非常勤劳的人，但缺乏生产技术，往往事倍功半，他们也渴望学习。农民是朴实的，像庄稼地一样，种下什么就收获什么。视频涵盖了大棚草莓、猕猴桃、苹果树等方方面面，是农民朋友切实需要的教学视频，我们将提供大量的农业技术教学视频，方便大家学习，不断提高农业技术，创造美好生活。', '5722cfcd93c84a9083720d2cb072c5a0.jpg', 'zhangxukun', '09:46:37', '2021-08-30 09:46:37', 1);
INSERT INTO `tb_knowledge` VALUES (28, '西瓜种植', '西瓜露地早春栽培，一般以地温稳定在15℃左右时为露地播种的适宜时间。播种的最佳时间，还应根据品种、栽培季节、栽培方式以及消费季节等条件来确定。一般月中下旬播种育苗，4月中下旬定植，6月下旬开始收获上市；秋西瓜7月上中旬播种，9月下旬开始采收上市。', 'da482ad921d64a798140138a0607eb76.jpg', 'gaoge', '09:54:19', '2021-08-30 09:54:19', 1);
INSERT INTO `tb_knowledge` VALUES (29, '生姜的一生｜现代农业种植和收获生姜', '两千多年来生姜一直活跃在餐桌上。一般做酱菜和小吃用嫩姜，做调料和药用以老姜为佳。传说，神农氏四处尝百草。有一次，误食毒蘑菇，吃了一株长着尖细叶子的青草，神农氏一阵腹泻，感觉死而复生。神农氏姓姜，他将这株救命的植物，叫做生姜。', '4265868e71a44832a3e39a4547dc307c.jpg', 'zhangxukun', '09:55:54', '2021-08-30 09:55:54', 1);
INSERT INTO `tb_knowledge` VALUES (30, '人工种植蘑菇', '黄伞伞，白杆杆，吃完一起开厂厂！一天卖2吨蘑菇的奥地利现代种植工厂赚钱全过程', '1aff704b6fa94e91b58bdda36f9db166.jpg', 'zhangxukun', '10:00:02', '2021-08-30 10:00:02', 1);

-- ----------------------------
-- Table structure for tb_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_order`;
CREATE TABLE `tb_order`  (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '需求标题',
  `price` decimal(65, 2) NULL DEFAULT NULL COMMENT '期望价格',
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '内容',
  `order_status` int NOT NULL DEFAULT 0 COMMENT '订单状态，0表示待合作，1表示待发货，2表示完成，3表示完成但未评价',
  `type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '1销售订单，2需求订单',
  `picture` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '发起订单人',
  `cooperation_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '合作人名字',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单收货地址',
  `stock` int NULL DEFAULT NULL,
  `spec` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `unit` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `min_purchase` int NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 145 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_order
-- ----------------------------
INSERT INTO `tb_order` VALUES (66, '地瓜粉红薯淀粉', 123.00, '福建闽南泉州官桥正宗农家手工自制地瓜粉红薯淀粉番薯粉无添加剂', 1, 'goods', '6c1c2f5b38ac4be190dfc4a421d65f73.jpg', 'wyn3', NULL, '2021-08-27 16:15:00', '2021-08-27 16:15:00', '高歌 18977771439 山东省德州市夏津县', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (67, '新疆小红杏吊干杏', 45.00, '新疆小红杏吊干杏新鲜杏子农产品应季1斤装水果特甜小白杏净重4斤', 1, 'goods', 'a5ffef69b838400695cf8f4203e6626a.jpg', 'wyn3', NULL, '2021-08-27 16:17:10', '2021-08-27 16:17:10', '张绪昆 18354648787 山东省济南市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (68, '云南特产大香蕉', 23.00, '云南特产冬季水果大香蕉新鲜当季10斤农产品直销土特产农家孕妇水', 1, 'goods', 'a4418dc8694a4c51875e18c045169697.jpg', 'wyn3', NULL, '2021-08-27 16:19:03', '2021-08-27 16:19:03', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (69, '山东大葱', 34.00, '山东大葱新鲜5斤时令蔬菜东北香葱包邮蘸酱铁杆章丘10号助农产品', 1, 'goods', '8781c37f24d24376bfd037fcbcb44dc5.jpg', 'wyn3', NULL, '2021-08-27 16:20:15', '2021-08-27 16:20:15', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (70, '大别山野生羊肚菌', 345.00, '高端消费人群厨房食材 大别山野生羊肚菌干货煲汤菌菇类特产50克', 1, 'goods', '2004039df5b64028bec5d06bdad06f6b.jpg', 'wyn3', NULL, '2021-08-27 16:28:52', '2021-08-27 16:28:52', '张绪昆 18354648787 山东省济南市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (71, '苹果', 22.81, '东北鸡心果5斤锦绣海棠果特产玫瑰小苹果花红沙果孕妇新鲜水果。原产地直发，酸甜可口', 0, 'goods', '4f9d706af7be455cb246c6615a56b631.jpg', 'wyn3', NULL, '2021-08-27 16:30:06', '2022-07-22 10:01:19', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (72, '白溪豆腐干香', 56.00, '白溪豆腐干香干湖南新化特产农家石磨手工风味柴火烟熏非武冈豆干', 1, 'goods', 'c1300fb2e1a34873a8f0ff6274df5dd8.jpg', 'wyn3', NULL, '2021-08-27 16:30:43', '2021-08-27 16:30:43', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (73, '红柚', 23.80, '福建平和红心柚子9斤红肉蜜柚水果新鲜密柚当季琯溪孕妇整箱包邮', 1, 'goods', '3b39708a162f4e6881e3e4e502a2e3a6.jpg', 'wyn3', NULL, '2021-08-27 16:30:48', '2021-08-27 16:30:48', '张绪昆 18396833008 山东省青岛市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (74, '百香果', 14.90, '广西百香果9斤特大果新鲜水果紫皮百果香果酱白香果5一级10包邮', 1, 'goods', '905b6a28de85432c9c969c6d3e06fff7.jpg', 'wyn3', NULL, '2021-08-27 16:31:51', '2021-08-27 16:31:51', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (76, '白葡萄', 79.00, '新疆无核白葡萄无籽水果新鲜当季整箱马奶提子青吐鲁番小葡萄5斤', 1, 'goods', '1dd2029058a547419b32814b9abf2969.jpg', 'wyn3', NULL, '2021-08-27 16:33:11', '2021-08-27 16:33:11', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (77, '榴莲', 65.80, '顺丰空运 泰国a级金枕头榴莲新鲜带壳巴掌当季进口水果整箱包邮', 0, 'goods', '6734e3ec36bd4ebc8348a358603de955.jpg', 'wyn3', NULL, '2021-08-27 16:33:36', '2021-08-27 16:33:36', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (78, '石榴', 15.80, '突尼斯软籽石榴水果新鲜10斤包邮无籽应季特大果会理甜红石榴一级', 0, 'goods', '87fabaebe89f4a66a3e29f1ada41c92b.jpg', 'zhangxukun', NULL, '2021-08-27 16:34:54', '2021-08-27 16:34:54', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (79, '车厘子', 138.00, '新鲜车厘子5斤装高端进口大樱桃包邮山东整箱一箱10当季孕妇水果', 0, 'goods', '4218be16396a44718fc8ed79d3fdfbfa.jpg', 'zhangxukun', NULL, '2021-08-27 16:35:15', '2021-08-27 16:35:15', '成吉思汗 18766661438 山东青岛市崂山区', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (80, '芋头新鲜小芋头', 324.00, '芋头新鲜小芋头江西农家蔬菜包邮毛芋仔梗芋子滑糯芋艿净重10斤', 0, 'goods', '5c888733b8ff4cb3a6ac2628b2492665.jpg', 'wyn3', NULL, '2021-08-27 16:35:17', '2021-08-27 16:35:17', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (81, '水蜜桃', 109.00, '正宗无锡水蜜桃湖景桃子旗舰店软桃新鲜水果礼盒装特产顺丰', 0, 'goods', '0dbd668be84a482ba2749eef5d141424.jpg', 'zhangxukun', NULL, '2021-08-27 16:35:53', '2021-08-27 16:35:53', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (82, '西梅', 69.00, '新疆法兰西西梅甜孕妇水果新鲜当季整箱特级李子正宗喀什西梅便秘', 0, 'goods', 'ef6dae3bb936415e849c208336537e0b.jpg', 'zhangxukun', NULL, '2021-08-27 16:37:42', '2021-08-27 16:37:42', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (83, '求购100斤大米', 123.00, '求购100斤大米，质量佳的优先考虑', 1, 'needs', '35b21d2ca3b1402f9ca67f917ce006c1.gif', 'lzh', NULL, '2021-08-27 16:38:29', '2021-08-27 17:27:49', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (84, '黄桃', 27.80, '正宗锦绣黄桃9斤桃子新鲜当季水果露天脆蜜桃带毛砀山整箱10包邮。收藏下单 拍下17.8元起 精选好果 香甜多汁~', 0, 'goods', 'd75dc206b22a48c6b9e50d81afa51cac.jpg', 'zhangxukun', NULL, '2021-08-27 16:38:58', '2021-08-27 16:38:58', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (86, '求购一千斤玉米', 2323.00, '急需，价低者来，请联系17826782782', 1, 'needs', '71ea0e08a7ce4bb697b1d6b87a113379.webp', 'lzh', NULL, '2021-08-27 16:40:28', '2021-08-27 16:40:28', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (87, '潮汕蓝姜', 25.00, '新鲜南姜蓝姜潮汕姜潮州姜野生山姜满包邮免运费芦苇姜 5斤25元', 0, 'goods', '8361828ce6a54b48832cf6b880961f3e.jpg', 'wyn3', NULL, '2021-08-27 16:41:43', '2021-08-27 16:41:43', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (88, '小香薯', 22.80, '求购临安天目山现挖小香薯', 1, 'needs', 'a70954af69094cbf9c72f6d15eb24509.jpg', 'zhangxukun', NULL, '2021-08-27 16:45:03', '2021-08-27 16:45:03', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (89, '青龙脆瓜', 98.00, '青龙瓜脆瓜稍瓜菜瓜烧瓜酱瓜边梁烧瓜低糖水果非八棱脆瓜三斤装', 0, 'goods', '43489c0d4a164f539fec75cfb8467de7.jpg', 'zwr', NULL, '2021-08-27 16:45:11', '2021-08-27 16:46:16', '成吉思汗 18766661438 山东青岛市崂山区', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (90, '求购90斤苹果', 2332.00, '甘甜可口，不要青苹果', 0, 'needs', 'eee99f060b4843909db360a346ddc18f.webp', 'lzh', NULL, '2021-08-27 16:45:29', '2021-08-27 16:45:29', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (91, '安徽特产米糖小吃', 30.00, '传统农家手工花生炒米糖米花糖安徽特产米糖休闲食品老式小吃零食', 0, 'goods', 'ac7937d4ff8f4eff9faf7dc25f6c2f20.jpg', 'zwr', NULL, '2021-08-27 16:48:05', '2021-08-27 16:53:21', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (92, '求购5斤红辣椒', 39.90, '求购5斤红辣椒，变态辣', 0, 'needs', 'e21080ff8f0c47ffb1dee3e236d6a5eb.jpg', 'zhangxukun', NULL, '2021-08-27 16:48:34', '2021-08-27 16:48:34', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (93, '求购新疆西瓜', 324.00, '要甜的，昼夜温差大的环境中种植的', 0, 'needs', '38d1cf5c26ee444aa709ac2e94730812.webp', 'wyn3', NULL, '2021-08-27 16:51:02', '2021-08-30 13:18:15', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (94, '求购10斤小紫薯', 48.80, '是小土豆，长不大，乒乓球大小的，但是要口感粉糯。有意者联系18396833838', 0, 'needs', '26820e36e2c449479a34b25c8a647f40.jpg', 'zhangxukun', NULL, '2021-08-27 16:51:11', '2021-08-27 16:51:11', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (95, '来100斤茄子', 23321.00, '有的MM，价钱合理哦', 0, 'needs', 'd18b7b37555a4bbda1020d56a0626ed5.jpg', 'lzh', NULL, '2021-08-27 16:53:20', '2021-08-27 16:53:20', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (96, '甘蔗', 35.80, '广西黑皮甘蔗新鲜水果包邮当季特产脆甜杆孕妇果蔗批发整箱9-10斤', 0, 'goods', 'cf5cb1460b71490b97c3a95c71d6d5f6.jpg', 'zhangxukun', NULL, '2021-08-27 16:54:31', '2021-08-27 16:54:31', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (97, '铁棍山药粉条', 69.00, '怀道居铁棍山药粉条河南焦作山药粉皮红薯正宗手工铁棍山药粉丝', 0, 'goods', '53fd61682e224f3abb96bff64afbd04f.jpg', 'zwr', NULL, '2021-08-27 16:55:10', '2021-08-27 16:55:10', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (98, '收100斤羊肉', 2333.00, '价钱给够，带价来，咩咩咩', 0, 'needs', '609b8a1aeef9473ca1bfd5f93098ce8f.jpg', 'wyn3', NULL, '2021-08-27 16:55:57', '2021-08-27 16:55:57', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (99, '野生阳荷新鲜', 26.00, '湖北恩施现挖现新鲜阳荷姜现摘现发新鲜直达500克买2包邮送一野生', 0, 'goods', 'a258efae882540bb910f167aa1c43a8f.jpg', 'zwr', NULL, '2021-08-27 16:57:08', '2021-08-27 16:57:08', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (100, '求购青苹果', 120.00, '求购40斤青苹果，有意者私聊', 0, 'needs', 'effaf0126a2541c4a18f8431051743ac.jpg', 'zhangxukun', NULL, '2021-08-27 16:57:29', '2021-08-27 16:57:29', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (101, '杨梅', 159.00, '求购浮宫杨梅新鲜当季孕妇水果应季非仙居东魁杨梅', 0, 'needs', 'd65fa5bfb94a4270b1e3265e34d83214.jpg', 'zwr', NULL, '2021-08-27 16:59:45', '2021-08-27 16:59:45', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (102, '洋芋蛋蛋', 12.40, '诚信求购新鲜洋芋蛋蛋', 0, 'needs', '0cb58391ab754653abe7958c6b4febc4.jpg', 'zhangxukun', NULL, '2021-08-27 17:00:07', '2021-08-27 17:00:07', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (103, '人参果', 96.00, '求购云南人参果圆果5斤精品中果应季孕妇圆水果新鲜当季', 0, 'needs', '8e5a53c441794395b84076fa2e457f40.jpg', 'zwr', NULL, '2021-08-27 17:01:49', '2021-08-27 17:01:49', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (104, '求购葡萄', 89.00, '求购福安象环葡萄巨峰产地大葡萄5斤', 0, 'needs', '08ea2a0040674d6098b426ad96715fd1.jpg', 'zwr', NULL, '2021-08-27 17:10:07', '2021-08-27 17:10:07', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (105, '甘蓝', 26.80, '求购羽衣甘蓝新鲜沙拉西餐蔬菜食材即食健身有机蔬菜2斤', 0, 'needs', 'f3705c3686944a5bb3843d842f4f37d8.jpg', 'zwr', NULL, '2021-08-27 17:14:26', '2021-08-27 17:14:26', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (106, '佛手瓜', 19.90, '求购新鲜云南佛手瓜5斤', 0, 'needs', '566ae2891bf24952874058b86051f4a2.jpg', 'zwr', NULL, '2021-08-27 17:28:37', '2021-08-27 17:28:37', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (113, '芒果', 12.00, '芒果芒果芒果芒果芒果', 0, 'goods', '2ab1041c64d64575a51d6eafa4dfcc4e.jpg', 'lzh', NULL, '2021-08-31 10:18:31', '2021-08-31 10:18:31', '山东省威海市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (139, '西瓜', 11.00, '烟台红富士本地苹果', 0, 'goods', 'e3b03f632c4241e2addb8f56378f0aed.jpg', 'lisi', NULL, '2022-09-01 09:38:24', '2022-09-01 09:39:36', '山东省临沂市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (140, '西瓜', 10.00, '新疆大西瓜，又甜又脆', 1, 'goods', '9552bd6894ad48909996f59b9f21852a.jpg', 'lzh', NULL, '2022-09-01 11:50:29', '2022-09-01 11:50:29', '山东省临沂市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (141, '求购水蜜桃', 20.00, '求购水蜜桃', 0, 'needs', 'ff485f0e71684f6fb48c23021ebf1408.jpg', 'lzh', NULL, '2022-09-01 11:54:29', '2022-09-01 11:54:29', '山东省临沂市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (142, '西瓜', 30.00, '新疆大西瓜，又甜又脆', 1, 'goods', 'c43dcae086e34c80900885c11f0a9e4d.jpg', 'lisi', NULL, '2022-09-01 17:00:46', '2022-09-01 17:00:46', '山东省青岛市', NULL, NULL, NULL, NULL);
INSERT INTO `tb_order` VALUES (143, '小麦', 2.00, '出售小麦，质量好，价格低', 0, 'goods', '49e7ded3704b45aab65e2b26a1202a89.gif', 'wangya', NULL, '2022-09-08 10:37:27', '2022-09-08 10:37:27', '山东省青岛市', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for tb_purchase
-- ----------------------------
DROP TABLE IF EXISTS `tb_purchase`;
CREATE TABLE `tb_purchase`  (
  `purchase_id` int NOT NULL AUTO_INCREMENT,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `purchase_type` int NOT NULL,
  `total_price` decimal(65, 2) NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单收货地址',
  `purchase_status` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `cancel_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `delivery_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`purchase_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_purchase
-- ----------------------------
INSERT INTO `tb_purchase` VALUES (120, 'wyn3', 1, 15.80, '184', 1, '2022-09-07 16:09:34', '2022-09-07 16:09:34', NULL, NULL);
INSERT INTO `tb_purchase` VALUES (121, 'wyn3', 1, 265.00, '184', 1, '2022-09-07 16:09:55', '2022-09-07 16:09:55', NULL, NULL);
INSERT INTO `tb_purchase` VALUES (122, 'zhangxukun', 1, 324.00, '147', 1, '2022-09-08 10:13:44', '2022-09-08 10:13:44', NULL, NULL);
INSERT INTO `tb_purchase` VALUES (123, 'admin001', 1, 131.60, '190', 1, '2023-06-30 10:15:27', '2023-06-30 10:15:27', NULL, NULL);

-- ----------------------------
-- Table structure for tb_purchase_detail
-- ----------------------------
DROP TABLE IF EXISTS `tb_purchase_detail`;
CREATE TABLE `tb_purchase_detail`  (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `purchase_id` int NOT NULL,
  `order_id` int NOT NULL,
  `unin_price` decimal(65, 2) NOT NULL,
  `sum_price` decimal(65, 2) NOT NULL,
  `count` int NOT NULL,
  PRIMARY KEY (`detail_id`) USING BTREE,
  INDEX `purchase_id`(`purchase_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_purchase_detail
-- ----------------------------
INSERT INTO `tb_purchase_detail` VALUES (123, 120, 78, 15.80, 15.80, 1);
INSERT INTO `tb_purchase_detail` VALUES (124, 121, 97, 69.00, 69.00, 1);
INSERT INTO `tb_purchase_detail` VALUES (125, 121, 89, 98.00, 196.00, 2);
INSERT INTO `tb_purchase_detail` VALUES (126, 122, 80, 324.00, 324.00, 1);
INSERT INTO `tb_purchase_detail` VALUES (127, 123, 77, 65.80, 131.60, 2);

-- ----------------------------
-- Table structure for tb_question
-- ----------------------------
DROP TABLE IF EXISTS `tb_question`;
CREATE TABLE `tb_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `expert_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '专家',
  `questioner` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '手机号',
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '作物详细信息',
  `plant_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '农作物名称',
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '问题标题',
  `question` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '问题',
  `answer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '回答',
  `status` int NOT NULL,
  `attachments` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_question
-- ----------------------------
INSERT INTO `tb_question` VALUES (101, 'zhangxu', 'wyn3', '13792499999', '玉米', '玉米苗灌溉', '专家您好，我想请问玉米苗新技术灌溉，对玉米苗有没有影响?', '', 0, NULL);
INSERT INTO `tb_question` VALUES (103, 'zhangxu', 'wyn3', '13792499999', '新疆哈密瓜', '新疆哈密瓜甜度控制', '新疆哈密瓜甜度是否跟日晒有关?', '哈密瓜性喜充足的阳光和较大的昼夜温差,白天可以充分发挥光合作用,而夜晚的呼吸消耗较小,有利于养分沉淀', 1, NULL);
INSERT INTO `tb_question` VALUES (108, 'gaoge', 'lzh', '15621367568', '苹果树', '苹果果树种植方法', '苹果果树种植多久浇一次水比较好?', '新栽苗木：定植后立即浇透水，前2周每3~5天浇1次，促进根系恢复。生长期：土壤干燥时（表土下5cm干透）每周浇1次，保持土壤湿润但不积水。春季萌芽期：需水量增加，10~15天浇1次（干旱地区缩短至7~10天）。', 1, NULL);
INSERT INTO `tb_question` VALUES (110, 'gaoge', 'wyn3', '13596488256', '番茄', '番茄种植', '番茄种植时为什么会出现裂果？如何预防？', '由于水分波动大（久旱后突然大量浇水或降雨）；钙元素缺乏导致果皮韧性差；高温强光下果皮快速老化。', 1, NULL);
INSERT INTO `tb_question` VALUES (111, 'gaoge', 'wyn3', '13792499275', '草莓', '北方草莓种植品种', '北方草莓种植品种有哪些', '红颜，甜宝', 1, NULL);
INSERT INTO `tb_question` VALUES (112, 'gaoge', 'admin001', '15566554488', '水稻', '水稻秧苗发黄、长势弱可能是什么原因？', '水稻秧苗发黄、长势弱可能是什么原因？', '缺氮：老叶先黄化，植株矮小。土壤板结或积水：根系缺氧，吸收受阻。病虫害：如稻瘟病、潜叶蝇危害。', 1, NULL);
INSERT INTO `tb_question` VALUES (113, 'fengj', 'wyn3', '17256245628', '草莓', '大棚草莓为什么会出现畸形果？', '大棚草莓为什么会出现畸形果？', NULL, 0, NULL);

-- ----------------------------
-- Table structure for tb_reserve
-- ----------------------------
DROP TABLE IF EXISTS `tb_reserve`;
CREATE TABLE `tb_reserve`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `expert_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '专家',
  `questioner` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '咨询者',
  `area` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '面积',
  `address` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '土地地址',
  `plant_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '农作物名称',
  `soil_condition` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '土壤条件',
  `plant_condition` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '作物条件',
  `plant_detail` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '作物详细信息',
  `phone` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '作物详细信息',
  `message` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '留言',
  `answer` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '回答',
  `status` int NOT NULL,
  `appointment_time` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `service_mode` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 211 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_reserve
-- ----------------------------
INSERT INTO `tb_reserve` VALUES (210, 'gaoge', 'wyn3', '2', '青岛崂山区北宅', '草莓', '沙地', '越冬植物', '红颜草莓', '13785964152', NULL, '已处理', 1, NULL, NULL);

-- ----------------------------
-- Table structure for tb_sell_purchase
-- ----------------------------
DROP TABLE IF EXISTS `tb_sell_purchase`;
CREATE TABLE `tb_sell_purchase`  (
  `sell_purchase_id` int NOT NULL AUTO_INCREMENT,
  `purchase_id` int NOT NULL,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `purchase_type` int NOT NULL,
  `unin_pricee` decimal(65, 2) NOT NULL,
  `sum_price` decimal(65, 2) NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单收货地址',
  `purchase_status` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `order_id` int NOT NULL,
  PRIMARY KEY (`sell_purchase_id`) USING BTREE,
  INDEX `purchase_id`(`purchase_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_sell_purchase
-- ----------------------------
INSERT INTO `tb_sell_purchase` VALUES (15, 120, 'zhangxukun', 2, 15.80, 15.80, '184', 1, '2022-09-07 16:09:34', '2022-09-07 16:09:34', 78);
INSERT INTO `tb_sell_purchase` VALUES (16, 121, 'zwr', 2, 69.00, 69.00, '184', 1, '2022-09-07 16:09:55', '2022-09-07 16:09:55', 97);
INSERT INTO `tb_sell_purchase` VALUES (17, 121, 'zwr', 2, 98.00, 196.00, '184', 1, '2022-09-07 16:09:55', '2022-09-07 16:09:55', 89);
INSERT INTO `tb_sell_purchase` VALUES (18, 122, 'wyn3', 2, 324.00, 324.00, '147', 1, '2022-09-08 10:13:44', '2022-09-08 10:13:44', 80);
INSERT INTO `tb_sell_purchase` VALUES (19, 123, 'wyn3', 2, 65.80, 131.60, '190', 1, '2023-06-30 10:15:27', '2023-06-30 10:15:27', 77);

-- ----------------------------
-- Table structure for tb_shoppingcart
-- ----------------------------
DROP TABLE IF EXISTS `tb_shoppingcart`;
CREATE TABLE `tb_shoppingcart`  (
  `shopping_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `count` int NOT NULL,
  `own_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`shopping_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19003 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_shoppingcart
-- ----------------------------
INSERT INTO `tb_shoppingcart` VALUES (1, 99, 1, 'dev_farmer', '2026-07-09 14:14:33', '2026-07-09 14:14:33');
INSERT INTO `tb_shoppingcart` VALUES (2, 99, 1, 'dev_farmer', '2026-07-09 14:14:34', '2026-07-09 14:14:34');
INSERT INTO `tb_shoppingcart` VALUES (3, 99, 1, 'dev_farmer', '2026-07-09 14:14:34', '2026-07-09 14:14:34');
INSERT INTO `tb_shoppingcart` VALUES (4, 99, 1, 'dev_farmer', '2026-07-09 14:14:34', '2026-07-09 14:14:34');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `user_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nick_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `identity_num` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '地址',
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'FARMER' COMMENT 'FARMER农户，BUYER买家，EXPERT专家，BANK银行，SYSTEM_ADMIN管理员',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `integral` int NULL DEFAULT 500 COMMENT '积分500',
  `credit` int NULL DEFAULT 5 COMMENT '信誉1，2，3，4，5',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '头像',
  `real_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('admin', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '管理员', '17894286579', '370343199612012718', '山东省青岛市', 'SYSTEM_ADMIN', '2021-09-01 09:00:51', '2022-09-01 16:35:24', 0, 0, '2ae82e5cf7ca47c9ab516d37dccab5dd.jpg', '');
INSERT INTO `tb_user` VALUES ('demo', '111111', 'demo', '1107197845', NULL, '东莞', 'FARMER', '2026-07-08 20:15:11', '2026-07-08 20:15:11', 500, 5, NULL, '老六');
INSERT INTO `tb_user` VALUES ('dev_admin', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Admin', '13800009000', '430000199001019000', 'Jishou Admin Office', 'SYSTEM_ADMIN', '2026-07-08 11:34:29', '2026-07-08 11:34:29', 1000, 5, 'avatar.png', 'Dev Admin');
INSERT INTO `tb_user` VALUES ('dev_bank', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Bank', '13800009004', '430000199001019004', 'Xiangxi Rural Commercial Bank', 'BANK', '2026-07-08 11:34:29', '2026-07-08 11:34:29', 900, 5, 'avatar.png', 'Lin Manager');
INSERT INTO `tb_user` VALUES ('dev_buyer', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Buyer', '13800009002', '430000199001019002', 'Changsha Purchase Center', 'BUYER', '2026-07-08 11:34:29', '2026-07-08 11:34:29', 720, 4, '2ae82e5cf7ca47c9ab516d37dccab5dd.jpg', 'Chen Buyer');
INSERT INTO `tb_user` VALUES ('dev_expert', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Expert', '13800009003', '430000199001019003', 'Hunan Agricultural University', 'EXPERT', '2026-07-08 11:34:29', '2026-07-08 11:34:29', 980, 5, 'expert01.png', 'Zhou Expert');
INSERT INTO `tb_user` VALUES ('dev_farmer', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Farmer', '13800009001', '430000199001019001', 'Shibadong Kiwi Base, Huayuan', 'FARMER', '2026-07-08 11:34:29', '2026-07-08 11:34:29', 860, 5, '0a2b73b5f1624530a5cbd25fd404a9ac.jpg', 'Shi Xiaoman');
INSERT INTO `tb_user` VALUES ('fengj', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '冯洁', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'fb7eb52c76c747999e553fca1ee6dc9f.png', '冯洁');
INSERT INTO `tb_user` VALUES ('gaoge', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '高歌', '18766661439', '370343199612016352', '山东省临沂市', 'EXPERT', '2021-08-27 16:05:20', '2022-09-01 11:21:11', 0, 0, 'expert04.png', '高歌');
INSERT INTO `tb_user` VALUES ('lijj', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '佳俊', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'expert02.png', '李佳俊');
INSERT INTO `tb_user` VALUES ('lisi', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '思思', '15623652365', '371323199601062719', '山东省临沂市', 'FARMER', '2022-08-19 16:39:11', '2022-09-01 17:20:04', 0, 0, 'expert07.png', '李思');
INSERT INTO `tb_user` VALUES ('liux', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '刘旭', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'expert01.png', '刘旭');
INSERT INTO `tb_user` VALUES ('liyy', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '依依', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, '9908de80aae54f8590b301ee9517beac.png', '李依依');
INSERT INTO `tb_user` VALUES ('lzh', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '小李', '15621367568', '373312199801032719', '山东省威海市', 'FARMER', '2022-07-22 11:05:54', '2022-09-07 16:45:53', 0, 0, 'ac10c6dc98d14afda5f09ba81f286197.jpg', '李增虎');
INSERT INTO `tb_user` VALUES ('tyy', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '公举', '15236983695', '370343199613652415', '山东省青岛市', 'FARMER', '2021-08-30 09:59:50', '2022-08-10 15:44:36', 0, 0, '39557b8fb7a54f81833c8d4a7309b05c.jpg', '唐艳艳');
INSERT INTO `tb_user` VALUES ('wangya', '$2a$10$nLKfTbJqrA5IoRdY.PsZBOACe2s4H3k2NPKLy5LdWL0wKWno0.oDG', '王娅', '13792449255', '370213198911120506', '山东省青岛市', 'FARMER', '2022-09-08 10:14:22', '2022-09-08 10:19:34', 0, 0, '39557b8fb7a54f81833c8d4a7309b05c.jpg', '王娅');
INSERT INTO `tb_user` VALUES ('wuhan', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '吴晗', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'expert08.png', '吴晗');
INSERT INTO `tb_user` VALUES ('wyn', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '玉娜', '18711236658', '370123200008123456', '山东省威海市', 'EXPERT', '2021-08-27 16:05:20', '2022-09-01 11:21:11', 0, 0, 'expert11.png', '王玉娜');
INSERT INTO `tb_user` VALUES ('wyn2', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '艳安', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'expert10.png', '王艳安');
INSERT INTO `tb_user` VALUES ('wyn3', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '丫丫', '15630429628', '324624861233056852', '山东省青岛市', 'FARMER', '2022-04-11 11:36:03', '2022-08-18 15:04:18', 0, 0, 'bd12eba3a9a24d89845ebbdb7fbff448.jpg', '王亚楠');
INSERT INTO `tb_user` VALUES ('zhangsan', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '三三', '13792499274', '370343199609176060', '山东省青岛市', 'FARMER', '2022-08-19 16:37:10', '2022-08-19 16:37:10', 0, 0, 'bd12eba3a9a24d89845ebbdb7fbff448.jpg', '张三');
INSERT INTO `tb_user` VALUES ('zhangxu', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '张旭', '13456567878', '370123200001012233', '山东省青岛市', 'EXPERT', '2021-08-31 10:13:42', '2022-08-10 15:43:58', 0, 0, 'expert10.png', '张旭');
INSERT INTO `tb_user` VALUES ('zhangxukun', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', 'kelven', '13544545454', '370123200008083422', '山东省临沂市', 'FARMER', '2021-08-27 16:05:25', '2022-08-23 15:19:28', 0, 0, '2ae82e5cf7ca47c9ab516d37dccab5dd.jpg', '张旭坤');
INSERT INTO `tb_user` VALUES ('zhaoqm', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '清明', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'expert01.png', '赵清明');
INSERT INTO `tb_user` VALUES ('zhengxin', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '鑫鑫', '15623652222', '370343199612012718', '山东省威海市', 'EXPERT', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'expert09.png', '郑鑫');
INSERT INTO `tb_user` VALUES ('zwr', '$2a$10$AC1gCsk1V5Ov7n.zvkxxvuMM4f3BnWmJqr4jNNYtVAm8j4nBdxIUq', '张文瑞', '15623652222', '111111111111111111', '山东省威海市', 'FARMER', '2021-08-30 09:20:24', '2022-08-23 15:23:34', 0, 0, 'bd12eba3a9a24d89845ebbdb7fbff449.jpg', '张文瑞');

-- ----------------------------
-- Function structure for sfn_GetSimilar_Rate
-- ----------------------------
DROP FUNCTION IF EXISTS `sfn_GetSimilar_Rate`;
delimiter ;;
CREATE FUNCTION `sfn_GetSimilar_Rate`(s1 VARCHAR(64),s2 VARCHAR(64))
 RETURNS float
  DETERMINISTIC
BEGIN
declare l1 int default 0;
declare l2 int default 0;
declare i int default 0;
declare _ss varchar(2) ;
declare j int default 0;
declare sum int default 0;
set l1=CHAR_LENGTH(s1);
set l2=CHAR_LENGTH(S2);
IF l1=0 THEN RETURN 0;
END IF;
IF l2=0 THEN RETURN 0;
END IF;
SET i=0;
set sum=0;
myloop1:LOOP
	SET i=i+1;
	IF i>l1 THEN LEAVE myloop1;
	end if;
	SET _ss=substr(s1,i,1);
	SET j=0;
	myloop2:LOOP
		set j=j+1;
		if j>l2 then 
		   LEAVE myloop2;
		end if;
		if _ss=substr(s2,j,1) then 
		  set sum=sum+1;
		end if;
	END LOOP myloop2;
END LOOP myloop1;
RETURN sum/l2;
END
;;
delimiter ;

-- ----------------------------
-- Extended Chinese demo accounts and linked business records
-- Password for every dev_* account: Test@123456
-- ----------------------------
UPDATE tb_user SET nick_name = '演示管理员', address = '吉首市平台运营中心', real_name = '张管理员' WHERE user_name = 'dev_admin';
UPDATE tb_user SET nick_name = '十八洞农户', address = '花垣县十八洞猕猴桃基地', real_name = '石小满' WHERE user_name = 'dev_farmer';
UPDATE tb_user SET nick_name = '社区团购买家', address = '长沙市雨花区采购中心', real_name = '陈采薇' WHERE user_name = 'dev_buyer';
UPDATE tb_user SET nick_name = '果树专家', address = '湖南农业大学园艺学院', real_name = '周明' WHERE user_name = 'dev_expert';
UPDATE tb_user SET nick_name = '农商行客户经理', address = '湘西农村商业银行', real_name = '林经理' WHERE user_name = 'dev_bank';
UPDATE tb_expert SET real_name = '周明', profession = '猕猴桃病虫害防治与土壤改良', position = '高级农艺师', belong = '湖南农业大学园艺学院' WHERE user_name = 'dev_expert';
UPDATE tb_bank SET bank_name = '湘西农商行春耕信用贷', introduce = '用于种子、化肥、灌溉、冷链和订单周转，材料精简，可按经营流水授信。', repayment = '按季付息' WHERE bank_id = 19001;
UPDATE tb_bank SET bank_name = '吉首普惠订单周转贷', introduce = '面向绿色农产品采购订单提供短期流动资金支持。', repayment = '到期还本' WHERE bank_id = 19002;
UPDATE tb_bank_user SET real_name = '林经理' WHERE user_name = 'dev_bank';
UPDATE tb_order SET title = '湘西鲜草莓 3kg', content = '当天采摘并完成分级，适合社区团购和商超试销。', type = '水果', address = '湘西州花垣县', spec = '3kg/箱', unit = '箱' WHERE order_id = 19001;
UPDATE tb_order SET title = '高山生态大米 50kg', content = '梯田稻谷低温烘干，适合食堂和商超批量采购。', type = '粮油', address = '湘西州龙山县', spec = '50kg/袋', unit = '斤' WHERE order_id = 19002;
UPDATE tb_order SET title = '柑橘分选包装服务', content = '提供柑橘分级、套袋和装箱服务，适合采收高峰期。', type = '农业服务', address = '湘西州泸溪县', spec = '按斤计价', unit = '斤' WHERE order_id = 19003;
UPDATE tb_order SET title = '新鲜农家鸡蛋 30枚', content = '按重量筛选褐壳和白壳鸡蛋，适合社区团购。', type = '禽蛋', address = '湘西州吉首市', spec = '30枚/箱', unit = '箱' WHERE order_id = 19004;

INSERT INTO tb_user (
  user_name, password, nick_name, phone, identity_num, address, role,
  create_time, update_time, integral, credit, avatar, real_name
) VALUES
('dev_farmer2', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '浦市果园农户', '13800009005', '430000199001019005', '泸溪县浦市柑橘合作社', 'FARMER', NOW(), NOW(), 790, 4, 'bd12eba3a9a24d89845ebbdb7fbff448.jpg', '吴果香'),
('dev_buyer2', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '商超采购经理', '13800009006', '430000199001019006', '吉首市生鲜商超配送中心', 'BUYER', NOW(), NOW(), 810, 5, 'a6f5dff3a39541009fedc8621485898c.png', '刘采购'),
('dev_expert2', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '植保专家', '13800009007', '430000199001019007', '湘西州农业技术推广站', 'EXPERT', NOW(), NOW(), 960, 5, 'expert06.png', '彭晓禾'),
('dev_bank2', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '普惠金融经理', '13800009008', '430000199001019008', '吉首普惠金融服务中心', 'BANK', NOW(), NOW(), 920, 5, '9908de80aae54f8590b301ee9517beac.png', '向经理');

INSERT INTO tb_expert (user_name, real_name, phone, profession, position, belong) VALUES
('dev_expert2', '彭晓禾', '13800009007', '水稻植保、病虫害识别与绿色防控', '农业技术推广研究员', '湘西州农业技术推广站');

INSERT INTO tb_bank (bank_id, bank_name, introduce, bank_phone, money, rate, repayment) VALUES
(19003, '柑橘产业升级贷', '支持果园水肥一体化、分选设备和冷库改造。', '0743-1900390', 260000.00, 3.95, '等额本息'),
(19004, '农产品仓储设备贷', '支持仓储、包装、冷链运输等生产经营设备采购。', '0743-1900490', 500000.00, 4.35, '按月付息');

INSERT INTO tb_bank_user (user_name, bank_id, password, real_name, phone, role, create_time, update_time) VALUES
('dev_bank2', 19003, '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '向经理', '13800009008', 'BANK', NOW(), NOW());

INSERT INTO tb_address (id, own_name, consignee, phone, address_detail, is_default) VALUES
(19004, 'dev_buyer2', '刘采购', '13800009006', '吉首市生鲜商超中央仓 C1', 1),
(19005, 'dev_farmer2', '吴果香', '13800009005', '泸溪县浦市柑橘合作社', 1);

INSERT INTO tb_order (
  order_id, title, price, content, order_status, type, picture,
  own_name, cooperation_name, create_time, update_time, address,
  stock, spec, unit, min_purchase
) VALUES
(19005, '浦市冰糖橙 10kg', 42.00, '果径分级、甜度检测后装箱，可提供商超条码标签。', 1, '水果', '5722cfcd93c84a9083720d2cb072c5a0.jpg', 'dev_farmer2', 'dev_buyer2', NOW(), NOW(), '湘西州泸溪县', 560, '10kg/箱', '箱', 5),
(19006, '保靖黄金茶 500g', 88.00, '明前春茶，支持礼盒和散装两种包装。', 0, '茶叶', 'tea.png', 'dev_farmer2', NULL, NOW(), NOW(), '湘西州保靖县', 180, '500g/盒', '盒', 2),
(19007, '高山红薯 25kg', 3.60, '沙壤土种植，完成泥土初筛，适合食堂和加工采购。', 1, '薯类', 'a7a1262dc3994331937e53d5258b57cb.webp', 'dev_farmer2', 'dev_buyer2', NOW(), NOW(), '湘西州永顺县', 1200, '25kg/袋', '斤', 50),
(19008, '时令鲜蔬组合 10kg', 6.50, '包含辣椒、茄子、豆角等当季蔬菜，按采购日组合发货。', 0, '蔬菜', '新鲜蔬菜.png', 'dev_farmer2', NULL, NOW(), NOW(), '湘西州吉首市', 360, '10kg/筐', '斤', 20);

INSERT INTO tb_shoppingcart (shopping_id, order_id, count, own_name, create_time, update_time) VALUES
(19004, 19005, 4, 'dev_buyer2', NOW(), NOW()),
(19005, 19007, 2, 'dev_buyer2', NOW(), NOW());

INSERT INTO tb_purchase (purchase_id, own_name, purchase_type, total_price, address, purchase_status, create_time, update_time, cancel_reason, delivery_no) VALUES
(19003, 'dev_buyer2', 1, 840.00, '吉首市生鲜商超中央仓 C1', 2, NOW(), NOW(), NULL, 'DEV-WL-19003'),
(19004, 'dev_buyer2', 1, 360.00, '吉首市生鲜商超中央仓 C1', 0, NOW(), NOW(), NULL, NULL);

INSERT INTO tb_purchase_detail (detail_id, purchase_id, order_id, unin_price, sum_price, count) VALUES
(19003, 19003, 19005, 42.00, 840.00, 20),
(19004, 19004, 19007, 3.60, 360.00, 100);

INSERT INTO tb_sell_purchase (sell_purchase_id, purchase_id, own_name, purchase_type, unin_pricee, sum_price, address, purchase_status, create_time, update_time, order_id) VALUES
(19003, 19003, 'dev_farmer2', 1, 42.00, 840.00, '吉首市生鲜商超中央仓 C1', 2, NOW(), NOW(), 19005),
(19004, 19004, 'dev_farmer2', 1, 3.60, 360.00, '吉首市生鲜商超中央仓 C1', 0, NOW(), NOW(), 19007);

INSERT INTO tb_finance (
  finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
  money, rate, repayment, create_time, update_time,
  combination_name1, combination_phone1, combination_idnum1,
  combination_name2, combination_phone2, combination_idnum2, file_info, materials
) VALUES
(19003, 19003, 'dev_farmer2', '吴果香', '13800009005', '430000199001019005', 0, '等待补充果园设备报价单', 120000.00, 3.95, '等额本息', NOW(), NOW(), '刘采购', '13800009006', '430000199001019006', NULL, NULL, NULL, '/files/dev-orange-equipment.pdf', '身份证明; 果园承包证明; 商超采购意向书'),
(19004, 19004, 'dev_farmer2', '吴果香', '13800009005', '430000199001019005', 2, '仓储建设周期暂不符合当前产品要求', 200000.00, 4.35, '按月付息', NOW(), NOW(), NULL, NULL, NULL, NULL, NULL, NULL, '/files/dev-warehouse-plan.pdf', '仓储改造方案; 设备清单; 近半年销售流水');

INSERT INTO tb_financing_intention (id, user_name, real_name, address, amount, application, item, repayment_period, area, phone, create_time, update_time) VALUES
(19003, 'dev_farmer2', '吴果香', '泸溪县浦市镇', 120000, '分选机和水肥一体化设备采购', '柑橘果园升级', '18个月', '60亩', '13800009005', NOW(), NOW()),
(19004, 'dev_buyer2', '刘采购', '吉首市乾州街道', 180000, '商超生鲜采购周转', '湘西特色农产品专柜', '9个月', '8亩', '13800009006', NOW(), NOW());

INSERT INTO tb_question (id, expert_name, questioner, phone, plant_name, title, question, answer, status, attachments) VALUES
(19003, 'dev_expert', 'dev_farmer2', '13800009005', '柑橘', '幼果期出现黑点', '冰糖橙幼果表面出现黑点，近期雨水较多，是否需要调整用药？', '先确认是否为黑点病，清理枯枝并选择雨停后的保护性药剂，注意安全间隔期。', 1, '5722cfcd93c84a9083720d2cb072c5a0.jpg'),
(19004, 'dev_expert2', 'dev_farmer2', '13800009005', '红薯', '叶片卷曲且长势不齐', '部分地块红薯叶片卷曲，是否与蓟马或缺素有关？', NULL, 0, NULL);

INSERT INTO tb_reserve (
  id, expert_name, questioner, area, address, plant_name,
  soil_condition, plant_condition, plant_detail, phone, message, answer,
  status, appointment_time, service_mode
) VALUES
(19003, 'dev_expert', 'dev_farmer2', '60亩', '泸溪县浦市柑橘园', '柑橘', '红壤，坡地排水较快', '幼果黑点较多', '东侧果园密度偏大，雨后发病明显', '13800009005', '希望安排一次现场巡园', '可安排周日下午，请准备近期施药和施肥记录。', 1, '2026-07-20 14:00', '现场指导'),
(19004, 'dev_expert2', 'dev_farmer2', '25亩', '永顺县高山红薯基地', '红薯', '沙壤土，局部偏旱', '叶片卷曲', '新叶卷曲并伴有少量虫口', '13800009005', '先做视频诊断再决定是否到场', NULL, 0, '2026-07-21 10:00', '视频指导');

INSERT INTO tb_knowledge (knowledge_id, title, content, pic_path, own_name, create_time, update_time, status) VALUES
(19003, '水稻纹枯病绿色防控清单', '控制田间湿度和种植密度，结合病株识别、科学施药与安全间隔期管理。', 'W020230811400645740814_ORIGIN.jpg', 'dev_expert2', CURRENT_TIME, NOW(), 1),
(19004, '湘西柑橘进入商超采购专区', '泸溪柑橘合作社新增分级包装货源，支持商超采购、冷链配送和订单融资。', '5722cfcd93c84a9083720d2cb072c5a0.jpg', '平台资讯', CURRENT_TIME, NOW(), 1);

INSERT INTO tb_discuss (discuss_id, knowledge_id, own_name, content, create_time) VALUES
(19003, 19003, 'dev_farmer2', '已收藏，准备按清单检查红薯和水稻地块的病虫害。', NOW()),
(19004, 19004, 'dev_buyer2', '商超采购需要稳定规格，后续希望增加每周可供货量。', NOW());

SET FOREIGN_KEY_CHECKS = 1;
