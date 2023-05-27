package com.theroom307.jcpm.core.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @CreationTimestamp
    private ZonedDateTime created;

    @UpdateTimestamp
    private ZonedDateTime modified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Component component = (Component) o;
        return Objects.equals(getId(), component.getId())
               && Objects.equals(getName(), component.getName())
               && Objects.equals(getDescription(), component.getDescription())
               && Objects.equals(getCreated(), component.getCreated())
               && Objects.equals(getModified(), component.getModified());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
