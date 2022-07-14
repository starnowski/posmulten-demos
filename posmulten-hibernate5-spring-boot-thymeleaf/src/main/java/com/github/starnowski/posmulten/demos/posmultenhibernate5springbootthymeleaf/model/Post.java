package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model;

import com.github.starnowski.posmulten.hibernate.core.TenantTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "posts")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@TenantTable
public class Post {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User author;

    @Column(columnDefinition = "text")
    private String text;

//    @ManyToMany
//    private Set<Category> categories;
}
