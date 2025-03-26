package com.demoApp.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalSubscriptions;
    private Long activeSubscriptions;
    private Long ordersToday;
    private Long totalOrders;
    
    private Map<String, String> serviceStatuses; // serviceName -> status
    private List<UserActivityDTO> recentUserActivities;
    private List<ServiceStatusDTO> serviceStatusList;
    
    private Map<String, Double> systemMetrics; // metricName -> value
    private List<ScheduledTaskDTO> upcomingScheduledTasks;
} 