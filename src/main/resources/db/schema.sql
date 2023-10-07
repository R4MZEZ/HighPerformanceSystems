drop table breeds;

create table if not exists breeds
(
    id serial PRIMARY KEY,
    name varchar(50) NOT NULL
)

-- CREATE TABLE product_discount
-- (
--     serial    id PRIMARY KEY,
--     varchar(50) name_discount NOT NULL,
--     boolean   enabled       NOT NULL,
--     timestamp creation_date NOT NULL
-- )
-- CREATE TABLE product_group
-- (
--     serial    id PRIMARY KEY,
--     varchar(50) product_group NOT NULL,
--     timestamp creation_date DEFAULT now() NOT NULL
-- )