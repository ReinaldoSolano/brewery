package br.com.rsscloud.rest;

import br.com.rsscloud.model.Beer;
import br.com.rsscloud.rest.dto.BeerRequest;
import br.com.rsscloud.service.BeerService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;

/**
 * REST resource for managing beers.
 */
@Path("/beers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BeerResource {

    private static final Logger LOGGER = Logger.getLogger(BeerResource.class);

    private final BeerService beerService;

    @Inject
    public BeerResource(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Retrieve all beers.
     *
     * @return List of all beers
     */
    @GET
    @Counted(value = "call_beers_total", description = "contador busca cervejas")
    @Timed(value = "duration_beers_endpoint", description = "duração da chamada")
    public Response listAll() {
        LOGGER.debug("Retrieving all beers");
        List<Beer> beers = Beer.listAll();
        return Response.ok(beers).build();
    }

    /**
     * Get a single beer by ID.
     *
     * @param id Beer ID
     * @return Beer if found
     */
    @GET
    @Path("/{id}")
    public Response getSingle(@PathParam("id") Long id) {
        LOGGER.debugf("Retrieving beer with id: %d", id);
        Beer beer = Beer.findById(id);

        if (beer == null) {
            LOGGER.warnf("Beer not found with id: %d", id);
            throw new WebApplicationException("Beer not found", Response.Status.NOT_FOUND);
        }

        return Response.ok(beer).build();
    }

    /**
     * Create a new beer.
     *
     * @param request Beer creation request
     * @return Created beer
     */
    @POST
    @Transactional
    public Response create(@Valid BeerRequest request) {
        LOGGER.infof("Creating new beer with name: %s", request.name);

        Beer beer = beerService.create(request);
        return Response.created(
            UriBuilder.fromResource(BeerResource.class)
                     .path(String.valueOf(beer.id))
                     .build()
        ).entity(beer).build();
    }

    /**
     * Update an existing beer.
     *
     * @param id ID of the beer to update
     * @param beer Updated beer data
     * @return Updated beer
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, @Valid Beer beer) {
        LOGGER.infof("Updating beer with id: %d", id);

        if (!Objects.equals(id, beer.id)) {
            LOGGER.warnf("Path id (%d) doesn't match body id (%d)", id, beer.id);
            throw new WebApplicationException("Path ID and body ID don't match", Response.Status.BAD_REQUEST);
        }

        Beer entity = Beer.findById(id);
        if (entity == null) {
            LOGGER.warnf("Beer not found with id: %d", id);
            throw new WebApplicationException("Beer not found", Response.Status.NOT_FOUND);
        }

        entity.name = beer.name;
        entity.description = beer.description;
        entity.type = beer.type;

        return Response.ok(entity).build();
    }

    /**
     * Delete a beer by ID.
     *
     * @param id ID of the beer to delete
     * @return No content on success
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        LOGGER.infof("Deleting beer with id: %d", id);

        if (Beer.deleteById(id)) {
            return Response.noContent().build();
        }

        LOGGER.warnf("Beer not found with id: %d", id);
        throw new WebApplicationException("Beer not found", Response.Status.NOT_FOUND);
    }

    /**
     * Find beers by type.
     *
     * @param type Beer type to search for
     * @return List of beers matching the type
     */
    @GET
    @Path("/type/{type}")
    public Response getByType(@PathParam("type") String type) {
        LOGGER.debugf("Searching beers by type: %s", type);
        List<Beer> beers = beerService.findByType(type);
        return Response.ok(beers).build();
    }
}
