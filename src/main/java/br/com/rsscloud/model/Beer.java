package br.com.rsscloud.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Beer extends PanacheEntity {
    public String sku;
    public String name;
    public String type;
    public String description;
}
