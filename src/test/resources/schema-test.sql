drop table if exists tb_user;
drop table if exists tb_address;
drop table if exists tb_expert;
drop table if exists tb_bank;
drop table if exists tb_finance;
drop table if exists tb_financing_intention;
drop table if exists tb_knowledge;
drop table if exists tb_discuss;
drop table if exists tb_order;
drop table if exists tb_purchase;
drop table if exists tb_purchase_detail;
drop table if exists tb_shoppingcart;
drop table if exists tb_question;
drop table if exists tb_reserve;

create table tb_user (
  user_name varchar(255) primary key,
  password varchar(255) not null,
  nick_name varchar(255),
  phone varchar(255),
  identity_num varchar(255),
  address varchar(255),
  role varchar(255) not null default 'FARMER',
  create_time datetime not null,
  update_time datetime not null,
  integral int default 500,
  credit int default 5,
  avatar varchar(255),
  real_name varchar(255)
);

create table tb_address (
  id int primary key,
  own_name varchar(255) not null,
  consignee varchar(255) not null,
  phone varchar(255) not null,
  address_detail varchar(255) not null,
  is_default int not null
);

create table tb_expert (
  user_name varchar(255) primary key,
  real_name varchar(255) not null,
  phone varchar(255) not null,
  profession varchar(255),
  position varchar(255),
  belong varchar(255)
);

create table tb_bank (
  bank_id int primary key,
  bank_name varchar(255) not null,
  introduce varchar(255),
  bank_phone varchar(255),
  money decimal(65,2),
  rate decimal(65,2),
  repayment varchar(255)
);

create table tb_finance (
  finance_id int primary key,
  bank_id int not null,
  own_name varchar(255),
  real_name varchar(255),
  phone varchar(255),
  id_num varchar(255),
  status int,
  remark varchar(255),
  money decimal(65,2),
  rate decimal(65,2),
  repayment varchar(255),
  create_time datetime not null,
  update_time datetime not null,
  combination_name1 varchar(255),
  combination_phone1 varchar(255),
  combination_idnum1 varchar(255),
  combination_name2 varchar(255),
  combination_phone2 varchar(255),
  combination_idnum2 varchar(255),
  file_info varchar(255)
);

create table tb_financing_intention (
  id int primary key,
  user_name varchar(255) not null,
  real_name varchar(255) not null,
  address varchar(255) not null,
  amount decimal(65,2) not null,
  application varchar(255),
  item varchar(255),
  repayment_period varchar(255),
  area varchar(255),
  phone varchar(255),
  create_time datetime not null,
  update_time datetime not null
);

create table tb_knowledge (
  knowledge_id int primary key,
  title varchar(255) not null,
  content varchar(255) not null,
  pic_path varchar(255),
  own_name varchar(255) not null,
  create_time datetime not null,
  update_time datetime not null
);

create table tb_discuss (
  discuss_id int primary key,
  knowledge_id int not null,
  own_name varchar(255) not null,
  content varchar(255) not null,
  create_time datetime not null
);

create table tb_order (
  order_id int primary key,
  title varchar(255) not null,
  price decimal(65,2),
  content varchar(255) not null,
  order_status int not null default 0,
  type varchar(255) not null,
  picture varchar(255),
  own_name varchar(255) not null,
  cooperation_name varchar(255),
  create_time datetime not null,
  update_time datetime not null,
  address varchar(255)
);

create table tb_purchase (
  purchase_id int primary key,
  own_name varchar(255) not null,
  purchase_type int not null,
  total_price decimal(65,2) not null,
  address varchar(255) not null,
  purchase_status int not null,
  create_time datetime not null,
  update_time datetime not null
);

create table tb_purchase_detail (
  detail_id int primary key,
  purchase_id int not null,
  order_id int not null,
  unin_price decimal(65,2) not null,
  sum_price decimal(65,2) not null,
  count int not null
);

create table tb_shoppingcart (
  shopping_id int primary key,
  order_id int not null,
  count int not null,
  own_name varchar(255) not null,
  create_time datetime not null,
  update_time datetime not null
);

create table tb_question (
  id int primary key,
  expert_name varchar(255) not null,
  questioner varchar(255) not null,
  phone varchar(255) not null,
  plant_name varchar(255) not null,
  title varchar(255) not null,
  question varchar(255),
  answer varchar(255),
  status int not null
);

create table tb_reserve (
  id int primary key,
  expert_name varchar(255) not null,
  questioner varchar(255) not null,
  area varchar(64) not null,
  address varchar(64) not null,
  plant_name varchar(64) not null,
  soil_condition varchar(255) not null,
  plant_condition varchar(255) not null,
  plant_detail varchar(255) not null,
  phone varchar(64) not null,
  message varchar(64),
  answer varchar(64),
  status int not null
);
