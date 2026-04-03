package itmo.course.lab1.client;

import itmo.course.lab1.exception.ValidationException;

import java.util.Objects;
import java.util.UUID;

public class Client {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private String address;
    private String passportNumber;

    Client(String firstName, String lastName, String address, String passportNumber) {
        this.id = UUID.randomUUID();
        this.firstName = requireValue(firstName, "Имя обязательно");
        this.lastName = requireValue(lastName, "Фамилия обязательна");
        this.address = normalizeOptional(address);
        this.passportNumber = normalizeOptional(passportNumber);
    }

    public static ClientBuilder.FirstNameStep builder() {
        return ClientBuilder.builder();
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public boolean isSuspicious() {
        return address == null || passportNumber == null;
    }

    public void updateAddress(String address) {
        this.address = requireValue(address, "Адрес не может быть пустым");
    }

    public void updatePassportNumber(String passportNumber) {
        this.passportNumber = requireValue(passportNumber, "Паспорт не может быть пустым");
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    private static String requireValue(String value, String message) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new ValidationException(message);
        }

        return normalized;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Client client)) {
            return false;
        }
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
