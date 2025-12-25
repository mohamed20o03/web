-- Add email verification and rejection reason columns to users table

ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verification_sent_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

-- Update existing test user to have verified email
UPDATE users SET email_verified = true WHERE email = 'test@eng.psu.edu.eg';
