DROP PROCEDURE IF EXISTS takeBook//
DROP PROCEDURE IF EXISTS returnBook//
DROP PROCEDURE IF EXISTS deleteClient//
DROP PROCEDURE IF EXISTS deleteBook//

CREATE PROCEDURE takeBook(IN clientId INT, IN bookId INT)
    BEGIN
        START TRANSACTION;

        UPDATE bookstore.books SET is_taken=1 WHERE id=bookId;
        INSERT INTO bookstore.client_book_log VALUES (NULL,clientId,bookId,NOW(),NULL);

        COMMIT;
    END //

CREATE PROCEDURE returnBook(IN clientId INT, IN bookId INT)
    BEGIN
        START TRANSACTION;

        UPDATE bookstore.books SET is_taken=0
            WHERE id=bookId;
        UPDATE bookstore.client_book_log SET end_date=NOW()
            WHERE client_id=clientId AND book_id=bookId AND end_date IS NULL;

        COMMIT;
    END //

CREATE PROCEDURE deleteClient(IN clientId INT)
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.client_book_log WHERE client_id=clientId;
        DELETE FROM bookstore.clients WHERE id=clientId;

        COMMIT;
    END //

CREATE PROCEDURE deleteBook(IN bookId INT)
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.client_book_log WHERE book_id=bookId;
        DELETE FROM bookstore.books WHERE id=bookId;

        COMMIT;
    END //
