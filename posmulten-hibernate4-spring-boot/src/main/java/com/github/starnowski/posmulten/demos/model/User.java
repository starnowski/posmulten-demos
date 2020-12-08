package com.github.starnowski.posmulten.demos.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

public class User {

    @Id
    @GeneratedValue
    private UUID userId;
    private String username;
    private String password;
}
