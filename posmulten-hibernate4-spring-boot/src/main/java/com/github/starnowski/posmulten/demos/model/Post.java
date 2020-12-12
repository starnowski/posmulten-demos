package com.github.starnowski.posmulten.demos.model;

import com.github.starnowski.posmulten.demos.util.TenantAware;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "posts")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Post extends TenantAware {

    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User author;

    @Column(columnDefinition = "text")
    private String text;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;
}
