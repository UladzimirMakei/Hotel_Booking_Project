package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Apartment implements Cloneable, Serializable {
    private int number;
    private String type;
    private BigDecimal price;

}
