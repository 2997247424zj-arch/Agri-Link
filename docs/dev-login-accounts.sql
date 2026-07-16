-- Agri-Link local business testing accounts and interactive seed data.
-- Target database: local/demo rongxiaotong.
-- Do NOT execute this file on production data.
-- All test accounts use BCrypt password hash. Plain password: Test@123456
-- Accounts: dev_admin / dev_farmer(2) / dev_buyer(2) / dev_expert(2) / dev_bank(2)

-- 0. Schema compatibility for older local databases.
-- Current backend reads/writes these columns. Add them when the imported SQL is older.
SET @ddl = IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_reserve' AND column_name = 'appointment_time') = 0,
  'ALTER TABLE tb_reserve ADD COLUMN appointment_time varchar(64) DEFAULT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_reserve' AND column_name = 'service_mode') = 0,
  'ALTER TABLE tb_reserve ADD COLUMN service_mode varchar(64) DEFAULT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_finance' AND column_name = 'materials') = 0,
  'ALTER TABLE tb_finance ADD COLUMN materials text DEFAULT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  (SELECT data_type FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_bank' AND column_name = 'repayment') <> 'varchar',
  'ALTER TABLE tb_bank MODIFY COLUMN repayment varchar(64) NOT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  (SELECT data_type FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_finance' AND column_name = 'repayment') <> 'varchar',
  'ALTER TABLE tb_finance MODIFY COLUMN repayment varchar(64) NOT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  (SELECT data_type FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_financing_intention' AND column_name = 'repayment_period') <> 'varchar',
  'ALTER TABLE tb_financing_intention MODIFY COLUMN repayment_period varchar(64) NOT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF(
  (SELECT data_type FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'tb_financing_intention' AND column_name = 'area') <> 'varchar',
  'ALTER TABLE tb_financing_intention MODIFY COLUMN area varchar(64) NOT NULL',
  'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

START TRANSACTION;

-- 1. Clean old dev seed data so this script can be executed repeatedly.
DELETE FROM tb_purchase_detail WHERE detail_id BETWEEN 19001 AND 19040 OR purchase_id BETWEEN 19001 AND 19040;
DELETE FROM tb_purchase WHERE purchase_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_buyer', 'dev_buyer2', 'dev_farmer', 'dev_farmer2');
DELETE FROM tb_sell_purchase WHERE sell_purchase_id BETWEEN 19001 AND 19040 OR purchase_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_buyer', 'dev_buyer2', 'dev_farmer', 'dev_farmer2');
DELETE FROM tb_shoppingcart WHERE shopping_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_buyer', 'dev_buyer2') OR order_id BETWEEN 19001 AND 19040;
DELETE FROM tb_finance WHERE finance_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2');
DELETE FROM tb_financing_intention WHERE id BETWEEN 19001 AND 19040 OR user_name IN ('dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2');
DELETE FROM tb_discuss WHERE discuss_id BETWEEN 19001 AND 19040 OR knowledge_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2', 'dev_expert', 'dev_expert2');
DELETE FROM tb_knowledge WHERE knowledge_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_expert', 'dev_expert2', '平台资讯', 'Dev News');
DELETE FROM tb_question WHERE id BETWEEN 19001 AND 19040 OR questioner IN ('dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2') OR expert_name IN ('dev_expert', 'dev_expert2');
DELETE FROM tb_reserve WHERE id BETWEEN 19001 AND 19040 OR questioner IN ('dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2') OR expert_name IN ('dev_expert', 'dev_expert2');
DELETE FROM tb_order WHERE order_id BETWEEN 19001 AND 19040 OR own_name IN ('dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2');
DELETE FROM tb_address WHERE id BETWEEN 19001 AND 19040 OR own_name IN ('dev_buyer', 'dev_buyer2', 'dev_farmer', 'dev_farmer2');
DELETE FROM tb_expert WHERE user_name IN ('dev_expert', 'dev_expert2');
DELETE FROM tb_bank_user WHERE user_name IN ('dev_bank', 'dev_bank2');
DELETE FROM tb_bank WHERE bank_id BETWEEN 19001 AND 19040;
DELETE FROM tb_user WHERE user_name IN ('dev_admin', 'dev_farmer', 'dev_farmer2', 'dev_buyer', 'dev_buyer2', 'dev_expert', 'dev_expert2', 'dev_bank', 'dev_bank2');

-- 2. Role accounts: admin, farmer, buyer, expert and bank.
INSERT INTO tb_user (
  user_name, password, nick_name, phone, identity_num, address, role,
  create_time, update_time, integral, credit, avatar, real_name
) VALUES
('dev_admin',   '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '演示管理员', '13800009000', '430000199001019000', '吉首市平台运营中心', 'SYSTEM_ADMIN', NOW(), NOW(), 1000, 5, 'avatar.png', '张管理员'),
('dev_farmer',  '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '十八洞农户', '13800009001', '430000199001019001', '花垣县十八洞猕猴桃基地', 'FARMER', NOW(), NOW(), 860, 5, '0a2b73b5f1624530a5cbd25fd404a9ac.jpg', '石小满'),
('dev_farmer2', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '浦市果园农户', '13800009005', '430000199001019005', '泸溪县浦市柑橘合作社', 'FARMER', NOW(), NOW(), 790, 4, 'bd12eba3a9a24d89845ebbdb7fbff448.jpg', '吴果香'),
('dev_buyer',   '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '社区团购买家', '13800009002', '430000199001019002', '长沙市雨花区采购中心', 'BUYER', NOW(), NOW(), 720, 4, '2ae82e5cf7ca47c9ab516d37dccab5dd.jpg', '陈采薇'),
('dev_buyer2',  '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '商超采购经理', '13800009006', '430000199001019006', '吉首市生鲜商超配送中心', 'BUYER', NOW(), NOW(), 810, 5, 'a6f5dff3a39541009fedc8621485898c.png', '刘采购'),
('dev_expert',  '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '果树专家', '13800009003', '430000199001019003', '湖南农业大学园艺学院', 'EXPERT', NOW(), NOW(), 980, 5, 'expert01.png', '周明'),
('dev_expert2', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '植保专家', '13800009007', '430000199001019007', '湘西州农业技术推广站', 'EXPERT', NOW(), NOW(), 960, 5, 'expert06.png', '彭晓禾'),
('dev_bank',    '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '农商行客户经理', '13800009004', '430000199001019004', '湘西农村商业银行', 'BANK', NOW(), NOW(), 900, 5, 'avatar.png', '林经理'),
('dev_bank2',   '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '普惠金融经理', '13800009008', '430000199001019008', '吉首普惠金融服务中心', 'BANK', NOW(), NOW(), 920, 5, '9908de80aae54f8590b301ee9517beac.png', '向经理');

INSERT INTO tb_expert (user_name, real_name, phone, profession, position, belong) VALUES
('dev_expert', '周明', '13800009003', '猕猴桃病虫害防治与土壤改良', '高级农艺师', '湖南农业大学园艺学院'),
('dev_expert2', '彭晓禾', '13800009007', '水稻植保、病虫害识别与绿色防控', '农业技术推广研究员', '湘西州农业技术推广站');

INSERT INTO tb_bank (bank_id, bank_name, introduce, bank_phone, money, rate, repayment) VALUES
(19001, '湘西农商行春耕信用贷', '用于种子、化肥、灌溉、冷链和订单周转，材料精简，可按经营流水授信。', '0743-1900190', 300000.00, 3.85, '按季付息'),
(19002, '吉首普惠订单周转贷', '面向绿色农产品采购订单提供短期流动资金支持。', '0743-1900290', 150000.00, 4.20, '到期还本'),
(19003, '柑橘产业升级贷', '支持果园水肥一体化、分选设备和冷库改造。', '0743-1900390', 260000.00, 3.95, '等额本息'),
(19004, '农产品仓储设备贷', '支持仓储、包装、冷链运输等生产经营设备采购。', '0743-1900490', 500000.00, 4.35, '按月付息');

INSERT INTO tb_bank_user (user_name, bank_id, password, real_name, phone, role, create_time, update_time) VALUES
('dev_bank', 19001, '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '林经理', '13800009004', 'BANK', NOW(), NOW()),
('dev_bank2', 19003, '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', '向经理', '13800009008', 'BANK', NOW(), NOW());

-- 3. Addresses for buyer/farmer profile, cart and purchase flows.
INSERT INTO tb_address (id, own_name, consignee, phone, address_detail, is_default) VALUES
(19001, 'dev_buyer',  '陈采薇', '13800009002', '长沙市雨花区农产品采购仓 A1', 1),
(19002, 'dev_buyer',  '陈采薇', '13800009002', '吉首市冷链配送中心 B2', 0),
(19003, 'dev_farmer', '石小满', '13800009001', '花垣县十八洞猕猴桃示范基地', 1),
(19004, 'dev_buyer2', '刘采购', '13800009006', '吉首市生鲜商超中央仓 C1', 1),
(19005, 'dev_farmer2', '吴果香', '13800009005', '泸溪县浦市柑橘合作社', 1);

-- 4. Trade orders, cart and purchases for visual checks in trade/cart/profile/admin pages.
INSERT INTO tb_order (
  order_id, title, price, content, order_status, type, picture,
  own_name, cooperation_name, create_time, update_time, address,
  stock, spec, unit, min_purchase
) VALUES
(19001, '湘西鲜草莓 3kg', 38.00, '当天采摘并完成分级，适合社区团购和商超试销。', 1, '水果', '75e4ef70b5a64dbd9a8736446014ce27.jpg', 'dev_farmer', 'dev_buyer', NOW(), NOW(), '湘西州花垣县', 420, '3kg/箱', '箱', 2),
(19002, '高山生态大米 50kg', 5.20, '梯田稻谷低温烘干，适合食堂和商超批量采购。', 0, '粮油', '02d69a4b9ad5439e9840a357fb509734.webp', 'dev_farmer', NULL, NOW(), NOW(), '湘西州龙山县', 800, '50kg/袋', '斤', 50),
(19003, '柑橘分选包装服务', 2.50, '提供柑橘分级、套袋和装箱服务，适合采收高峰期。', 2, '农业服务', '5722cfcd93c84a9083720d2cb072c5a0.jpg', 'dev_farmer', 'dev_buyer', NOW(), NOW(), '湘西州泸溪县', 3000, '按斤计价', '斤', 100),
(19004, '新鲜农家鸡蛋 30枚', 29.90, '按重量筛选褐壳和白壳鸡蛋，适合社区团购。', 1, '禽蛋', 'fresh-eggs.webp', 'dev_farmer', 'dev_buyer', NOW(), NOW(), '湘西州吉首市', 260, '30枚/箱', '箱', 2),
(19005, '浦市冰糖橙 10kg', 42.00, '果径分级、甜度检测后装箱，可提供商超条码标签。', 1, '水果', '5722cfcd93c84a9083720d2cb072c5a0.jpg', 'dev_farmer2', 'dev_buyer2', NOW(), NOW(), '湘西州泸溪县', 560, '10kg/箱', '箱', 5),
(19006, '保靖黄金茶 500g', 88.00, '明前春茶，支持礼盒和散装两种包装。', 0, '茶叶', 'tea.png', 'dev_farmer2', NULL, NOW(), NOW(), '湘西州保靖县', 180, '500g/盒', '盒', 2),
(19007, '高山红薯 25kg', 3.60, '沙壤土种植，完成泥土初筛，适合食堂和加工采购。', 1, '薯类', 'a7a1262dc3994331937e53d5258b57cb.webp', 'dev_farmer2', 'dev_buyer2', NOW(), NOW(), '湘西州永顺县', 1200, '25kg/袋', '斤', 50),
(19008, '时令鲜蔬组合 10kg', 6.50, '包含辣椒、茄子、豆角等当季蔬菜，按采购日组合发货。', 0, '蔬菜', '新鲜蔬菜.png', 'dev_farmer2', NULL, NOW(), NOW(), '湘西州吉首市', 360, '10kg/筐', '斤', 20);

INSERT INTO tb_shoppingcart (shopping_id, order_id, count, own_name, create_time, update_time) VALUES
(19001, 19001, 2, 'dev_buyer', NOW(), NOW()),
(19002, 19002, 1, 'dev_buyer', NOW(), NOW()),
(19003, 19004, 2, 'dev_buyer', NOW(), NOW()),
(19004, 19005, 4, 'dev_buyer2', NOW(), NOW()),
(19005, 19007, 2, 'dev_buyer2', NOW(), NOW());

INSERT INTO tb_purchase (purchase_id, own_name, purchase_type, total_price, address, purchase_status, create_time, update_time, cancel_reason, delivery_no) VALUES
(19001, 'dev_buyer', 1, 344.00, '长沙市雨花区农产品采购仓 A1', 1, NOW(), NOW(), NULL, 'DEV-WL-19001'),
(19002, 'dev_buyer', 1, 260.00, '吉首市冷链配送中心 B2', 0, NOW(), NOW(), NULL, NULL),
(19003, 'dev_buyer2', 1, 840.00, '吉首市生鲜商超中央仓 C1', 2, NOW(), NOW(), NULL, 'DEV-WL-19003'),
(19004, 'dev_buyer2', 1, 360.00, '吉首市生鲜商超中央仓 C1', 0, NOW(), NOW(), NULL, NULL);

INSERT INTO tb_purchase_detail (detail_id, purchase_id, order_id, unin_price, sum_price, count) VALUES
(19001, 19001, 19001, 8.60, 344.00, 40),
(19002, 19002, 19002, 5.20, 260.00, 50),
(19003, 19003, 19005, 42.00, 840.00, 20),
(19004, 19004, 19007, 3.60, 360.00, 100);

INSERT INTO tb_sell_purchase (sell_purchase_id, purchase_id, own_name, purchase_type, unin_pricee, sum_price, address, purchase_status, create_time, update_time, order_id) VALUES
(19001, 19001, 'dev_farmer', 1, 8.60, 344.00, '长沙市雨花区农产品采购仓 A1', 1, NOW(), NOW(), 19001),
(19002, 19002, 'dev_farmer', 1, 5.20, 260.00, '吉首市冷链配送中心 B2', 0, NOW(), NOW(), 19002),
(19003, 19003, 'dev_farmer2', 1, 42.00, 840.00, '吉首市生鲜商超中央仓 C1', 2, NOW(), NOW(), 19005),
(19004, 19004, 'dev_farmer2', 1, 3.60, 360.00, '吉首市生鲜商超中央仓 C1', 0, NOW(), NOW(), 19007);

-- 5. Finance applications and intentions for farmer/bank/admin interactions.
INSERT INTO tb_finance (
  finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
  money, rate, repayment, create_time, update_time,
  combination_name1, combination_phone1, combination_idnum1,
  combination_name2, combination_phone2, combination_idnum2,
  file_info, materials
) VALUES
(19001, 19001, 'dev_farmer', '石小满', '13800009001', '430000199001019001', 0, '等待银行核验春耕采购合同', 80000.00, 3.85, '按季付息', NOW(), NOW(), '陈采薇', '13800009002', '430000199001019002', NULL, NULL, NULL, '/files/dev-finance-contract.pdf', '身份证明; 土地租赁合同; 近三个月交易流水'),
(19002, 19002, 'dev_farmer', '石小满', '13800009001', '430000199001019001', 1, '冷链周转授信已通过', 50000.00, 4.20, '到期还本', NOW(), NOW(), NULL, NULL, NULL, NULL, NULL, NULL, '/files/dev-cold-chain.pdf', '采购订单合同; 收货地址证明'),
(19003, 19003, 'dev_farmer2', '吴果香', '13800009005', '430000199001019005', 0, '等待补充果园设备报价单', 120000.00, 3.95, '等额本息', NOW(), NOW(), '刘采购', '13800009006', '430000199001019006', NULL, NULL, NULL, '/files/dev-orange-equipment.pdf', '身份证明; 果园承包证明; 商超采购意向书'),
(19004, 19004, 'dev_farmer2', '吴果香', '13800009005', '430000199001019005', 2, '仓储建设周期暂不符合当前产品要求', 200000.00, 4.35, '按月付息', NOW(), NOW(), NULL, NULL, NULL, NULL, NULL, NULL, '/files/dev-warehouse-plan.pdf', '仓储改造方案; 设备清单; 近半年销售流水');

INSERT INTO tb_financing_intention (id, user_name, real_name, address, amount, application, item, repayment_period, area, phone, create_time, update_time) VALUES
(19001, 'dev_farmer', '石小满', '花垣县十八洞村', 80000, '春耕化肥采购与灌溉升级', '猕猴桃基地扩建', '12个月', '35亩', '13800009001', NOW(), NOW()),
(19002, 'dev_buyer', '陈采薇', '长沙市雨花区', 50000, '采购订单周转资金', '社区团购备货', '6个月', '5亩', '13800009002', NOW(), NOW()),
(19003, 'dev_farmer2', '吴果香', '泸溪县浦市镇', 120000, '分选机和水肥一体化设备采购', '柑橘果园升级', '18个月', '60亩', '13800009005', NOW(), NOW()),
(19004, 'dev_buyer2', '刘采购', '吉首市乾州街道', 180000, '商超生鲜采购周转', '湘西特色农产品专柜', '9个月', '8亩', '13800009006', NOW(), NOW());

-- 6. Expert Q&A and reservations for farmer/expert workflow checks.
INSERT INTO tb_question (id, expert_name, questioner, phone, plant_name, title, question, answer, status, attachments) VALUES
(19001, 'dev_expert', 'dev_farmer', '13800009001', '猕猴桃', '雨后叶片黄化并伴随落果', '南坡猕猴桃连续降雨后叶片发黄，应该先排水还是补钙？', '先疏通排水沟，再喷施钙镁叶面肥，并抽样检查根腐情况。', 1, 'W020230811400645740814_ORIGIN.jpg'),
(19002, 'dev_expert2', 'dev_farmer', '13800009001', '水稻', '稻田局部倒伏', '高山水稻抽穗后遇连续降雨出现局部倒伏，还能补救吗？', NULL, 0, NULL),
(19003, 'dev_expert', 'dev_farmer2', '13800009005', '柑橘', '幼果期出现黑点', '冰糖橙幼果表面出现黑点，近期雨水较多，是否需要调整用药？', '先确认是否为黑点病，清理枯枝并选择雨停后的保护性药剂，注意安全间隔期。', 1, '5722cfcd93c84a9083720d2cb072c5a0.jpg'),
(19004, 'dev_expert2', 'dev_farmer2', '13800009005', '红薯', '叶片卷曲且长势不齐', '部分地块红薯叶片卷曲，是否与蓟马或缺素有关？', NULL, 0, NULL);

INSERT INTO tb_reserve (
  id, expert_name, questioner, area, address, plant_name,
  soil_condition, plant_condition, plant_detail, phone, message, answer,
  status, appointment_time, service_mode
) VALUES
(19001, 'dev_expert', 'dev_farmer', '35亩', '十八洞猕猴桃示范基地', '猕猴桃', '酸性黄壤，雨后低洼处积水', '叶片黄化并轻微落果', '三年生果树，南坡低处更严重', '13800009001', '希望本周末到场指导', '周六上午可以到场，请先准备土壤检测记录。', 1, '2026-07-18 09:30', '现场指导'),
(19002, 'dev_expert2', 'dev_farmer', '20亩', '龙山县梯田水稻基地', '水稻', '有机质较高，保水性好', '局部倒伏', '抽穗后遇连续降雨倒伏', '13800009001', '需要远程图片诊断', NULL, 0, '2026-07-19 15:00', '视频指导'),
(19003, 'dev_expert', 'dev_farmer2', '60亩', '泸溪县浦市柑橘园', '柑橘', '红壤，坡地排水较快', '幼果黑点较多', '东侧果园密度偏大，雨后发病明显', '13800009005', '希望安排一次现场巡园', '可安排周日下午，请准备近期施药和施肥记录。', 1, '2026-07-20 14:00', '现场指导'),
(19004, 'dev_expert2', 'dev_farmer2', '25亩', '永顺县高山红薯基地', '红薯', '沙壤土，局部偏旱', '叶片卷曲', '新叶卷曲并伴有少量虫口', '13800009005', '先做视频诊断再决定是否到场', NULL, 0, '2026-07-21 10:00', '视频指导');

-- 7. Knowledge articles and comments for public knowledge and admin content checks.
INSERT INTO tb_knowledge (knowledge_id, title, content, pic_path, own_name, create_time, update_time) VALUES
(19001, '猕猴桃雨季排水与补钙要点', '检查排水沟、根颈含水量和叶面营养，降低根腐和生理性落果风险。', '5722cfcd93c84a9083720d2cb072c5a0.jpg', 'dev_expert', CURRENT_TIME, NOW()),
(19002, '订单农业采后分级操作建议', '按照采购订单规格完成分级、预冷和包装，减少验收与售后损耗。', '新鲜蔬菜.png', '平台资讯', CURRENT_TIME, NOW()),
(19003, '水稻纹枯病绿色防控清单', '控制田间湿度和种植密度，结合病株识别、科学施药与安全间隔期管理。', 'W020230811400645740814_ORIGIN.jpg', 'dev_expert2', CURRENT_TIME, NOW()),
(19004, '湘西柑橘进入商超采购专区', '泸溪柑橘合作社新增分级包装货源，支持商超采购、冷链配送和订单融资。', '5722cfcd93c84a9083720d2cb072c5a0.jpg', '平台资讯', CURRENT_TIME, NOW());

INSERT INTO tb_discuss (discuss_id, knowledge_id, own_name, content, create_time) VALUES
(19001, 19001, 'dev_farmer', '排水建议很实用，我们准备按地块逐步改造排水沟。', NOW()),
(19002, 19002, 'dev_buyer', '希望分级标准可以继续关联采购订单的验收条件。', NOW()),
(19003, 19003, 'dev_farmer2', '已收藏，准备按清单检查红薯和水稻地块的病虫害。', NOW()),
(19004, 19004, 'dev_buyer2', '商超采购需要稳定规格，后续希望增加每周可供货量。', NOW());

COMMIT;

-- Quick verification queries:
-- SELECT user_name, role, nick_name FROM tb_user WHERE user_name LIKE 'dev_%';
-- SELECT order_id, title, own_name, order_status FROM tb_order WHERE order_id BETWEEN 19001 AND 19040;
-- SELECT finance_id, own_name, status, money FROM tb_finance WHERE finance_id BETWEEN 19001 AND 19040;
