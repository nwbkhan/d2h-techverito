create user d2h_user with password 'd2h_user';
create database d2h_db;
grant all privileges on database d2h_db to d2h_user;

drop table subscription_pack_channels;
create table subscription_pack_channels
(
  subscription_pack_type varchar(255) not null constraint dsadksfkd references subscription_packs (pack_name),
  channel_id             bigint       not null
    constraint fkmth5o9pr93t6g8f2puy2d2pc2
    references channels
);

insert into channels (channel_name, price)
values ('Zee', 10),
       ('Sony', 15),
       ('Star Plus', 20),
       ('Discovery', 10),
       ('NatGeo', 20);

INSERT INTO subscription_packs (discount, pack_name, price)
VALUES (10, 'Silver', 50),
       (10, 'Gold', 100);


INSERT INTO service_subscriptions (price, service_name)
VALUES (200, 'LearnEnglish'),
       (100, 'LearnCooking');

insert into subscription_pack_channels (subscription_pack_type, channel_id)
VALUES ('Silver', 1),
       ('Silver', 2),
       ('Silver', 3),
       ('Gold', 1),
       ('Gold', 2),
       ('Gold', 3),
       ('Gold', 4),
       ('Gold', 5);
