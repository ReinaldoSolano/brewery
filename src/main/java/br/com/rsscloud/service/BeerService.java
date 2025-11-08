package br.com.rsscloud.service;

import br.com.rsscloud.model.Beer;
import br.com.rsscloud.repository.BeerRepository;
import br.com.rsscloud.rest.dto.BeerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class BeerService {
    @Inject
    BeerRepository beerRepository;



    @Transactional
    public Beer create(BeerRequest request) {

        validName(request.name);
        Beer beer = new Beer();
        beer.name = request.name;
        beer.type = request.type;
        beer.sku = generateSKU();
        beer.description = "Lipi's ".concat(request.name.trim()).concat(" beer");
        beer.persist();
        return beer;


    }

    public void validName(String name) {
        boolean existsName = beerRepository.findByName(name).stream().findFirst().isPresent();
        if(existsName)
            throw new WebApplicationException("name must be unique to create beer", Response.Status.BAD_REQUEST);
    }

    private String  generateSKU() {
        boolean skuValid = true;
        String sku = "";
        while (skuValid) {
            sku = "LIPI-".concat(UUID.randomUUID().toString());
            skuValid = existsSKU(sku);
        }
        return sku;
    }

    private boolean existsSKU(String sku) {
        if(Objects.isNull(sku)){
            return true;
        }
        return beerRepository.findBySku(sku).stream().findFirst().isPresent();
    }


    public List<Beer> findByType(String type) {
        return beerRepository.findByType(type);
    }
}