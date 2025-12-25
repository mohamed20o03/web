/**
 * Content moderation domain.
 * Protects platform from inappropriate content through banned word detection.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Service:</strong> ContentModerationService for real-time text scanning</li>
 *   <li><strong>Models:</strong> BannedWord, FlaggedContent entities</li>
 *   <li><strong>Repositories:</strong> BannedWordRepository, FlaggedContentRepository</li>
 *   <li><strong>DTOs:</strong> Banned word and flagged content responses</li>
 * </ul>
 *
 * <p>Moderation workflow:
 * <ol>
 *   <li>User submits text content (bio, interests, etc.)</li>
 *   <li>ContentModerationService checks against banned words list</li>
 *   <li>If violations found, content is flagged for admin review</li>
 *   <li>Admin reviews flagged content in dashboard</li>
 *   <li>Admin takes action: approve (false positive), edit, or delete</li>
 * </ol>
 *
 * <p>Banned words categories:
 * <ul>
 *   <li>Offensive language and profanity</li>
 *   <li>Hate speech and discriminatory terms</li>
 *   <li>Spam and advertising keywords</li>
 *   <li>Inappropriate content references</li>
 * </ul>
 *
 * <p>Admins can add/remove banned words through admin endpoints.
 * All checks are case-insensitive and use substring matching.
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.moderation;
