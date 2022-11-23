package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Apartment extends Entity {
    private int number;
    private String type;
    private BigDecimal price;

}
