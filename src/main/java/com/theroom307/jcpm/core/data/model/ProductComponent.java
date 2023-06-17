package com.theroom307.jcpm.core.data.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "product_components")
@NoArgsConstructor
@AllArgsConstructor
public class ProductComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_components_seq")
    @SequenceGenerator(name = "product_components_seq")
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Component component;

}