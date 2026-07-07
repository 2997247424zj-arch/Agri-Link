insert into tb_user (
  user_name, password, nick_name, phone, identity_num, address, role,
  create_time, update_time, integral, credit, avatar, real_name
) values (
  'farmer001', 'secret', 'Farmer One', '13800000001', '430000199001010001',
  'Jishou', 'FARMER', '2026-01-01 08:00:00', '2026-01-02 08:00:00',
  500, 5, '/files/avatar.png', 'Zhang San'
);

insert into tb_user (
  user_name, password, nick_name, phone, identity_num, address, role,
  create_time, update_time, integral, credit, avatar, real_name
) values (
  'admin001', 'secret', 'System Admin', '13800000000', '430000198001010001',
  'Jishou', 'SYSTEM_ADMIN', '2026-01-01 07:00:00', '2026-01-02 07:00:00',
  0, 0, '/files/avatar.png', 'Admin User'
);

insert into tb_address (
  id, own_name, consignee, phone, address_detail, is_default
) values (
  1, 'farmer001', 'Zhang San', '13800000001', 'Jishou address detail', 1
);

insert into tb_expert (
  user_name, real_name, phone, profession, position, belong
) values (
  'expert001', 'Li Expert', '13800000002', 'plant protection', 'senior agronomist', 'Jishou University'
);

insert into tb_bank (
  bank_id, bank_name, introduce, bank_phone, money, rate, repayment
) values (
  1001, 'Agri Bank', 'Agricultural loan product', '07430000001', 100000.00, 3.50, '12 months'
);

insert into tb_finance (
  finance_id, bank_id, own_name, real_name, phone, id_num, status, remark,
  money, rate, repayment, create_time, update_time, combination_name1,
  combination_phone1, combination_idnum1, combination_name2, combination_phone2,
  combination_idnum2, file_info
) values (
  1, 1001, 'farmer001', 'Zhang San', '13800000001', '430000199001010001',
  0, 'pending review', 50000.00, 3.50, '12 months',
  '2026-01-01 11:00:00', '2026-01-02 11:00:00',
  'Li Si', '13800000003', '430000199101010001',
  'Wang Wu', '13800000004', '430000199201010001', '/files/finance.pdf'
);

insert into tb_financing_intention (
  id, user_name, real_name, address, amount, application, item,
  repayment_period, area, phone, create_time, update_time
) values (
  1, 'farmer001', 'Zhang San', 'Jishou', 30000.00, 'expand production',
  'orange orchard', '12 months', '20 mu', '13800000001',
  '2026-01-01 12:00:00', '2026-01-02 12:00:00'
);

insert into tb_knowledge (
  knowledge_id, title, content, pic_path, own_name, create_time, update_time
) values (
  1, 'Rice planting', 'Water and fertilizer management', '/files/rice.png',
  'expert001', '2026-01-01 09:00:00', '2026-01-02 09:00:00'
);

insert into tb_discuss (
  discuss_id, knowledge_id, own_name, content, create_time
) values (
  1, 1, 'farmer001', 'Useful article', '2026-01-03 09:00:00'
);

insert into tb_order (
  order_id, title, price, content, order_status, type, picture,
  own_name, cooperation_name, create_time, update_time, address
) values (
  1, 'Fresh oranges', 6.50, 'Sell fresh oranges', 0, '1', '/files/orange.png',
  'farmer001', null, '2026-01-01 10:00:00', '2026-01-02 10:00:00', 'Jishou'
);

insert into tb_purchase (
  purchase_id, own_name, purchase_type, total_price, address, purchase_status,
  create_time, update_time
) values (
  1, 'farmer001', 1, 65.00, 'Jishou', 0,
  '2026-01-01 13:00:00', '2026-01-02 13:00:00'
);

insert into tb_purchase_detail (
  detail_id, purchase_id, order_id, unin_price, sum_price, count
) values (
  1, 1, 1, 6.50, 65.00, 10
);

insert into tb_shoppingcart (
  shopping_id, order_id, count, own_name, create_time, update_time
) values (
  1, 1, 3, 'farmer001', '2026-01-01 14:00:00', '2026-01-02 14:00:00'
);

insert into tb_question (
  id, expert_name, questioner, phone, plant_name, title, question, answer, status
) values (
  1, 'expert001', 'farmer001', '13800000001', 'rice', 'Leaf yellowing',
  'Why are leaves yellow?', 'Check fertilizer and drainage.', 1
);

insert into tb_reserve (
  id, expert_name, questioner, area, address, plant_name, soil_condition,
  plant_condition, plant_detail, phone, message, answer, status
) values (
  1, 'expert001', 'farmer001', '20 mu', 'Jishou', 'rice',
  'acid soil', 'leaf yellowing', 'seedling stage', '13800000001',
  'Need onsite guidance', 'Reserve accepted', 1
);
