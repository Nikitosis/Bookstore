ALTER TABLE bookstore.users
  CHANGE auth_provider auth_provider enum('local','google','facebook') DEFAULT 'local' NOT NULL ;