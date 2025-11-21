CREATE DATABASE IF NOT EXISTS plagiarism_checker;
USE plagiarism_checker;

DROP TABLE IF EXISTS Results;
DROP TABLE IF EXISTS Documents;
DROP TABLE IF EXISTS Submissions;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    batch_token CHAR(36) NOT NULL,
    guest_token CHAR(36) NULL,
    user_id INT NULL,
    filename VARCHAR(255) NOT NULL,
    raw_content MEDIUMTEXT NOT NULL,
    cleaned_content MEDIUMTEXT NOT NULL,
    status ENUM('QUEUED','PROCESSING','DONE','FAILED') DEFAULT 'QUEUED',
    stack_order INT NOT NULL DEFAULT 0,
    upload_size BIGINT NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL,
    INDEX idx_guest_token (guest_token),
    INDEX idx_batch_token (batch_token)
);

CREATE TABLE Results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    similarity_winnowing DOUBLE NOT NULL,
    similarity_tfidf DOUBLE NOT NULL,
    status ENUM('Plagiarism Suspected','Properly Cited','No Issues') NOT NULL,
    matched_segments JSON NOT NULL,
    source_document VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_result_submission FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE,
    INDEX idx_result_submission (submission_id)
);

CREATE TABLE Documents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    filepath VARCHAR(500) NOT NULL,
    filesize BIGINT NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_docs_owner FOREIGN KEY (owner_id) REFERENCES Users(id) ON DELETE CASCADE,
    INDEX idx_docs_owner (owner_id)
);

INSERT INTO Users (username, password, role) VALUES
    ('admin', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role);
