package com.theroom307.jcpm.core.data.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Builder
@Entity
@Table(name = "product_components")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_components_seq")
    @SequenceGenerator(name = "product_components_seq")
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Component component;

    @Builder.Default
    private Integer quantity = 1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductComponent that = (ProductComponent) o;
        return Objects.equals(getId(), that.getId())
               && Objects.equals(getProduct(), that.getProduct())
               && Objects.equals(getComponent(), that.getComponent())
               && Objects.equals(getQuantity(), that.getQuantity());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode()
               + (getProduct() == null ? 0 : getProduct().hashCode())
               + (getComponent() == null ? 0 : getComponent().hashCode())
               + (getId() == null ? 0 : getId().hashCode());
    }
}