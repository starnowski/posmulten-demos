package com.github.starnowski.posmulten.demos.model;


import com.github.starnowski.posmulten.demos.util.TenantAware;
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
public class Comment extends TenantAware {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User author;
    @ManyToOne
    private Post post;
}
