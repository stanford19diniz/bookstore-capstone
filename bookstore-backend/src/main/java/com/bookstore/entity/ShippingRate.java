package com.bookstore.entity;

import com.bookstore.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_rates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShippingRate extends BaseEntity {

    @Column(name = "zone", nullable = false, length = 100)
    private String zone;

    @Column(name = "weight_from", nullable = false, precision = 8, scale = 3)
    private BigDecimal weightFrom;

    @Column(name = "weight_to", nullable = false, precision = 8, scale = 3)
    private BigDecimal weightTo;

    @Column(name = "rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal rate;
}
