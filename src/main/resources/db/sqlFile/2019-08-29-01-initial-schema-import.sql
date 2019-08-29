CREATE SCHEMA IF NOT EXISTS bookstore;

CREATE TABLE IF NOT EXISTS bookstore.books
(
    id       INT          AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    CONSTRAINT books_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookstore.clients
(
    id       INT          AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    CONSTRAINT clients_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookstore.client_book_log
(
    id       INT          AUTO_INCREMENT,
    client_id INT         NOT NULL,
    book_id   INT         NOT NULL,
    start_date DATE       NOT NULL,
    end_date   DATE,
    CONSTRAINT client_book_log_pk PRIMARY KEY (id),
    CONSTRAINT FK_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT FK_book FOREIGN KEY (book_id) REFERENCES books (id)
);