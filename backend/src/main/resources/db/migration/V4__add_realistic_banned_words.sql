-- Migration V4: Add realistic banned words for content moderation
-- This migration adds appropriate banned words for a university campus management system

-- Remove placeholder words first (if they exist)
DELETE FROM banned_words WHERE word IN ('badword1', 'badword2');

-- Add realistic banned words for university context
-- These words cover inappropriate language, hate speech, and offensive content
INSERT INTO banned_words (word) VALUES
-- Inappropriate/Offensive Language
('fuck'),
('shit'),
('bitch'),
('asshole'),
('damn'),
('hell'),

-- Hate Speech / Discrimination
('hate'),
('violence'),
('kill'),
('die'),

-- Spam/Scam related
('spam'),
('scam'),
('fraud'),
('cheat'),

-- Already existing (keeping them)
('offensive'),
('inappropriate')

ON CONFLICT (word) DO NOTHING;

-- Note: The words are stored in lowercase for case-insensitive matching
-- The ContentModerationService converts all text to lowercase before checking

