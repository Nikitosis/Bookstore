CREATE PROCEDURE takeBook(IN clientId INT, IN bookId INT)
    BEGIN
        UPDATE bookstore.books SET is_taken=1 WHERE id=bookId;
        INSERT INTO bookstore.client_book_log VALUES (NULL,clientId,bookId,NOW(),NULL);
    END //

CREATE PROCEDURE returnBook(IN clientId INT, IN bookId INT)
    BEGIN
        UPDATE bookstore.books SET is_taken=1
            WHERE id=bookId;
        UPDATE bookstore.client_book_log SET end_date=NOW()
            WHERE client_id=clientId AND book_id=bookId AND end_date IS NULL;
    END //