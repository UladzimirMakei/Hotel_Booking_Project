package by.uladzimirmakei.hotelbooking.entity;

import lombok.Data;

@Data
public class Client extends Entity {
    private long clientId;
    private String surname;
    private String name;
    private String login;
    private String password;


}
