/**
 * Common utilities and shared infrastructure domain.
 * Contains cross-cutting concerns used across all other domains.
 *
 * <p>Key components:
 * <ul>
 *   <li><strong>Security:</strong> JWT service, authentication filter, rate limiting</li>
 *   <li><strong>Configuration:</strong> Security config, MinIO config, rate limit config</li>
 *   <li><strong>Converters:</strong> JPA attribute converters for enums (Role, Status, Visibility)</li>
 *   <li><strong>Validation:</strong> Custom validators (PSU email validation)</li>
 *   <li><strong>Exception Handling:</strong> Global exception handler</li>
 * </ul>
 *
 * <p>Security components:
 * <ul>
 *   <li><strong>JwtService:</strong> Token generation, validation, claim extraction</li>
 *   <li><strong>JwtAuthenticationFilter:</strong> Request authentication via JWT token</li>
 *   <li><strong>RateLimitInterceptor:</strong> Prevents brute force attacks on auth endpoints</li>
 *   <li><strong>SecurityConfig:</strong> Spring Security configuration (CORS, authorization rules)</li>
 * </ul>
 *
 * <p>Configuration components:
 * <ul>
 *   <li><strong>MinioConfig:</strong> MinIO client setup for object storage</li>
 *   <li><strong>RateLimitConfig:</strong> Rate limiting rules (5 login, 3 signup attempts)</li>
 *   <li><strong>AdminUserInitializer:</strong> Bootstrap admin account on first run</li>
 * </ul>
 *
 * <p>Converters for enum persistence:
 * <ul>
 *   <li><strong>RoleConverter:</strong> Converts User.Role enum to DB string</li>
 *   <li><strong>StatusConverter:</strong> Converts User.Status enum to DB string</li>
 *   <li><strong>VisibilityConverter:</strong> Converts Profile.Visibility enum to DB string</li>
 * </ul>
 *
 * @since 1.0
 * @author CampusCard Team
 */
package com.abdelwahab.CampusCard.domain.common;
