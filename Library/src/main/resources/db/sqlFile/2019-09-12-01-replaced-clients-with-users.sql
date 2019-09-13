DROP TABLE IF EXISTS bookstore.client_book_log;
DROP TABLE IF EXISTS bookstore.client_book;
DROP TABLE IF EXISTS bookstore.clients;

CREATE TABLE IF NOT EXISTS bookstore.users
(
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS bookstore.user_book
(
    book_id INT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    CONSTRAINT book_user_pk PRIMARY KEY (book_id),
    CONSTRAINT FK_user_book_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_user_book_users FOREIGN KEY (user_id) REFERENCES users (username)
);

CREATE TABLE bookstore.user_book_log
(
    id       INT          AUTO_INCREMENT,
    user_id   VARCHAR(255)    NOT NULL,
    book_id   INT              NOT NULL,
    log_date  DATETIME        NOT NULL,
    log_action ENUM('TAKE','RETURN') NOT NULL,
    CONSTRAINT user_book_log_pk PRIMARY KEY (id),
    CONSTRAINT FK_user_book_log_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_user_book_log_users FOREIGN KEY (user_id) REFERENCES users (username)
);