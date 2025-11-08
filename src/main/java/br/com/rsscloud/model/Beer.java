package br.com.rsscloud.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Beer extends PanacheEntity {
    @NotBlank(message = "SKU is required")
    @Size(min = 8, max = 50, message = "SKU must be between 8 and 50 characters")
    public String sku;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    public String name;

    @NotBlank(message = "Type is required")
    @Size(min = 3, max = 50, message = "Type must be between 3 and 50 characters")
    public String type;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    public String description;
}
