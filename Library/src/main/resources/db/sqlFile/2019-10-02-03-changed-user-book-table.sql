DROP TABLE IF EXISTS bookstore.user_book;

CREATE TABLE bookstore.user_book
(
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    take_date DATETIME NOT NULL,
    return_date DATETIME,
    CONSTRAINT book_user_pk PRIMARY KEY (user_id,book_id),
    CONSTRAINT FK_user_book_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_user_book_users FOREIGN KEY (user_id) REFERENCES users (id)
);