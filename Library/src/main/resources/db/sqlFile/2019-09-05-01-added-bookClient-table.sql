CREATE TABLE IF NOT EXISTS bookstore.client_book
(
    book_id INT NOT NULL,
    client_id INT NOT NULL,
    CONSTRAINT book_client_pk PRIMARY KEY (book_id),
    CONSTRAINT FK_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_clients FOREIGN KEY (client_id) REFERENCES clients (id)
);