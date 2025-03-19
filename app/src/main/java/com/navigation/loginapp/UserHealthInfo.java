package com.navigation.loginapp;

public class UserHealthInfo {
    private String userId;
    private String relativesPhone;
    private String healthIssues;
    private String prescriptions;

    private String allergy;
    private String notes;

    // No-argument constructor
    public UserHealthInfo() {
    }

    // Parameterized constructor
    public UserHealthInfo(String userId, String relativesPhone, String healthIssues, String prescriptions, String allergy, String notes) {
        this.userId = userId;
        this.relativesPhone = relativesPhone;
        this.healthIssues = healthIssues;
        this.prescriptions = prescriptions;
        this.allergy = allergy;
        this.notes = notes;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRelativesPhone() {
        return relativesPhone;
    }

    public void setRelativesPhone(String relativesPhone) {
        this.relativesPhone = relativesPhone;
    }

    public String getHealthIssues() {
        return healthIssues;
    }

    public void setHealthIssues(String healthIssues) {
        this.healthIssues = healthIssues;
    }

    public String getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }
    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
