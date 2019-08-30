CREATE PROCEDURE deleteClient(IN clientId INT)
    BEGIN
        DELETE FROM bookstore.client_book_log WHERE client_id=clientId;
        DELETE FROM bookstore.clients WHERE id=clientId;
    END //

CREATE PROCEDURE deleteBook(IN bookId INT)
    BEGIN
        DELETE FROM bookstore.client_book_log WHERE book_id=bookId;
        DELETE FROM bookstore.books WHERE id=bookId;
    END //