alter table owners alter column balance drop not null;
alter table owners alter column reserved_balance drop not null;

alter table owners alter column user_id set not null;
alter table dogs alter column user_id set not null;
