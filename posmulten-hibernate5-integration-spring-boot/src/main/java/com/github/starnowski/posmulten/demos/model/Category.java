package com.github.starnowski.posmulten.demos.model;

import com.github.starnowski.posmulten.hibernate.core.TenantTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "categories")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@TenantTable
public class Category {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private String text;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts;
}
