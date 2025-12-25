package com.abdelwahab.CampusCard.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for system statistics shown in admin dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    private Long totalUsers;
    private Long pendingApprovals;
    private Long approvedUsers;
    private Long rejectedUsers;
    private Long studentsCount;
    private Long adminsCount;
    private Long verifiedEmails;
    private Long unverifiedEmails;
}
