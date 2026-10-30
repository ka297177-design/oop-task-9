package ru.booking;

import java.util.Set;

public final class ConferenceRoom extends Room {

    public ConferenceRoom(String id, int capacity, Set<String> equipment) {
        super(id, capacity, RoomType.CONFERENCE, equipment);
    }

    @Override
    public int suitabilityScore(int requiredCapacity) {
        if (getCapacity() < requiredCapacity) {
            return -1000;
        }
        return 80 + Math.min(getCapacity() / 10, 30);
    }
}
