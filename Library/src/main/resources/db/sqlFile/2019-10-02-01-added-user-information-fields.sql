ALTER TABLE bookstore.users
 ADD country VARCHAR(255),
 ADD city VARCHAR(255),
 ADD gender ENUM('MALE','FEMALE'),
 ADD email VARCHAR(255),
 ADD phone VARCHAR(12),
 ADD avatar_link VARCHAR(255);
