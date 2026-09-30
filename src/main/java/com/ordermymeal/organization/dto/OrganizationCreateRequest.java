package com.ordermymeal.organization.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OrganizationCreateRequest {

    @NotBlank(message = "Organization name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 150)
    private String displayName;

    @Size(max = 100)
    private String timeZone;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Enter a valid admin email")
    @Size(max = 255)
    private String adminEmail;

    @Size(max = 150)
    private String adminName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}
