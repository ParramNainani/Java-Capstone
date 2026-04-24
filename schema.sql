CREATE DATABASE IF NOT EXISTS online_quiz_db;
USE online_quiz_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS quizzes (
    numeric_quiz_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    time_limit INT DEFAULT 60,
    creator_id INT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (creator_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer VARCHAR(255) NOT NULL,
    question_type VARCHAR(50),
    category VARCHAR(100),
    marks INT DEFAULT 1,
    difficulty_level VARCHAR(50),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(numeric_quiz_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    quiz_id INT,
    score INT NOT NULL,
    total_marks INT NOT NULL,
    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(numeric_quiz_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS responses (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT,
    question_id INT,
    selected_option_id INT,
    short_answer_text TEXT,
    is_correct BOOLEAN,
    FOREIGN KEY (result_id) REFERENCES results(result_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS certificates (
    certificate_id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT,
    issue_date DATE,
    certificate_hash VARCHAR(255) UNIQUE,
    FOREIGN KEY (result_id) REFERENCES results(result_id) ON DELETE CASCADE
);
