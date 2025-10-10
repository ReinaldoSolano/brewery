package br.com.rsscloud.repository;

import br.com.rsscloud.model.Beer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BeerRepository implements PanacheRepository<Beer> {
    public List<Beer> findByType(String type) {
        return find("type", type).list();
    }
}
