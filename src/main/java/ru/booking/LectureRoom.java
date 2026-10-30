package ru.booking;

import java.util.Set;

public final class LectureRoom extends Room {

    public LectureRoom(String id, int capacity, Set<String> equipment) {
        super(id, capacity, RoomType.LECTURE, equipment);
    }

    @Override
    public int suitabilityScore(int requiredCapacity) {
        if (getCapacity() < requiredCapacity) {
            return -1000;
        }
        int excess = getCapacity() - requiredCapacity;
        return 100 - Math.min(excess, 50);
    }
}
