DROP PROCEDURE IF EXISTS deleteClient//
DROP PROCEDURE IF EXISTS deleteBook//

CREATE PROCEDURE deleteUser(IN usernameArg VARCHAR(255))
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.user_book WHERE user_id=usernameArg;
        DELETE FROM bookstore.users WHERE username=usernameArg;

        COMMIT;
    END //

CREATE PROCEDURE deleteBook(IN bookId INT)
    BEGIN
        START TRANSACTION;

        DELETE FROM bookstore.user_book WHERE book_id=bookId;
        DELETE FROM bookstore.books WHERE id=bookId;

        COMMIT;
    END //
