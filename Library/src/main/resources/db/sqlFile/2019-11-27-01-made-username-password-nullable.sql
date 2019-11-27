ALTER TABLE bookstore.users
  CHANGE username username VARCHAR(255);

ALTER TABLE bookstore.users
  CHANGE password password VARCHAR(255);

ALTER TABLE bookstore.users
  DROP INDEX username;