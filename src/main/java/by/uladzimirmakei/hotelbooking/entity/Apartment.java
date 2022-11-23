package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Apartment implements Cloneable, Serializable {
    private int number;
    public String type;

}
