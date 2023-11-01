create table product_wcode (id bigint primary key, name varchar(255) not null, price int not null, quantity int not null, indate datetime, actdate datetime);
create table movements (id int primary key auto_increment, productId bigint not null, productName varchar(255), type varchar(255), date datetime);
create table user (id int primary key auto_increment, name varchar(255), username varchar(255), password varchar(255), role varchar(255), indate datetime);
create table movements_user (id int primary key auto_increment, username varchar(255), type varchar(255), date datetime);
create table product (id int primary key auto_increment, name varchar(255) not null, price int not null, quantity int not null, indate datetime, actdate datetime);
