CREATE DATABASE IF NOT EXISTS plagiarism_checker;
USE plagiarism_checker;

CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    filename VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS Results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    compared_with INT,
    similarity FLOAT,
    FOREIGN KEY (submission_id) REFERENCES Submissions(id)
);

INSERT INTO Users (username, password) VALUES
    ("admin", "admin123")
ON DUPLICATE KEY UPDATE password = VALUES(password);
