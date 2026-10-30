package ru.booking;

public enum RoomType {
    LECTURE("Лекционная аудитория", false),
    LAB("Лаборатория", true),
    SEMINAR("Семинарская", false),
    CONFERENCE("Конференц-зал", true),
    COMPUTER("Компьютерный класс", true);

    private final String description;
    private final boolean requiresSpecialEquipment;

    RoomType(String description, boolean requiresSpecialEquipment) {
        this.description = description;
        this.requiresSpecialEquipment = requiresSpecialEquipment;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresSpecialEquipment() {
        return requiresSpecialEquipment;
    }


    public String recommendation() {
        return switch (this) {
            case LECTURE -> "Подходит для потоковых лекций";
            case LAB -> "Требуется предварительная проверка оборудования";
            case SEMINAR -> "Оптимально для групп до 25 человек";
            case CONFERENCE -> "Необходимо бронировать заранее";
            case COMPUTER -> "Проверьте наличие лицензий ПО";
        };
    }
}
