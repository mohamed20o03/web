/**
 * User profile management domain.
 * Handles student profiles, visibility settings, and public directory.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Controllers:</strong> ProfileController (authenticated), PublicController (public directory)</li>
 *   <li><strong>Services:</strong> ProfileService, PublicService</li>
 *   <li><strong>Model:</strong> Profile entity with visibility settings</li>
 *   <li><strong>Repository:</strong> ProfileRepository with visibility-aware queries</li>
 *   <li><strong>DTOs:</strong> Profile response, update request, visibility settings</li>
 * </ul>
 *
 * <p>Profile features:
 * <ul>
 *   <li>Profile photo upload and management</li>
 *   <li>Bio and interests (content moderation checked)</li>
 *   <li>Social links (LinkedIn, GitHub)</li>
 *   <li>Contact information (phone)</li>
 *   <li>Visibility control (PUBLIC, STUDENTS_ONLY, PRIVATE)</li>
 * </ul>
 *
 * <p>Visibility rules:
 * <ul>
 *   <li><strong>PUBLIC:</strong> Visible to all authenticated users in directory</li>
 *   <li><strong>STUDENTS_ONLY:</strong> Only visible to other approved students</li>
 *   <li><strong>PRIVATE:</strong> Hidden from directory, only owner can view</li>
 * </ul>
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.profile;
