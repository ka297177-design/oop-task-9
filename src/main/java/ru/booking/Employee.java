package ru.booking;

import java.util.Objects;

public final class Employee {

    private final String id;
    private final String fullName;

    public Employee(String id, String fullName) {
        this.id = Objects.requireNonNull(id, "id must not be null").strip();
        if (this.id.isEmpty()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null").strip();
        if (this.fullName.isEmpty()) {
            throw new IllegalArgumentException("fullName must not be blank");
        }
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee other)) {
            return false;
        }
        return id.equals(other.id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return fullName + " (" + id + ")";
    }
}
