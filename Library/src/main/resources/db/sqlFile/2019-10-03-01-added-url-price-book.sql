ALTER TABLE bookstore.books
    ADD url VARCHAR(255),
    ADD daily_price DOUBLE DEFAULT 0;