ALTER TABLE bookstore.books
    CHANGE price price DECIMAL(19,2) DEFAULT 0;

ALTER TABLE bookstore.users
    CHANGE money money DECIMAL(19,2) DEFAULT 0;