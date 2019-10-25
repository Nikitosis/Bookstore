CREATE TABLE bookstore.user_book_payment_log(
     id         int auto_increment primary key,
    user_id    int                     not null,
    book_id    int                     not null,
    log_date   datetime                not null,
    payment    decimal(19, 2)          not null,
    constraint FK_user_book_payment_log_books
        foreign key (book_id) references books (id),
    constraint FK_user_book_payment_log_users
        foreign key (user_id) references users (id)
)