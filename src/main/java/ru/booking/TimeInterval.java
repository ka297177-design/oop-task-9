package ru.booking;

import java.time.LocalDateTime;
import java.util.Objects;


public record TimeInterval(LocalDateTime start, LocalDateTime end) {

    /**
     * Компактный конструктор с проверкой инварианта.
     */
    public TimeInterval {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Конец интервала должен быть позже начала: " + start + " .. " + end);
        }
    }


    public boolean intersects(TimeInterval other) {
        Objects.requireNonNull(other, "other must not be null");
        return start.isBefore(other.end) && other.start.isBefore(end);
    }


    public long durationMinutes() {
        return java.time.Duration.between(start, end).toMinutes();
    }

    @Override
    public String toString() {
        return start + " — " + end;
    }
}
