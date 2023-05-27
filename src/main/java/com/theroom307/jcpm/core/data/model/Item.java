package com.theroom307.jcpm.core.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@ToString
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected String name;

    protected String description;

    @CreationTimestamp
    protected ZonedDateTime created;

    @UpdateTimestamp
    protected ZonedDateTime modified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var item = (Item) o;
        return Objects.equals(getId(), item.getId())
               && Objects.equals(getName(), item.getName())
               && Objects.equals(getDescription(), item.getDescription())
               && Objects.equals(getCreated(), item.getCreated())
               && Objects.equals(getModified(), item.getModified());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
