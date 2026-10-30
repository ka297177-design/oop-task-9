package ru.booking;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public abstract sealed class Room permits LectureRoom, LabRoom, ConferenceRoom {

    private final String id;
    private final int capacity;
    private final RoomType type;
    private final Set<String> equipment;

    protected Room(String id, int capacity, RoomType type, Set<String> equipment) {
        this.id = Objects.requireNonNull(id, "id must not be null").strip();
        if (this.id.isEmpty()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.equipment = equipment == null
                ? Set.of()
                : Collections.unmodifiableSet(new LinkedHashSet<>(equipment));
    }

    public String getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public RoomType getType() {
        return type;
    }

    public Set<String> getEquipment() {
        return equipment;
    }

    public abstract int suitabilityScore(int requiredCapacity);

    public String describe() {
        return type.getDescription() + " " + id + " (мест: " + capacity + ")";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room other)) {
            return false;
        }
        return id.equals(other.id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return describe();
    }
}
