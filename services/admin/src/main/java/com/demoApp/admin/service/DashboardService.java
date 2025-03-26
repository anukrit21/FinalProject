package com.demoApp.admin.service;

import com.demoApp.admin.dto.DashboardSummaryDTO;
import com.demoApp.admin.dto.UserActivityDTO;
import com.demoApp.admin.entity.ServiceStatus;
import com.demoApp.admin.entity.ScheduledTask;
import com.demoApp.admin.entity.SystemMetric;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final ServiceStatusService serviceStatusService;
    private final UserActivityService userActivityService;
    private final SystemMetricService systemMetricService;
    private final ScheduledTaskService scheduledTaskService;
    private final RestTemplate restTemplate;
    
    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
        // Get service statuses
        List<ServiceStatus> serviceStatuses = serviceStatusService.getAllServiceStatuses();
        Map<String, String> serviceStatusMap = serviceStatuses.stream()
                .collect(Collectors.toMap(
                        ServiceStatus::getServiceName,
                        status -> status.getStatus().name()
                ));
        summary.setServiceStatuses(serviceStatusMap);
        summary.setServiceStatusList(serviceStatusService.convertToDTOs(serviceStatuses));
        
        // Get recent user activities (Fixed Type Mismatch)
        Page<UserActivityDTO> recentActivitiesPage = userActivityService.getAllUserActivities(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
        List<UserActivityDTO> recentActivities = recentActivitiesPage.getContent(); // Fix
        summary.setRecentUserActivities(recentActivities);

        // Get upcoming scheduled tasks
        List<ScheduledTask> upcomingTasks = scheduledTaskService.getScheduledTasksByStatus(ScheduledTask.Status.SCHEDULED)
                .stream()
                .sorted((t1, t2) -> {
                    if (t1.getNextExecutionTime() == null) return 1;
                    if (t2.getNextExecutionTime() == null) return -1;
                    return t1.getNextExecutionTime().compareTo(t2.getNextExecutionTime());
                })
                .limit(5)
                .collect(Collectors.toList());
        summary.setUpcomingScheduledTasks(scheduledTaskService.convertToDTOs(upcomingTasks));
        
        // Collect system metrics
        Map<String, Double> metrics = new HashMap<>();
        try {
            // Try to get user metrics from user service
            metrics.put("TOTAL_USERS", fetchMetricFromService("user-service", "total-users"));
            metrics.put("ACTIVE_USERS", fetchMetricFromService("user-service", "active-users"));
            
            // Try to get subscription metrics from subscription service
            metrics.put("TOTAL_SUBSCRIPTIONS", fetchMetricFromService("subscription-service", "total-subscriptions"));
            metrics.put("ACTIVE_SUBSCRIPTIONS", fetchMetricFromService("subscription-service", "active-subscriptions"));
            
            // Try to get order metrics from mess service
            metrics.put("ORDERS_TODAY", fetchMetricFromService("mess-service", "orders-today"));
            metrics.put("TOTAL_ORDERS", fetchMetricFromService("mess-service", "total-orders"));
        } catch (Exception e) {
            // Fallback to local metrics if available
            for (SystemMetric.MetricType metricType : SystemMetric.MetricType.values()) {
                try {
                    for (String service : serviceStatusMap.keySet()) {
                        SystemMetric metric = systemMetricService.getLatestMetricByServiceAndType(service, metricType);
                        if (metric.getNumericValue() != null) {
                            metrics.put(metricType.name() + "_" + service, metric.getNumericValue());
                        }
                    }
                } catch (Exception ex) {
                    // Ignore if not found
                }
            }
        }
        
        summary.setSystemMetrics(metrics);
        
        // Set counts from metrics
        summary.setTotalUsers(metrics.getOrDefault("TOTAL_USERS", 0.0).longValue());
        summary.setActiveUsers(metrics.getOrDefault("ACTIVE_USERS", 0.0).longValue());
        summary.setTotalSubscriptions(metrics.getOrDefault("TOTAL_SUBSCRIPTIONS", 0.0).longValue());
        summary.setActiveSubscriptions(metrics.getOrDefault("ACTIVE_SUBSCRIPTIONS", 0.0).longValue());
        summary.setOrdersToday(metrics.getOrDefault("ORDERS_TODAY", 0.0).longValue());
        summary.setTotalOrders(metrics.getOrDefault("TOTAL_ORDERS", 0.0).longValue());
        
        return summary;
    }
    
    private Double fetchMetricFromService(String serviceName, String metricPath) {
        try {
            ServiceStatus service = serviceStatusService.getServiceStatusByName(serviceName);
            String url = service.getServiceUrl() + "/api/v1/metrics/" + metricPath;
            return restTemplate.getForObject(url, Double.class);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
