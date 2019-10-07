ALTER TABLE bookstore.books
 CHANGE url file_path VARCHAR(255);

ALTER TABLE bookstore.books
 CHANGE daily_price price DOUBLE DEFAULT 0;