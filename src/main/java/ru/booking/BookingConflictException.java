package ru.booking;

public class BookingConflictException extends Exception {

    private final Booking conflictingBooking;


    public BookingConflictException(String message, Booking conflictingBooking) {
        super(message);
        this.conflictingBooking = conflictingBooking;
    }


    public Booking getConflictingBooking() {
        return conflictingBooking;
    }
}
