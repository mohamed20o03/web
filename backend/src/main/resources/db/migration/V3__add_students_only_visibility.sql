-- Migration V3: Add students_only visibility option
-- This migration updates the visibility CHECK constraint to include 'students_only'

-- Drop the existing CHECK constraint
ALTER TABLE profiles DROP CONSTRAINT IF EXISTS profiles_visibility_check;

-- Add the new CHECK constraint with students_only option
ALTER TABLE profiles ADD CONSTRAINT profiles_visibility_check 
    CHECK (visibility IN ('public', 'students_only', 'private'));

