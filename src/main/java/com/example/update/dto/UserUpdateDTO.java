package com.example.update.dto;

import java.io.Serializable;
import java.util.Map;

public class UserUpdateDTO implements Serializable {
    private String userId;
    private String department;
    private String role;
    private String status;
    private Double salary;
    private Map<String, Object> additionalAttributes;

    public UserUpdateDTO() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public Map<String, Object> getAdditionalAttributes() { return additionalAttributes; }
    public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }
}
