package com.github.starnowski.posmulten.demos.model;

import javax.persistence.*;

public class Post {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User author;

    @Column(columnDefinition = "text")
    private String text;
}
