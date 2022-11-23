package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Client implements Cloneable, Serializable {
    private long clientId;
    private String surname;
    private String name;
    private String login;
    private String password;


}
