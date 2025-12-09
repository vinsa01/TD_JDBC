


CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    creation_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_id INT NOT NULL,
    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
        REFERENCES product(id)
        ON DELETE CASCADE
);
