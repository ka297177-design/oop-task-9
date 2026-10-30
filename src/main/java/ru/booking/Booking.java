package ru.booking;

import java.util.Objects;
import java.util.UUID;

public final class Booking {

    private final String id;
    private final Room room;
    private final TimeInterval interval;
    private final Employee employee;

    public Booking(Room room, TimeInterval interval, Employee employee) {
        this(UUID.randomUUID().toString(), room, interval, employee);
    }


    public Booking(String id, Room room, TimeInterval interval, Employee employee) {
        this.id = Objects.requireNonNull(id, "id must not be null").strip();
        if (this.id.isEmpty()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        this.room = Objects.requireNonNull(room, "room must not be null");
        this.interval = Objects.requireNonNull(interval, "interval must not be null");
        this.employee = Objects.requireNonNull(employee, "employee must not be null");
    }

    public String getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public Employee getEmployee() {
        return employee;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking other)) {
            return false;
        }
        return id.equals(other.id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return "Booking{" + id.substring(0, Math.min(8, id.length())) +
                ", room=" + room.getId() +
                ", " + interval +
                ", by=" + employee.getFullName() + '}';
    }
}
