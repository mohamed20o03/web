package com.abdelwahab.CampusCard.domain.moderation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.abdelwahab.CampusCard.domain.moderation.model.BannedWord;
import com.abdelwahab.CampusCard.domain.moderation.model.FlaggedContent;
import com.abdelwahab.CampusCard.domain.user.model.User;
import com.abdelwahab.CampusCard.domain.moderation.repository.BannedWordRepository;
import com.abdelwahab.CampusCard.domain.moderation.repository.FlaggedContentRepository;
import com.abdelwahab.CampusCard.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for content moderation and banned word detection.
 * Protects platform from inappropriate content in user-generated text fields.
 *
 * <p>Moderation features:
 * <ul>
 *   <li>Real-time banned word detection in profile bios and interests</li>
 *   <li>Automatic content flagging when banned words detected</li>
 *   <li>Admin interface for managing banned words list</li>
 *   <li>Flagged content review and resolution workflow</li>
 * </ul>
 *
 * <p>Moderation workflow:
 * <ol>
 *   <li>User submits content (bio, interests, etc.)</li>
 *   <li>Service checks content against banned words list</li>
 *   <li>If banned words found, content is flagged for admin review</li>
 *   <li>Admin reviews flagged content and takes action (approve/edit/delete)</li>
 * </ol>
 *
 * <p>Banned words are checked case-insensitively and include common:
 * <ul>
 *   <li>Offensive language and slurs</li>
 *   <li>Hate speech and discriminatory terms</li>
 *   <li>Spam and advertising keywords</li>
 *   <li>Inappropriate content references</li>
 * </ul>
 *
 * @author CampusCard Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentModerationService {
    
    private final BannedWordRepository bannedWordRepository;
    private final FlaggedContentRepository flaggedContentRepository;
    private final UserRepository userRepository;

    /**
     * Detects banned words in user-submitted text content.
     * Returns list of specific banned words found for admin review.
     *
     * <p>Search is case-insensitive and checks for substring matches.
     * Empty or null text returns empty list (no violations).
     *
     * @param text the user-submitted content to check (bio, interests, etc.)
     * @return list of banned words found in the text, empty list if none
     */
    public List<String> checkForBannedWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        String lowerText = text.toLowerCase();
        List<BannedWord> allBannedWords = bannedWordRepository.findAll();
        
        return allBannedWords.stream()
                .map(BannedWord::getWord)
                .filter(word -> lowerText.contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Checks if text contains any banned words (boolean result).
     * Convenience method for quick validation without needing specific words.
     *
     * @param text the user-submitted content to check
     * @return true if one or more banned words found, false if clean
     */
    public boolean containsBannedWords(String text) {
        return !checkForBannedWords(text).isEmpty();
    }

    /**
     * Validate multiple text fields for banned words
     * @param fields Map of field names to text values
     * @return Map of field names to lists of banned words found
     */
    public java.util.Map<String, List<String>> validateFields(java.util.Map<String, String> fields) {
        java.util.Map<String, List<String>> violations = new java.util.HashMap<>();
        
        for (java.util.Map.Entry<String, String> entry : fields.entrySet()) {
            List<String> bannedWords = checkForBannedWords(entry.getValue());
            if (!bannedWords.isEmpty()) {
                violations.put(entry.getKey(), bannedWords);
            }
        }
        
        return violations;
    }

    /**
     * Log a content moderation violation for admin review
     * @param userId The user ID who attempted to post the content
     * @param fieldName The field name where the violation occurred
     * @param content The content that was flagged
     * @param bannedWords The banned words found
     */
    public void logViolation(Integer userId, String fieldName, String content, List<String> bannedWords) {
        log.warn("Content Moderation Violation - User ID: {}, Field: {}, Banned Words: {}, Content Preview: {}",
                userId, fieldName, bannedWords, 
                content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content);
        
        // Save to flagged_content table for admin review
        try {
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user != null && content != null) {
                String flaggedMessage = String.format(
                    "[Field: %s] Banned words detected: %s | Content: %s",
                    fieldName,
                    String.join(", ", bannedWords),
                    content.length() > 500 ? content.substring(0, 500) + "..." : content
                );
                
                FlaggedContent flaggedContent = FlaggedContent.builder()
                        .user(user)
                        .content(flaggedMessage)
                        .build();
                
                flaggedContentRepository.save(flaggedContent);
            }
        } catch (Exception e) {
            log.error("Failed to save flagged content: {}", e.getMessage());
        }
    }
}

