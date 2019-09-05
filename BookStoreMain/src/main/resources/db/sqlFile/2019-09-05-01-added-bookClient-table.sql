CREATE TABLE bookstore.book_client{
    book_id INT,
    client_id INT,
    CONSTRAINT book_client_pk PRIMARY KEY(book_id)
    CONSTRAINT FK_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_clients FOREIGN KEY (client_id) REFERENCES clients (id)
};