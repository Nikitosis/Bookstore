ALTER TABLE bookstore.users
    ADD is_email_verified BOOLEAN NOT NULL DEFAULT 0,
    ADD verification_token VARCHAR(255);