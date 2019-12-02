ALTER TABLE bookstore.users
  CHANGE username username VARCHAR(255) NOT NULL;

ALTER TABLE bookstore.users
  ADD COLUMN auth_provider enum('local','google') DEFAULT 'local' NOT NULL ;