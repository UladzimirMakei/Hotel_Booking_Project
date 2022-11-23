package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Reservation implements Cloneable, Serializable {
    private long reservationId;
    private String surname;
    private int apartmentNumber;
}
