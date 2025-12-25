# CampusCard Admin Guide

Complete guide for administrators managing the CampusCard platform.

## Table of Contents

- [Admin Overview](#admin-overview)
- [Accessing the Admin Panel](#accessing-the-admin-panel)
- [Dashboard](#dashboard)
- [User Management](#user-management)
- [Approval Workflow](#approval-workflow)
- [Content Moderation](#content-moderation)
- [Role Management](#role-management)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Admin Overview

As a CampusCard administrator, you have complete control over the platform including:

- **User Approval**: Review and approve/reject student registrations
- **Email Verification**: Verify user email addresses
- **Content Moderation**: Manage banned words and review flagged content
- **Role Management**: Promote users to admin or demote admins
- **Dashboard Analytics**: View platform statistics and metrics
- **Profile Access**: View all user profiles regardless of visibility settings

---

## Accessing the Admin Panel

### Login

1. Navigate to the CampusCard login page: `http://your-domain.com/login`
2. Enter your admin email and password
3. Click "Log In"
4. You will be automatically redirected to the admin dashboard

### First-Time Login

The default admin account is created automatically when the application first starts:

- **Email**: Value from `ADMIN_EMAIL` environment variable
- **Password**: Value from `ADMIN_PASSWORD` environment variable

**⚠️ IMPORTANT**: Change the default admin password immediately after first login!

### Changing Your Password

Currently password change is not implemented in the UI. To change admin password:

1. Use a password hashing tool:
   ```bash
   # Using BCrypt online tool or:
   ./mvnw spring-boot:run
   # In logs, find BCrypt hash generator
   ```

2. Update database directly:
   ```sql
   UPDATE users 
   SET password = '$2a$10$NEW_HASH_HERE' 
   WHERE email = 'admin@eng.psu.edu.eg';
   ```

---

## Dashboard

The admin dashboard provides an overview of platform statistics.

### Accessing the Dashboard

- **URL**: `/admin`
- **Navigation**: Click "Dashboard" in the admin menu

### Dashboard Metrics

The dashboard displays:

#### User Statistics
- **Total Users**: All registered users (students + admins)
- **Pending Users**: Users awaiting approval
- **Approved Users**: Active students with approved status
- **Rejected Users**: Users whose registration was rejected
- **Total Admins**: Number of administrator accounts

#### Faculty Distribution
- Visual breakdown of students by faculty
- Helps identify popular faculties and departments

### Interpreting Dashboard Data

**High Pending Count**: 
- Indicates many unreviewed registrations
- Action: Review pending users regularly

**High Rejection Rate**:
- May indicate unclear registration requirements
- Action: Review rejection reasons for patterns

**Uneven Faculty Distribution**:
- Normal - reflects actual university enrollment
- Ensure all faculties have proper departments configured

---

## User Management

### Viewing All Users

1. Go to "Pending Users" page: `/admin/users`
2. View list of users awaiting approval
3. Click on a user to see detailed information

### User Information

For each user, you can view:

**Personal Information**:
- Full name (first name + last name)
- Email address
- Date of birth
- National ID number

**Academic Information**:
- Faculty
- Department
- Academic year

**Account Status**:
- Registration status (PENDING, APPROVED, REJECTED)
- Email verification status
- Role (STUDENT, ADMIN)

**Documents**:
- Profile photo (if uploaded)
- National ID scan (required for approval)

---

## Approval Workflow

The approval workflow ensures only verified university students can access the platform.

### Reviewing Pending Users

1. Navigate to "Pending Users" (`/admin/users`)
2. You'll see a list of all users with `PENDING` status
3. Click "Review" on any user to see their details

### Review Page

The review page (`/admin/review/:userId`) displays:

**Left Column**:
- User personal information
- Academic information
- Account details
- Email verification status

**Right Column**:
- Profile photo preview
- National ID scan preview
- Comparison view for verification

### Verification Checklist

Before approving a user, verify:

- [ ] Email address follows university format (`@eng.psu.edu.eg`)
- [ ] National ID is 14 digits
- [ ] Profile photo matches ID photo
- [ ] ID scan is clear and readable
- [ ] Academic information (faculty/department) is correct
- [ ] Email is verified (green checkmark)

### Email Verification

**Manual Email Verification**:
1. On the review page, check if email is verified
2. If not verified, click "Verify Email" button
3. Email will be marked as verified manually

**Automatic Email Verification** (if SMTP configured):
1. User receives verification email after registration
2. User clicks verification link
3. Email automatically marked as verified

### Approving a User

1. Review all user information
2. Ensure email is verified (required)
3. Click "Approve User" button
4. User status changes to `APPROVED`
5. User can now fully access the platform
6. Approval email sent (if SMTP configured)

**Post-Approval**:
- User can login and access all features
- Profile becomes visible (based on visibility setting)
- User appears in student directory (if public)

### Rejecting a User

1. Review user information
2. Determine reason for rejection
3. Click "Reject User" button
4. Enter rejection reason in the dialog
5. Click "Confirm Rejection"

**Rejection Reasons** (examples):
- "National ID photo does not match profile photo"
- "Invalid or unclear national ID scan"
- "Email address does not match university domain"
- "Duplicate registration detected"
- "Incorrect academic information"

**Post-Rejection**:
- User status changes to `REJECTED`
- User can still login to view rejection reason
- User cannot access platform features
- Rejection reason displayed on user's status page

### Bulk Operations

Currently, bulk approval/rejection is not implemented. Each user must be reviewed individually to ensure quality control.

**Future Enhancement**: Planned for future releases.

---

## Content Moderation

CampusCard includes automated content moderation to prevent inappropriate content.

### Banned Words System

The system maintains a list of banned words that are checked against:
- Profile bio
- Interests
- Social media links

### How It Works

1. User submits profile update
2. System checks all text fields against banned words
3. If match found:
   - Update rejected
   - Violation logged
   - User sees error message
4. If clean:
   - Update proceeds

### Managing Banned Words

**Viewing Banned Words**:
Currently requires database access:
```sql
SELECT * FROM banned_words ORDER BY word;
```

**Adding Banned Words**:
```sql
INSERT INTO banned_words (word) VALUES ('inappropriate_word');
```

**Removing Banned Words**:
```sql
DELETE FROM banned_words WHERE word = 'word_to_remove';
```

**Future Enhancement**: UI for managing banned words is planned.

### Reviewing Flagged Content

When content moderation triggers:

1. Violation logged to `flagged_content` table
2. Admin can review logs:
   ```sql
   SELECT * FROM flagged_content ORDER BY flagged_at DESC LIMIT 50;
   ```

**Log Information**:
- User ID
- Content type (bio, interests, etc.)
- Flagged content
- Matched word
- Timestamp

### Content Moderation Best Practices

1. **Regular Reviews**: Check flagged content weekly
2. **False Positives**: Remove words that cause legitimate content to be blocked
3. **User Education**: Provide clear guidelines on acceptable content
4. **Privacy**: Don't expose specific banned words to users
5. **Balance**: Moderate effectively without over-censoring

---

## Role Management

Admins can promote users to admin or demote admins to students.

### Promoting a User to Admin

1. Navigate to user details page
2. Click "Promote to Admin" button
3. Confirm the action
4. User role changes to `ADMIN`
5. User gains all admin privileges on next login

**When to Promote**:
- Trusted staff members
- Faculty representatives
- IT support personnel

**Security Considerations**:
- Limit number of admins
- Only promote verified individuals
- Document all admin promotions
- Review admin list regularly

### Demoting an Admin to Student

1. Navigate to admin user details
2. Click "Demote to Student" button
3. Confirm the action
4. User role changes to `STUDENT`
5. User loses admin privileges immediately

**When to Demote**:
- Staff member leaves position
- Temporary admin period ended
- Security concerns

**⚠️ Warning**: You cannot demote yourself! Another admin must perform this action.

---

## Best Practices

### Daily Tasks

- [ ] Check pending users count
- [ ] Review any new registrations
- [ ] Respond to user inquiries

### Weekly Tasks

- [ ] Review all pending users
- [ ] Approve eligible users
- [ ] Check flagged content logs
- [ ] Review rejection patterns

### Monthly Tasks

- [ ] Review dashboard statistics
- [ ] Analyze faculty distribution
- [ ] Update banned words list if needed
- [ ] Review admin accounts
- [ ] Backup database

### Security Best Practices

1. **Strong Password**:
   - Minimum 16 characters
   - Mix of letters, numbers, symbols
   - Unique to CampusCard
   - Use password manager

2. **Session Management**:
   - Logout when finished
   - Don't share credentials
   - Use private/incognito for public computers

3. **Access Control**:
   - Minimum number of admins
   - Regular access reviews
   - Prompt demotion of ex-staff

4. **Audit Trail**:
   - Document major actions
   - Keep records of approvals/rejections
   - Log admin activities

### Communication Guidelines

**Approving Users**:
- No message needed (automatic email sent)
- Welcome users warmly
- Provide help resources

**Rejecting Users**:
- Be clear and specific
- Be professional and respectful
- Provide actionable feedback
- Offer re-registration if applicable

**Example Rejection Messages**:

✅ Good:
> "Your national ID scan is unclear. Please re-register with a clear, well-lit photo of your national ID card."

❌ Bad:
> "Rejected."

### Efficiency Tips

1. **Keyboard Shortcuts**: Learn browser shortcuts for faster navigation
2. **Multiple Tabs**: Open multiple users in tabs for comparison
3. **Batch Review**: Set aside dedicated time for user reviews
4. **Priority Queue**: Review oldest pending users first
5. **Notes**: Keep a notepad for complex cases

---

## Troubleshooting

### Common Issues

#### Cannot Access Admin Panel

**Symptoms**: Redirected to login or error page

**Solutions**:
1. Verify you're logged in as admin
2. Check email and password
3. Clear browser cache and cookies
4. Try incognito/private mode
5. Check with IT if issue persists

#### Email Verification Not Working

**Symptoms**: Cannot verify user emails manually

**Solutions**:
1. Check if SMTP is configured
2. Verify email service is running
3. Use manual verification button
4. Check application logs for errors

#### Cannot Approve User

**Symptoms**: "Approve User" button doesn't work or shows error

**Possible Causes**:
1. **Email not verified**: Verify email first
2. **User already approved**: Refresh page
3. **Network error**: Check connection
4. **Server error**: Contact IT

**Solutions**:
1. Verify user email first
2. Refresh the page
3. Check browser console for errors
4. Try different browser

#### Statistics Not Updating

**Symptoms**: Dashboard shows stale data

**Solutions**:
1. Refresh the page (F5 or Ctrl+R)
2. Hard refresh (Ctrl+Shift+R)
3. Clear cache
4. Data may be cached - wait a few minutes

### Error Messages

| Error | Meaning | Solution |
|-------|---------|----------|
| "Email not verified" | User email must be verified before approval | Click "Verify Email" button first |
| "User not found" | User ID is invalid or user was deleted | Check URL, refresh user list |
| "Unauthorized" | Your session expired or you're not an admin | Login again as admin |
| "Server error" | Backend error occurred | Contact IT support |

### Getting Help

1. **Check Logs**:
   - Backend logs: Check application console
   - Frontend errors: Check browser console (F12)

2. **Contact IT Support**:
   - Email: Mohamed170408@eng.psu.edu.eg
   - Include error message and steps to reproduce

3. **Documentation**:
   - Review this guide
   - Check [API.md](API.md) for technical details
   - See [SECURITY.md](SECURITY.md) for security info

---

## Appendix

### Admin Permissions

Admins can:
- ✅ View all users regardless of status
- ✅ View all profiles regardless of visibility
- ✅ Approve pending users
- ✅ Reject pending users with reason
- ✅ Verify user emails manually
- ✅ Promote users to admin
- ✅ Demote admins to students
- ✅ View dashboard statistics
- ✅ Access admin-only endpoints

Admins cannot:
- ❌ Delete users (no deletion feature)
- ❌ Edit other users' profiles directly
- ❌ Change other users' passwords
- ❌ Demote themselves
- ❌ View passwords (hashed in database)

### Quick Reference

**Useful URLs**:
- Dashboard: `/admin`
- Pending Users: `/admin/users`
- Review User: `/admin/review/:userId`

**Keyboard Shortcuts**:
- Refresh page: `F5` or `Ctrl+R`
- Open browser console: `F12`
- Back to previous page: `Alt+←`

**Status Indicators**:
- 🟢 Green checkmark: Email verified
- 🔴 Red X: Email not verified
- 🟡 Yellow: Pending approval
- ✅ Approved
- ❌ Rejected

---

**Last Updated**: December 24, 2025  
**Version**: 1.0  
**For Support**: Mohamed170408@eng.psu.edu.eg
