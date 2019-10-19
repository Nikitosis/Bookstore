ALTER TABLE bookstore.users
    ADD is_email_verified BOOLEAN DEFAULT 0,
    ADD verification_token VARCHAR(255);