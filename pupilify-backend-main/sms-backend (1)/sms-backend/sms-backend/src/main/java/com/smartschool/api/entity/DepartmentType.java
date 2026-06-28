package com.smartschool.api.entity;

public enum DepartmentType {
    ADMISSION("Admission Department"),
    EXAM("Exam Department"),
    BUS("Bus Department");

    private final String displayName;

    DepartmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

