ALTER TABLE bookstore.users
  MODIFY money DECIMAL(19, 2) NOT NULL DEFAULT 0 ;

ALTER TABLE bookstore.books
  MODIFY price DECIMAL(19,2) NOT NULL DEFAULT 0;