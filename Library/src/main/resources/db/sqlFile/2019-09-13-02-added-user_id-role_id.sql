DROP TABLE IF EXISTS bookstore.user_book_log;
DROP TABLE IF EXISTS bookstore.user_book;
DROP TABLE IF EXISTS bookstore.user_role;
DROP TABLE IF EXISTS bookstore.users;
DROP TABLE IF EXISTS bookstore.roles;

CREATE TABLE bookstore.users
(
    id         INT          AUTO_INCREMENT,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    CONSTRAINT users_pk PRIMARY KEY (id),
    UNIQUE(username)
);

CREATE TABLE bookstore.user_book
(
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT book_user_pk PRIMARY KEY (book_id),
    CONSTRAINT FK_user_book_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_user_book_users FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE bookstore.user_book_log
(
    id       INT          AUTO_INCREMENT,
    user_id   INT           NOT NULL,
    book_id   INT              NOT NULL,
    log_date  DATETIME        NOT NULL,
    log_action ENUM('TAKE','RETURN') NOT NULL,
    CONSTRAINT user_book_log_pk PRIMARY KEY (id),
    CONSTRAINT FK_user_book_log_books FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT FK_user_book_log_users FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE bookstore.roles(
    id    INT          AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT roles_pk PRIMARY KEY (id)
);

CREATE TABLE bookstore.user_role(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT FK_user_role_users FOREIGN KEY (user_id) REFERENCES bookstore.users (id),
    CONSTRAINT FK_user_role_roles FOREIGN KEY (role_id) REFERENCES bookstore.roles (id),
    UNIQUE (user_id, role_id)
);