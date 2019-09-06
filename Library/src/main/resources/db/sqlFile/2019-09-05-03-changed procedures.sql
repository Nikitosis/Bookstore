DROP PROCEDURE IF EXISTS takeBook//
DROP PROCEDURE IF EXISTS returnBook//
DROP PROCEDURE IF EXISTS deleteClient//
DROP PROCEDURE IF EXISTS deleteBook//


CREATE PROCEDURE deleteClient(IN clientId INT)
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.client_book WHERE client_id=clientId;
        DELETE FROM bookstore.clients WHERE id=clientId;

        COMMIT;
    END //

CREATE PROCEDURE deleteBook(IN bookId INT)
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.client_book WHERE book_id=bookId;
        DELETE FROM bookstore.books WHERE id=bookId;

        COMMIT;
    END //
