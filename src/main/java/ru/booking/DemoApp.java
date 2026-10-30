package ru.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class DemoApp {

    public static void main(String[] args) {
        BookingService service = new BookingService();

        System.out.println("=== Демонстрация системы бронирования помещений ===\n");

        System.out.println("--- Регистрация помещений ---");
        Room r1 = new LectureRoom("A-101", 120, Set.of("проектор", "микрофон", "доска"));
        Room r2 = new LectureRoom("A-102", 80, Set.of("проектор", "доска"));
        Room r3 = new LectureRoom("A-201", 150, Set.of("проектор", "микрофон", "экран"));
        Room r4 = new LabRoom("B-301", 24, Set.of("осциллограф", "пайка", "ПК"));
        Room r5 = new LabRoom("B-302", 20, Set.of("химия", "вытяжка"));
        Room r6 = new LabRoom("B-303", 30, Set.of("робототехника", "3D-принтер"));
        Room r7 = new ConferenceRoom("C-401", 50, Set.of("видеоконференция", "проектор"));
        Room r8 = new ConferenceRoom("C-402", 100, Set.of("видеоконференция", "микрофон", "экран"));
        Room r9 = new LectureRoom("A-301", 60, Set.of("доска"));
        Room r10 = new ConferenceRoom("C-501", 40, Set.of("проектор"));
        Room r11 = new LabRoom("B-401", 18, Set.of("микроскоп"));
        Room r12 = new LectureRoom("A-401", 200, Set.of("проектор", "микрофон", "камера"));

        List.of(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12).forEach(service::addRoom);
        service.getRooms().forEach(r -> System.out.println("  + " + r + " | " + r.getEquipment()));
        System.out.println("Всего помещений: " + service.getRooms().size());

        System.out.println("\n--- Сотрудники ---");
        Employee e1 = new Employee("E001", "Иванов Иван Иванович");
        Employee e2 = new Employee("E002", "Петрова Анна Сергеевна");
        Employee e3 = new Employee("E003", "Сидоров Пётр Алексеевич");
        Employee e4 = new Employee("E004", "Козлова Мария Викторовна");
        Employee e5 = new Employee("E005", "Новиков Дмитрий Олегович");
        Employee e6 = new Employee("E006", "Морозова Елена Павловна");
        Employee e7 = new Employee("E007", "Волков Андрей Николаевич");
        Employee e8 = new Employee("E008", "Соколова Ольга Игоревна");
        Employee e9 = new Employee("E009", "Лебедев Сергей Владимирович");
        Employee e10 = new Employee("E010", "Кузнецова Татьяна Романовна");
        Employee e11 = new Employee("E011", "Попов Алексей Юрьевич");
        Employee e12 = new Employee("E012", "Васильева Наталья Андреевна");

        List.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12)
                .forEach(e -> System.out.println("  " + e));

        System.out.println("\n--- Бронирование (базовый уровень) ---");
        LocalDate day = LocalDate.of(2026, 10, 13);

        try {
            Booking b1 = service.book(r1, interval(day, 9, 0, 10, 30), e1);
            System.out.println("OK: " + b1);
            Booking b2 = service.book(r1, interval(day, 11, 0, 12, 30), e2);
            System.out.println("OK: " + b2);
            Booking b3 = service.book(r4, interval(day, 9, 0, 11, 0), e3);
            System.out.println("OK: " + b3);
            Booking b4 = service.book(r7, interval(day, 14, 0, 16, 0), e4);
            System.out.println("OK: " + b4);
            Booking b5 = service.book(r2, interval(day, 10, 0, 11, 30), e5);
            System.out.println("OK: " + b5);
            Booking b6 = service.book(r8, interval(day, 9, 30, 12, 0), e6);
            System.out.println("OK: " + b6);
            Booking b7 = service.book(r3, interval(day, 13, 0, 14, 30), e7);
            System.out.println("OK: " + b7);
            Booking b8 = service.book(r5, interval(day, 15, 0, 17, 0), e8);
            System.out.println("OK: " + b8);
            Booking b9 = service.book(r9, interval(day, 8, 30, 10, 0), e9);
            System.out.println("OK: " + b9);
            Booking b10 = service.book(r6, interval(day, 12, 0, 14, 0), e10);
            System.out.println("OK: " + b10);
            Booking b11 = service.book(r10, interval(day, 16, 0, 18, 0), e11);
            System.out.println("OK: " + b11);
            Booking b12 = service.book(r12, interval(day, 10, 0, 12, 0), e12);
            System.out.println("OK: " + b12);

            LocalDate day2 = day.plusDays(1);
            service.book(r1, interval(day2, 9, 0, 11, 0), e1);
            service.book(r2, interval(day2, 14, 0, 16, 0), e2);
            service.book(r4, interval(day2, 10, 0, 12, 0), e3);
            service.book(r7, interval(day2, 11, 0, 13, 0), e4);
            service.book(r3, interval(day2, 15, 0, 17, 0), e5);

            System.out.println("\nВсего броней после успешного создания: " + service.bookingCount());

            System.out.println("\n--- Попытка конфликтного бронирования ---");
            try {
                service.book(r1, interval(day, 10, 0, 11, 0), e3);
                System.out.println("ОШИБКА: конфликт не обнаружен!");
            } catch (BookingConflictException ex) {
                System.out.println("Ожидаемый конфликт: " + ex.getMessage());
                System.out.println("  Конфликтующая бронь: " + ex.getConflictingBooking());
            }

        } catch (BookingConflictException e) {
            System.err.println("Неожиданный конфликт при начальном заполнении: " + e.getMessage());
        }

        System.out.println("\n--- Свободные окна помещения A-101 на " + day + " ---");
        List<TimeInterval> slots = service.freeSlots(r1, day);
        if (slots.isEmpty()) {
            System.out.println("  (нет свободных окон)");
        } else {
            slots.forEach(s -> System.out.println("  " + s + " (" + s.durationMinutes() + " мин)"));
        }

        System.out.println("\n--- Свободные окна лаборатории B-301 на " + day + " ---");
        service.freeSlots(r4, day).forEach(s ->
                System.out.println("  " + s + " (" + s.durationMinutes() + " мин)"));

        System.out.println("\n--- Подбор помещения по вместимости и времени ---");
        TimeInterval need = interval(day, 18, 0, 20, 0);
        Optional<Room> found = service.findSuitableRoom(40, need);
        if (found.isPresent()) {
            Room chosen = found.get();
            System.out.println("  Найдено: " + chosen +
                    " | score=" + chosen.suitabilityScore(40) +
                    " | " + chosen.getType().recommendation());
        } else {
            System.out.println("  Подходящее помещение не найдено");
        }

        Optional<Room> foundLab = service.findSuitableRoom(20, interval(day, 18, 30, 19, 30));
        foundLab.ifPresentOrElse(
                r -> System.out.println("  Для 20 чел.: " + r + " | score=" + r.suitabilityScore(20)),
                () -> System.out.println("  Для 20 чел. ничего не найдено"));

        System.out.println("\n--- Отмена брони ---");
        List<Booking> all = service.getBookings();
        if (!all.isEmpty()) {
            Booking toCancel = all.get(0);
            boolean ok = service.cancel(toCancel.getId());
            System.out.println("  Отменена " + toCancel.getId().substring(0, 8) + "... → " + ok);
            System.out.println("  Броней осталось: " + service.bookingCount());
        }

        System.out.println("\n=== Итог ===");
        System.out.println("Помещений: " + service.getRooms().size());
        System.out.println("Броней:    " + service.bookingCount());
        System.out.println("Объектов модели создано заведомо больше 30 (помещения + сотрудники + брони + интервалы).");
        System.out.println("Демонстрация базового уровня завершена успешно.");
    }

    private static TimeInterval interval(LocalDate day, int h1, int m1, int h2, int m2) {
        return new TimeInterval(
                LocalDateTime.of(day, LocalTime.of(h1, m1)),
                LocalDateTime.of(day, LocalTime.of(h2, m2)));
    }
}
