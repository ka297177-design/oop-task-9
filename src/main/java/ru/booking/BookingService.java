package ru.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


public class BookingService {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new CopyOnWriteArrayList<>();


    public void addRoom(Room room) {
        Objects.requireNonNull(room, "room must not be null");
        rooms.add(room);
    }


    public List<Room> getRooms() {
        return Collections.unmodifiableList(new ArrayList<>(rooms));
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(new ArrayList<>(bookings));
    }


    public Booking book(Room room, TimeInterval interval, Employee employee)
            throws BookingConflictException {
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(interval, "interval");
        Objects.requireNonNull(employee, "employee");

        for (Booking existing : bookings) {
            if (existing.getRoom().equals(room) && existing.getInterval().intersects(interval)) {
                throw new BookingConflictException(
                        "Конфликт с существующей бронью: " + existing,
                        existing);
            }
        }

        Booking booking = new Booking(room, interval, employee);
        bookings.add(booking);
        return booking;
    }


    public boolean cancel(String bookingId) {
        Objects.requireNonNull(bookingId, "bookingId");
        return bookings.removeIf(b -> b.getId().equals(bookingId));
    }


    public List<TimeInterval> freeSlots(Room room, LocalDate day) {
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(day, "day");

        LocalDateTime dayStart = day.atTime(LocalTime.of(8, 0));
        LocalDateTime dayEnd = day.atTime(LocalTime.of(22, 0));

        List<TimeInterval> occupied = bookings.stream()
                .filter(b -> b.getRoom().equals(room))
                .map(Booking::getInterval)
                .filter(i -> i.start().isBefore(dayEnd) && i.end().isAfter(dayStart))
                .map(i -> new TimeInterval(
                        i.start().isBefore(dayStart) ? dayStart : i.start(),
                        i.end().isAfter(dayEnd) ? dayEnd : i.end()))
                .sorted(Comparator.comparing(TimeInterval::start))
                .toList();

        List<TimeInterval> free = new ArrayList<>();
        LocalDateTime cursor = dayStart;

        for (TimeInterval occ : occupied) {
            if (cursor.isBefore(occ.start())) {
                free.add(new TimeInterval(cursor, occ.start()));
            }
            if (occ.end().isAfter(cursor)) {
                cursor = occ.end();
            }
        }
        if (cursor.isBefore(dayEnd)) {
            free.add(new TimeInterval(cursor, dayEnd));
        }
        return List.copyOf(free);
    }

    public Optional<Room> findSuitableRoom(int requiredCapacity, TimeInterval interval) {
        Objects.requireNonNull(interval, "interval");
        if (requiredCapacity <= 0) {
            throw new IllegalArgumentException("requiredCapacity must be positive");
        }

        return rooms.stream()
                .filter(r -> r.getCapacity() >= requiredCapacity)
                .filter(r -> isFree(r, interval))
                .max(Comparator.comparingInt(r -> r.suitabilityScore(requiredCapacity)));
    }

    public boolean isFree(Room room, TimeInterval interval) {
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(interval, "interval");
        return bookings.stream()
                .noneMatch(b -> b.getRoom().equals(room) && b.getInterval().intersects(interval));
    }


    public int bookingCount() {
        return bookings.size();
    }
}
