package ru.booking;

import java.util.Set;


public final class LabRoom extends Room {

    public LabRoom(String id, int capacity, Set<String> equipment) {
        super(id, capacity, RoomType.LAB, equipment);
    }

    @Override
    public int suitabilityScore(int requiredCapacity) {
        if (getCapacity() < requiredCapacity) {
            return -1000;
        }
        int base = 120 - Math.min(getCapacity() - requiredCapacity, 40);
        return getEquipment().isEmpty() ? base - 20 : base + 10;
    }
}
