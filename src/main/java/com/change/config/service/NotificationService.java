package com.change.config.service;

import com.change.config.model.ConfigChange;

/**
 * Service for notifying external systems about configuration changes.
 */
public interface NotificationService {
    
    /**
     * Notifies external monitoring service about a critical configuration change.
     *
     * @param configChange The configuration change that is critical
     */
    void notifyCriticalChange(ConfigChange configChange);
}