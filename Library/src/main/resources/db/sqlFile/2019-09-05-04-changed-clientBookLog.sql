DROP TABLE IF EXISTS client_book_log;

CREATE TABLE bookstore.client_book_log
(
    id       INT          AUTO_INCREMENT,
    client_id INT         NOT NULL,
    book_id   INT         NOT NULL,
    log_date  DATETIME        NOT NULL,
    log_action ENUM('TAKE','RETURN') NOT NULL,
    CONSTRAINT client_book_log_pk PRIMARY KEY (id),
    CONSTRAINT FK_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT FK_book FOREIGN KEY (book_id) REFERENCES books (id)
);