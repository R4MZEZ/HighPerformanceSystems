CREATE TABLE product_entity
(
    serial    id PRIMARY KEY,
    varchar(50) name_product NOT NULL,
    integer   price            NOT NULL,
    serial    product_group_id NOT NULL,
    serial    product_discount_id,
    timestamp creation_date    NOT NULL
)
CREATE TABLE product_discount
(
    serial    id PRIMARY KEY,
    varchar(50) name_discount NOT NULL,
    boolean   enabled       NOT NULL,
    timestamp creation_date NOT NULL
)
CREATE TABLE product_group
(
    serial    id PRIMARY KEY,
    varchar(50) product_group NOT NULL,
    timestamp creation_date DEFAULT now() NOT NULL
)