package com.bookstore.entity;

import com.bookstore.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "publishers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Publisher extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;
}
