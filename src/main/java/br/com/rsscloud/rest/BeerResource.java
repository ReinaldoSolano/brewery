package br.com.rsscloud.rest;

import br.com.rsscloud.model.Beer;
import br.com.rsscloud.repository.BeerRepository;
import br.com.rsscloud.rest.dto.BeerRequest;
import br.com.rsscloud.service.BeerService;
import io.micrometer.core.annotation.Counted;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/beers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BeerResource {
    @Inject
    BeerRepository beerRepository;

    @Inject
    BeerService beerService;
    @GET
    @Counted(description = "contador busca cervejas")
    public List<Beer> listAll(){
        return Beer.listAll();
    }

    @GET
    @Path("/{id}")
    public Beer getSingle (@PathParam("id") Long id){
        return Beer.findById(id);
    }

    @POST
    @Transactional
    public Response create(BeerRequest request){
        beerService.create(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Beer update(@PathParam("id") Long id, Beer beer){

        Beer entity = Beer.findById(id);
        if(entity == null){
            throw new WebApplicationException("Beer not found", Response.Status.NOT_FOUND);
        }
        entity.name = beer.name;
        entity.description = beer.description;
        entity.type = beer.type;

        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id){

       if(Beer.deleteById(id)){
           return Response.status(Response.Status.NO_CONTENT).build();
       }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    @GET
    @Path("/type/{type}")
    public List<Beer> getByType(@PathParam("type") String type){
        return beerService.findByType(type);
    }
}
