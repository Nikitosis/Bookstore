DROP PROCEDURE IF EXISTS deleteUser//
DROP PROCEDURE IF EXISTS deleteBook//

CREATE PROCEDURE deleteUser(IN usernameArg VARCHAR(255))
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.user_role WHERE user_id=userId;
        DELETE FROM bookstore.user_book_log WHERE user_id=userId;
        DELETE FROM bookstore.user_book WHERE user_id=userId;
        DELETE FROM bookstore.user_book_payment_log WHERE user_id=userId;
        DELETE FROM bookstore.users WHERE id=userId;

        COMMIT;
    END //

CREATE PROCEDURE deleteBook(IN bookId INT)
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.user_book WHERE book_id=bookId;
        DELETE FROM bookstore.user_book_log WHERE book_id=bookId;
        DELETE FROM bookstore.user_book_payment_log WHERE book_id=bookId;
        DELETE FROM bookstore.books WHERE id=bookId;

        COMMIT;
    END //