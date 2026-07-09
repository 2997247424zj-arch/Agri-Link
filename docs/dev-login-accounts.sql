-- Agri-Link local business testing accounts and interactive seed data.
-- Target database: local/demo rongxiaotong.
-- Do NOT execute this file on production data.
-- All test accounts use BCrypt password hash. Plain password: Test@123456
-- Accounts: dev_admin / dev_farmer / dev_buyer / dev_expert / dev_bank

-- 0. Schema compatibility for older local databases.
-- Current backend reads/writes these columns. Add them when the imported SQL is older.
ALTER TABLE tb_reserve
  ADD COLUMN IF NOT EXISTS appointment_time varchar(64) DEFAULT NULL;

ALTER TABLE tb_reserve
  ADD COLUMN IF NOT EXISTS service_mode varchar(64) DEFAULT NULL;

ALTER TABLE tb_finance
  ADD COLUMN IF NOT EXISTS materials text DEFAULT NULL;

START TRANSACTION;

-- 1. Clean old dev seed data so this script can be executed repeatedly.
DELETE FROM tb_purchase_detail WHERE detail_id BETWEEN 19001 AND 19020 OR purchase_id BETWEEN 19001 AND 19020;
DELETE FROM tb_purchase WHERE purchase_id BETWEEN 19001 AND 19020 OR own_name IN ('dev_buyer', 'dev_farmer');
DELETE FROM tb_sell_purchase WHERE sell_purchase_id BETWEEN 19001 AND 19020 OR purchase_id BETWEEN 19001 AND 19020 OR own_name IN ('dev_buyer', 'dev_farmer');
DELETE FROM tb_shoppingcart WHERE shopping_id BETWEEN 19001 AND 19020 OR own_name = 'dev_buyer' OR order_id BETWEEN 19001 AND 19020;
DELETE FROM tb_finance WHERE finance_id BETWEEN 19001 AND 19020 OR own_name IN ('dev_farmer', 'dev_buyer');
DELETE FROM tb_financing_intention WHERE id BETWEEN 19001 AND 19020 OR user_name IN ('dev_farmer', 'dev_buyer');
DELETE FROM tb_discuss WHERE discuss_id BETWEEN 19001 AND 19020 OR knowledge_id BETWEEN 19001 AND 19020 OR own_name IN ('dev_farmer', 'dev_buyer', 'dev_expert');
DELETE FROM tb_knowledge WHERE knowledge_id BETWEEN 19001 AND 19020 OR own_name IN ('dev_expert', 'Dev News');
DELETE FROM tb_question WHERE id BETWEEN 19001 AND 19020 OR questioner IN ('dev_farmer', 'dev_buyer') OR expert_name = 'dev_expert';
DELETE FROM tb_reserve WHERE id BETWEEN 19001 AND 19020 OR questioner IN ('dev_farmer', 'dev_buyer') OR expert_name = 'dev_expert';
DELETE FROM tb_order WHERE order_id BETWEEN 19001 AND 19020 OR own_name IN ('dev_farmer', 'dev_buyer');
DELETE FROM tb_address WHERE id BETWEEN 19001 AND 19020 OR own_name IN ('dev_buyer', 'dev_farmer');
DELETE FROM tb_expert WHERE user_name = 'dev_expert';
DELETE FROM tb_bank_user WHERE user_name = 'dev_bank';
DELETE FROM tb_bank WHERE bank_id BETWEEN 19001 AND 19020;
DELETE FROM tb_user WHERE user_name IN ('dev_admin', 'dev_farmer', 'dev_buyer', 'dev_expert', 'dev_bank');

-- 2. Role accounts: admin, farmer, buyer, expert and bank.
INSERT INTO tb_user (
  user_name, password, nick_name, phone, identity_num, address, role,
  create_time, update_time, integral, credit, avatar, real_name
) VALUES
('dev_admin',  '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Admin',  '13800009000', '430000199001019000', 'Jishou Admin Office', 'SYSTEM_ADMIN', NOW(), NOW(), 1000, 5, 'dev-admin.png', 'Dev Admin'),
('dev_farmer', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Farmer', '13800009001', '430000199001019001', 'Shibadong Kiwi Base, Huayuan', 'FARMER', NOW(), NOW(), 860, 5, 'dev-farmer.png', 'Shi Xiaoman'),
('dev_buyer',  '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Buyer',  '13800009002', '430000199001019002', 'Changsha Purchase Center', 'BUYER', NOW(), NOW(), 720, 4, 'dev-buyer.png', 'Chen Buyer'),
('dev_expert', '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Expert', '13800009003', '430000199001019003', 'Hunan Agricultural University', 'EXPERT', NOW(), NOW(), 980, 5, 'dev-expert.png', 'Zhou Expert'),
('dev_bank',   '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Dev Bank',   '13800009004', '430000199001019004', 'Xiangxi Rural Commercial Bank', 'BANK', NOW(), NOW(), 900, 5, 'dev-bank.png', 'Lin Manager');

INSERT INTO tb_expert (user_name, real_name, phone, profession, position, belong) VALUES
('dev_expert', 'Zhou Expert', '13800009003', 'Kiwi pest control and soil improvement', 'Senior Agronomist', 'Hunan Agricultural University');

INSERT INTO tb_bank (bank_id, bank_name, introduce, bank_phone, money, rate, repayment) VALUES
(19001, 'Xiangxi AgriBank Dev Branch', 'Credit loan for spring farming, seed, fertilizer, cold-chain and order-based cash flow.', '0743-1900190', 300000.00, 3.85, 12),
(19002, 'Jishou Inclusive Finance Dev Branch', 'Short-term working capital loan for green agricultural product purchase orders.', '0743-1900290', 150000.00, 4.20, 6);

INSERT INTO tb_bank_user (user_name, bank_id, password, real_name, phone, role, create_time, update_time) VALUES
('dev_bank', 19001, '$2a$10$BnbMjtNYz3Ny2fb2DSzcY.G.FUF6fjBVt8/0VwUT54Vw07JqBrrxC', 'Lin Manager', '13800009004', 'BANK', NOW(), NOW());

-- 3. Addresses for buyer/farmer profile, cart and purchase flows.
INSERT INTO tb_address (id, own_name, consignee, phone, address_detail, is_default) VALUES
(19001, 'dev_buyer',  'Chen Buyer',  '13800009002', 'Changsha Yuhua Agri Purchase Warehouse A1', 1),
(19002, 'dev_buyer',  'Chen Buyer',  '13800009002', 'Jishou Cold Chain Distribution Center B2', 0),
(19003, 'dev_farmer', 'Shi Xiaoman', '13800009001', 'Shibadong Kiwi Demonstration Base', 1);

-- 4. Trade orders, cart and purchases for visual checks in trade/cart/profile/admin pages.
INSERT INTO tb_order (
  order_id, title, price, content, order_status, type, picture,
  own_name, cooperation_name, create_time, update_time, address,
  stock, spec, unit, min_purchase
) VALUES
(19001, 'Selenium-rich Kiwi 20kg', 8.60, 'Fresh seasonal kiwi for group buying and supermarket trial sale.', 1, 'Fruit', 'dev-kiwi.jpg', 'dev_farmer', 'dev_buyer', NOW(), NOW(), 'Huayuan, Xiangxi', 1200, '20kg/box', 'jin', 20),
(19002, 'Mountain Eco Rice 50kg', 5.20, 'Terrace rice dried at low temperature, suitable for bulk purchase.', 0, 'Grain', 'dev-rice.jpg', 'dev_farmer', NULL, NOW(), NOW(), 'Longshan, Xiangxi', 800, '50kg/bag', 'jin', 50),
(19003, 'Orange Sorting and Packing Service', 2.50, 'Sorting, bagging and boxing service for peak harvest season.', 2, 'Service', 'dev-orange-pack.jpg', 'dev_farmer', 'dev_buyer', NOW(), NOW(), 'Luxi, Xiangxi', 3000, 'priced by jin', 'jin', 100);

INSERT INTO tb_shoppingcart (shopping_id, order_id, count, own_name, create_time, update_time) VALUES
(19001, 19001, 2, 'dev_buyer', NOW(), NOW()),
(19002, 19002, 1, 'dev_buyer', NOW(), NOW());

INSERT INTO tb_purchase (purchase_id, own_name, purchase_type, total_price, address, purchase_status, create_time, update_time, cancel_reason, delivery_no) VALUES
(19001, 'dev_buyer', 1, 344.00, 'Changsha Yuhua Agri Purchase Warehouse A1', 1, NOW(), NOW(), NULL, 'DEV-WL-19001'),
(19002, 'dev_buyer', 1, 260.00, 'Jishou Cold Chain Distribution Center B2', 0, NOW(), NOW(), NULL, NULL);

INSERT INTO tb_purchase_detail (detail_id, purchase_id, order_id, unin_price, sum_price, count) VALUES
(19001, 19001, 19001, 8.60, 344.00, 40),
(19002, 19002, 19002, 5.20, 260.00, 50);

INSERT INTO tb_sell_purchase (sell_purchase_id, purchase_id, own_name, purchase_type, unin_pricee, sum_price, address, purchase_status, create_time, update_time, order_id) VALUES
(19001, 19001, 'dev_farmer', 1, 8.60, 344.00, 'Changsha Yuhua Agri Purchase Warehouse A1', 1, NOW(), NOW(), 19001),
(19002, 19002, 'dev_farmer', 1, 5.20, 260.00, 'Jishou Cold Chain Distribution Center B2', 0, NOW(), NOW(), 19002);

-- 5. Finance applications and intentions for farmer/bank/admin interactions.
INSERT INTO tb_finance (
  finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
  money, rate, repayment, create_time, update_time,
  combination_name1, combination_phone1, combination_idnum1,
  combination_name2, combination_phone2, combination_idnum2,
  file_info, materials
) VALUES
(19001, 19001, 'dev_farmer', 'Shi Xiaoman', '13800009001', '430000199001019001', 0, 'Waiting for bank review of spring farming purchase contract', 80000.00, 3.85, 12, NOW(), NOW(), 'Chen Buyer', '13800009002', '430000199001019002', NULL, NULL, NULL, '/files/dev-finance-contract.pdf', 'ID card; land lease contract; three-month trade records'),
(19002, 19002, 'dev_farmer', 'Shi Xiaoman', '13800009001', '430000199001019001', 1, 'Credit approved for cold-chain turnover', 50000.00, 4.20, 6, NOW(), NOW(), NULL, NULL, NULL, NULL, NULL, NULL, '/files/dev-cold-chain.pdf', 'Purchase order contract; delivery address proof');

INSERT INTO tb_financing_intention (id, user_name, real_name, address, amount, application, item, repayment_period, area, phone, create_time, update_time) VALUES
(19001, 'dev_farmer', 'Shi Xiaoman', 'Shibadong Village, Huayuan', 80000, 'Spring fertilizer and irrigation upgrade', 'Kiwi base expansion', 12, 35, '13800009001', NOW(), NOW()),
(19002, 'dev_buyer', 'Chen Buyer', 'Yuhua District, Changsha', 50000, 'Purchase order turnover fund', 'Community group buying stock', 6, 5, '13800009002', NOW(), NOW());

-- 6. Expert Q&A and reservations for farmer/expert workflow checks.
INSERT INTO tb_question (id, expert_name, questioner, phone, plant_name, title, question, answer, status, attachments) VALUES
(19001, 'dev_expert', 'dev_farmer', '13800009001', 'Kiwi', 'Leaves turn yellow with fruit drop', 'The south slope kiwi leaves are yellow after heavy rain. Should we improve drainage or add calcium?', 'Open drainage ditches first, spray calcium-magnesium foliar fertilizer, and sample roots for rot check.', 1, 'dev-question-leaf.jpg'),
(19002, 'dev_expert', 'dev_farmer', '13800009001', 'Rice', 'Partial lodging in rice field', 'Some mountain rice plants lodged after continuous rain. Can this still be rescued?', NULL, 0, NULL);

INSERT INTO tb_reserve (
  id, expert_name, questioner, area, address, plant_name,
  soil_condition, plant_condition, plant_detail, phone, message, answer,
  status, appointment_time, service_mode
) VALUES
(19001, 'dev_expert', 'dev_farmer', '35 mu', 'Shibadong Demonstration Base', 'Kiwi', 'Acid yellow soil with waterlogging after rain', 'Yellow leaves and slight fruit drop', 'Three-year-old trees, worse in low south slope', '13800009001', 'Need onsite guidance this weekend', 'Saturday morning is available. Prepare soil test records first.', 1, '2026-07-12 09:30', 'Onsite'),
(19002, 'dev_expert', 'dev_farmer', '20 mu', 'Longshan terrace field', 'Rice', 'High organic matter and good water retention', 'Partial lodging', 'Lodging after heading stage', '13800009001', 'Need remote photo diagnosis', NULL, 0, '2026-07-13 15:00', 'Video');

-- 7. Knowledge articles and comments for public knowledge and admin content checks.
INSERT INTO tb_knowledge (knowledge_id, title, content, pic_path, own_name, create_time, update_time) VALUES
(19001, 'Rainy-season drainage and calcium supplement for kiwi', 'Check ditches, root collar moisture and foliar nutrition to reduce root rot and physiological fruit drop.', 'dev-knowledge-kiwi.jpg', 'dev_expert', CURRENT_TIME, NOW()),
(19002, 'Post-harvest grading for order agriculture', 'Grade, pre-cool and pack products according to purchase order specs to reduce after-sale loss.', 'dev-knowledge-order.jpg', 'Dev News', CURRENT_TIME, NOW());

INSERT INTO tb_discuss (discuss_id, knowledge_id, own_name, content, create_time) VALUES
(19001, 19001, 'dev_farmer', 'This drainage advice is useful for our base. We will improve ditches by block.', NOW()),
(19002, 19002, 'dev_buyer', 'It would be better if grading standards can link to purchase order acceptance.', NOW());

COMMIT;

-- Quick verification queries:
-- SELECT user_name, role, nick_name FROM tb_user WHERE user_name LIKE 'dev_%';
-- SELECT order_id, title, own_name, order_status FROM tb_order WHERE order_id BETWEEN 19001 AND 19020;
-- SELECT finance_id, own_name, status, money FROM tb_finance WHERE finance_id BETWEEN 19001 AND 19020;
