package ru.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private BookingService service;
    private Room lecture;
    private Room lab;
    private Room conference;
    private Employee emp;
    private LocalDate day;

    @BeforeEach
    void setUp() {
        service = new BookingService();
        lecture = new LectureRoom("A-101", 100, Set.of("проектор"));
        lab = new LabRoom("B-301", 24, Set.of("ПК"));
        conference = new ConferenceRoom("C-401", 50, Set.of("экран"));
        service.addRoom(lecture);
        service.addRoom(lab);
        service.addRoom(conference);
        emp = new Employee("E001", "Иванов И.И.");
        day = LocalDate.of(2026, 10, 13);
    }

    private TimeInterval ti(int h1, int m1, int h2, int m2) {
        return new TimeInterval(
                LocalDateTime.of(day, LocalTime.of(h1, m1)),
                LocalDateTime.of(day, LocalTime.of(h2, m2)));
    }



    @Test
    @DisplayName("TimeInterval: конец позже начала — ок")
    void intervalValid() {
        TimeInterval i = ti(9, 0, 10, 0);
        assertEquals(60, i.durationMinutes());
    }

    @Test
    @DisplayName("TimeInterval: конец не позже начала — исключение")
    void intervalInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ti(10, 0, 9, 0));
        assertThrows(IllegalArgumentException.class, () -> ti(10, 0, 10, 0));
    }

    @Test
    @DisplayName("TimeInterval: пересечение")
    void intervalIntersects() {
        TimeInterval a = ti(9, 0, 11, 0);
        TimeInterval b = ti(10, 0, 12, 0);
        TimeInterval c = ti(11, 0, 12, 0);
        TimeInterval d = ti(12, 0, 13, 0);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
        assertFalse(a.intersects(c)); // касание границы — по нашей реализации не пересекается (end exclusive)
        assertFalse(a.intersects(d));
    }


    @Test
    @DisplayName("Employee: equals/hashCode по id")
    void employeeEquals() {
        Employee a = new Employee("E001", "Иванов");
        Employee b = new Employee("E001", "Другое Имя");
        Employee c = new Employee("E002", "Иванов");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "E001");
    }


    @Test
    @DisplayName("Room: equals/hashCode по id")
    void roomEquals() {
        Room a = new LectureRoom("A-101", 50, Set.of());
        Room b = new LectureRoom("A-101", 80, Set.of("x"));
        Room c = new LabRoom("B-301", 50, Set.of());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }


    @Test
    @DisplayName("Успешное бронирование")
    void bookSuccess() throws BookingConflictException {
        Booking b = service.book(lecture, ti(9, 0, 10, 0), emp);
        assertNotNull(b.getId());
        assertEquals(lecture, b.getRoom());
        assertEquals(1, service.bookingCount());
    }

    @Test
    @DisplayName("Конфликт бронирования — BookingConflictException")
    void bookConflict() throws BookingConflictException {
        service.book(lecture, ti(9, 0, 11, 0), emp);
        BookingConflictException ex = assertThrows(BookingConflictException.class,
                () -> service.book(lecture, ti(10, 0, 12, 0), emp));
        assertNotNull(ex.getConflictingBooking());
        assertEquals(1, service.bookingCount());
    }

    @Test
    @DisplayName("Нет конфликта на соседних интервалах")
    void bookAdjacentOk() throws BookingConflictException {
        service.book(lecture, ti(9, 0, 10, 0), emp);
        Booking b2 = service.book(lecture, ti(10, 0, 11, 0), emp);
        assertNotNull(b2);
        assertEquals(2, service.bookingCount());
    }

    @Test
    @DisplayName("Разные помещения — конфликта нет")
    void bookDifferentRoomsOk() throws BookingConflictException {
        service.book(lecture, ti(9, 0, 11, 0), emp);
        Booking b2 = service.book(lab, ti(9, 0, 11, 0), emp);
        assertNotNull(b2);
        assertEquals(2, service.bookingCount());
    }


    @Test
    @DisplayName("Отмена существующей брони")
    void cancelExisting() throws BookingConflictException {
        Booking b = service.book(lecture, ti(9, 0, 10, 0), emp);
        assertTrue(service.cancel(b.getId()));
        assertEquals(0, service.bookingCount());
        assertFalse(service.cancel(b.getId()));
    }

    @Test
    @DisplayName("Отмена несуществующей брони")
    void cancelMissing() {
        assertFalse(service.cancel("non-existent-id"));
    }


    @Test
    @DisplayName("Свободные окна: пустой день — одно окно 08:00-22:00")
    void freeSlotsEmptyDay() {
        List<TimeInterval> slots = service.freeSlots(lecture, day);
        assertEquals(1, slots.size());
        assertEquals(LocalTime.of(8, 0), slots.get(0).start().toLocalTime());
        assertEquals(LocalTime.of(22, 0), slots.get(0).end().toLocalTime());
    }

    @Test
    @DisplayName("Свободные окна: с одной бронью — два окна")
    void freeSlotsWithOneBooking() throws BookingConflictException {
        service.book(lecture, ti(10, 0, 12, 0), emp);
        List<TimeInterval> slots = service.freeSlots(lecture, day);
        assertEquals(2, slots.size());
        assertEquals(LocalTime.of(8, 0), slots.get(0).start().toLocalTime());
        assertEquals(LocalTime.of(10, 0), slots.get(0).end().toLocalTime());
        assertEquals(LocalTime.of(12, 0), slots.get(1).start().toLocalTime());
        assertEquals(LocalTime.of(22, 0), slots.get(1).end().toLocalTime());
    }

    @Test
    @DisplayName("Свободные окна: граничный случай — бронь с 08:00")
    void freeSlotsFromStart() throws BookingConflictException {
        service.book(lecture, ti(8, 0, 9, 0), emp);
        List<TimeInterval> slots = service.freeSlots(lecture, day);
        assertEquals(1, slots.size());
        assertEquals(LocalTime.of(9, 0), slots.get(0).start().toLocalTime());
    }


    @Test
    @DisplayName("Подбор помещения: находится свободное")
    void findSuitable() throws BookingConflictException {
        service.book(lecture, ti(9, 0, 10, 0), emp);
        Optional<Room> found = service.findSuitableRoom(40, ti(11, 0, 12, 0));
        assertTrue(found.isPresent());
        assertTrue(found.get().getCapacity() >= 40);
    }

    @Test
    @DisplayName("Подбор помещения: ничего не подходит по вместимости")
    void findSuitableNoneByCapacity() {
        Optional<Room> found = service.findSuitableRoom(500, ti(11, 0, 12, 0));
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Подбор помещения: всё занято")
    void findSuitableNoneByBusy() throws BookingConflictException {
        TimeInterval interval = ti(9, 0, 10, 0);
        service.book(lecture, interval, emp);
        service.book(lab, interval, emp);
        service.book(conference, interval, emp);
        Optional<Room> found = service.findSuitableRoom(10, interval);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Полиморфизм suitabilityScore")
    void suitabilityPolymorphism() {
        assertTrue(lab.suitabilityScore(20) > lecture.suitabilityScore(20)
                || conference.suitabilityScore(20) != lecture.suitabilityScore(20));
        assertEquals(-1000, lecture.suitabilityScore(200));
    }

    @Test
    @DisplayName("RoomType: поля и recommendation")
    void roomTypeEnum() {
        assertEquals("Лекционная аудитория", RoomType.LECTURE.getDescription());
        assertTrue(RoomType.LAB.requiresSpecialEquipment());
        assertFalse(RoomType.SEMINAR.requiresSpecialEquipment());
        assertFalse(RoomType.LECTURE.recommendation().isBlank());
    }

    @Test
    @DisplayName("Инкапсуляция: getEquipment возвращает неизменяемый набор")
    void equipmentImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> lecture.getEquipment().add("хак"));
    }

    @Test
    @DisplayName("Booking equals по id")
    void bookingEquals() throws BookingConflictException {
        Booking b1 = service.book(lecture, ti(9, 0, 10, 0), emp);
        Booking b2 = new Booking(b1.getId(), lecture, ti(11, 0, 12, 0), emp);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }
}
