-- Version 1.0 - Simple Schema for Student Profile System


CREATE TABLE faculties (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(500),
    years_numbers INT NOT NULL
);

CREATE INDEX idx_faculty_name ON faculties(name);

-- ============================================
-- 2. DEPARTMENTS TABLE
-- ============================================
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    faculty_id INT NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE CASCADE,
    UNIQUE(name, faculty_id)
);

CREATE INDEX idx_department_name ON departments(name);
CREATE INDEX idx_department_faculty_id ON departments(faculty_id);

-- ============================================
-- 3. USERS TABLE
-- ============================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    national_id VARCHAR(50) UNIQUE NOT NULL,
    national_id_scan VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'student' NOT NULL CHECK (role IN ('student', 'admin')),
    status VARCHAR(20) DEFAULT 'pending' NOT NULL CHECK (status IN ('pending', 'approved', 'rejected')),
    email_verified BOOLEAN DEFAULT false NOT NULL,
    rejection_reason TEXT,
    year INT NOT NULL,
    faculty_id INT NOT NULL,
    department_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_status ON users(status);
CREATE INDEX idx_role ON users(role);
CREATE INDEX idx_department_id ON users(department_id);

-- ============================================
-- 4. PROFILES TABLE
-- ============================================
CREATE TABLE profiles (
    id SERIAL PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    profile_photo VARCHAR(255),
    bio TEXT,
    phone VARCHAR(20),
    linkedin VARCHAR(255),
    github VARCHAR(255),
    interests TEXT,
    visibility VARCHAR(20) DEFAULT 'public' NOT NULL CHECK (visibility IN ('public', 'private')),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_id ON profiles(user_id);
CREATE INDEX idx_visibility ON profiles(visibility);

-- ============================================
-- 5. BANNED WORDS TABLE
-- ============================================
CREATE TABLE banned_words (
    id SERIAL PRIMARY KEY,
    word VARCHAR(100) UNIQUE NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_word ON banned_words(word);

-- ============================================
-- 6. FLAGGED CONTENT TABLE
-- ============================================
CREATE TABLE flagged_content (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    flagged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_id_flagged ON flagged_content(user_id);
CREATE INDEX idx_flagged_at ON flagged_content(flagged_at);

-- ============================================
-- All Faculties (IDs 1-12)
INSERT INTO faculties (id, name, description, years_numbers) VALUES
    (1, 'Faculty of Engineering', 'Faculty of Engineering at Port Said University', 5),
    (2, 'Faculty of Commerce', 'Faculty of Commerce', 4),
    (3, 'Faculty of Science', 'Faculty of Science', 4),
    (4, 'Faculty of Education', 'Faculty of Education', 4),
    (5, 'Faculty of Specific Education', 'Faculty of Specific Education', 4),
    (6, 'Faculty of Physical Education', 'Faculty of Physical Education', 4),
    (7, 'Faculty of Nursing', 'Faculty of Nursing', 5),
    (8, 'Faculty of Early Childhood Education', 'Faculty of Early Childhood Education', 4),
    (9, 'Faculty of Arts', 'Faculty of Arts', 4),
    (10, 'Faculty of Law', 'Faculty of Law', 4),
    (11, 'Faculty of Management Technology and Information Systems', 'Faculty of Management Technology and Information Systems', 4),
    (12, 'Faculty of Pharmacy', 'Faculty of Pharmacy', 5);

-- ============================================
-- Port Said University Faculties/Departments
-- 1. Faculty of Engineering
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Civil Engineering', 'Civil Engineering Department', 1),
    ('Electrical Power Engineering', 'Electrical Power Engineering Department', 1),
    ('Communication and Electronics Engineering', 'Communication and Electronics Engineering Department', 1),
    ('Computer and Control Engineering', 'Computer and Control Engineering Department', 1),
    ('Mechanical Power Engineering', 'Mechanical Power Engineering Department', 1),
    ('Mechanical Production Engineering', 'Mechanical Production Engineering Department', 1),
    ('Architecture Engineering', 'Architecture Engineering Department', 1),
    ('Naval Architecture and Marine Engineering', 'Naval Architecture and Marine Engineering Department', 1);

-- 2. Faculty of Commerce
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Accounting', 'Accounting Department', 2),
    ('Business Administration', 'Business Administration Department', 2),
    ('Economics', 'Economics Department', 2),
    ('Finance', 'Finance Department', 2);

-- 3. Faculty of Science
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Mathematics', 'Mathematics Department', 3),
    ('Physics', 'Physics Department', 3),
    ('Chemistry', 'Chemistry Department', 3),
    ('Biology', 'Biology Department', 3),
    ('Geology', 'Geology Department', 3);

-- 4. Faculty of Education
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Mathematics Education', 'Mathematics Education Department', 4),
    ('Science Education', 'Science Education Department', 4),
    ('Language Education', 'Language Education Department', 4),
    ('Social Studies Education', 'Social Studies Education Department', 4);

-- 5. Faculty of Specific Education
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Art Education', 'Art Education Department', 5),
    ('Music Education', 'Music Education Department', 5),
    ('Home Economics', 'Home Economics Department', 5);

-- 6. Faculty of Physical Education
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Sports Training', 'Sports Training Department', 6),
    ('Sports Management', 'Sports Management Department', 6),
    ('Exercise Science', 'Exercise Science Department', 6);

-- 7. Faculty of Nursing
INSERT INTO departments (name, description, faculty_id) VALUES
    ('General Nursing', 'General Nursing Department', 7);

-- 8. Faculty of Early Childhood Education
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Early Childhood Education', 'Early Childhood Education Department', 8);

-- 9. Faculty of Arts
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Arabic Language', 'Arabic Language Department', 9),
    ('English Language and Literature', 'English Language and Literature Department', 9),
    ('History', 'History Department', 9),
    ('Geography', 'Geography Department', 9);

-- 10. Faculty of Law
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Civil Law', 'Civil Law Department', 10),
    ('Criminal Law', 'Criminal Law Department', 10),
    ('Public Law', 'Public Law Department', 10);

-- 11. Faculty of Management Technology and Information Systems
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Information Systems', 'Information Systems Department', 11),
    ('Management Technology', 'Management Technology Department', 11),
    ('Cybersecurity', 'Cybersecurity Department', 11);

-- 12. Faculty of Pharmacy
INSERT INTO departments (name, description, faculty_id) VALUES
    ('Pharmacology', 'Pharmacology Department', 12),
    ('Pharmaceutical Chemistry', 'Pharmaceutical Chemistry Department', 12),
    ('Clinical Pharmacy', 'Clinical Pharmacy Department', 12);


-- ============================================
-- SEED DATA - Sample Banned Words
-- ============================================
INSERT INTO banned_words (word) VALUES 
('spam'),
('offensive'),
('inappropriate'),
('badword1'),
('badword2');

-- ============================================
-- SEED DATA - Sample Test User
-- ============================================


