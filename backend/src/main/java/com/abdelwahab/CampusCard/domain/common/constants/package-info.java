/**
 * Central repository for application-wide constants.
 *
 * <p>This package contains:
 * <ul>
 *   <li>{@link com.abdelwahab.CampusCard.domain.common.constants.ValidationConstants} - 
 *       Validation rules, field limits, and patterns</li>
 *   <li>{@link com.abdelwahab.CampusCard.domain.common.constants.ErrorMessages} - 
 *       User-facing error messages for consistent messaging</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>{@code
 * import static com.abdelwahab.CampusCard.domain.common.constants.ValidationConstants.*;
 * import static com.abdelwahab.CampusCard.domain.common.constants.ErrorMessages.*;
 * 
 * if (password.length() < MIN_PASSWORD_LENGTH) {
 *     throw new ValidationException(PASSWORD_TOO_SHORT);
 * }
 * }</pre>
 *
 * <p>Benefits:
 * <ul>
 *   <li>Single source of truth for all constants</li>
 *   <li>Easy to update values across entire application</li>
 *   <li>Consistent error messaging</li>
 *   <li>Compile-time safety (vs magic strings)</li>
 * </ul>
 *
 * @author CampusCard Team
 * @since 1.0
 */
package com.abdelwahab.CampusCard.domain.common.constants;
