ALTER TABLE bookstore.books
    ADD url VARCHAR(255) NOT NULL,
    ADD daily_price DOUBLE DEFAULT 0;