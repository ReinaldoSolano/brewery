package br.com.rsscloud.rest;

import br.com.rsscloud.model.Beer;
import br.com.rsscloud.repository.BeerRepository;
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
    @GET
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
    public Response create(Beer beer){
        if(beer.id != null){
            throw new WebApplicationException("identifier must be null when creating a beer", Response.Status.BAD_REQUEST);
        }
        beer.persist();
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Beer create(@PathParam("id") Long id, Beer beer){

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
        return beerRepository.findByType(type);
    }
}
