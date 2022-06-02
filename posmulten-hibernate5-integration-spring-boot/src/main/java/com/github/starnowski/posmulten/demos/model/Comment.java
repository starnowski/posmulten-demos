package com.github.starnowski.posmulten.demos.model;


import com.github.starnowski.posmulten.hibernate.core.TenantTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "comments")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@TenantTable
public class Comment {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User author;
    @ManyToOne
    private Post post;
    @Column(columnDefinition = "text")
    private String text;
}
