package com.impilohealth.user;

/**
 * USER is a normal app user. The other roles map to the three-tier data access
 * framework (NGO Admin / Regional Coordinator / System Admin).
 */
public enum Role {
    USER,
    NGO_ADMIN,
    REGIONAL_COORDINATOR,
    SYSTEM_ADMIN
}
