package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

@Data
public class Reservation extends Entity {
    private long reservationId;
    private String surname;
    private int apartmentNumber;
}
