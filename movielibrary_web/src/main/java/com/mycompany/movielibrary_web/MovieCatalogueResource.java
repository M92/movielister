package com.mycompany.movielibrary_web;
import dat076.group4.model.core.Movie;
import dat076.group4.model.dao.IMovieCatalogue;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
/**
 * REST Web Service for the movie catalogue (an Adapter) We always use
 * Response as return value (probably easier to modify)
 *
 */
@Path("movies") // Leading trailing slash doesn't matter, see web.xml
public class MovieCatalogueResource {
    @EJB
   IMovieCatalogue movieCatalogue;
    // Helper class used to build URI's. Injected by container 
    @Context
    private UriInfo uriInfo;
 
    
    //Create a movie
    @POST
   /* 
    public Response create(JsonObject data) {
        Movie p = new Movie(data.getString("title"), data.getInt("releaseYear"));
       */ 
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("title") String title, @FormParam("releaseYear") int releaseYear) {
        Movie p = new Movie(title, releaseYear);
        try {
            movieCatalogue.create(p);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(p.getId())).build(p);
            return Response.created(uri).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

     
    }
    /* We wont remove any movies from the database
    //Delete a movie with the id
    @DELETE
    @Path(value = "{id}")
    public Response delete(@PathParam("id") Long id) {
         try {
            movieCatalogue.delete(id);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    */
    //Update a movie
  /*  @PUT
    @Path(value = "{id}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response update(@PathParam(value = "id") Long id, JsonObject json) {
       try {
           Movie m = movieCatalogue.find(id);
           m.setTitle(json.getString("title"));
           m.setReleaseYear(json.getInt("releaseYear"));
            movieCatalogue.update(m);
            MovieCatalogueWrapper mw = new MovieCatalogueWrapper(m);
            return Response.ok(mw).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
*/    
    //Find a movie with the id
    @GET
    @Path(value = "{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Long id) {
       Movie m = movieCatalogue.find(id);
        if (m != null) {
            MovieCatalogueWrapper mw = new MovieCatalogueWrapper(m);
            return Response.ok(mw).build();
        } else {
            return Response.noContent().build();
        }
    }
    
    //Find all movies
    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response findAll() {
        List<MovieCatalogueWrapper> pwList = new ArrayList<>();
        List<Movie> movieList = movieCatalogue.findAll();
        for (Movie p : movieList) {
            pwList.add(new MovieCatalogueWrapper(p));
        }
        GenericEntity<List<MovieCatalogueWrapper>> ge = new GenericEntity<List<MovieCatalogueWrapper>>(pwList) {
        };
        return Response.ok(ge).build();
    }
    
    //Get the range of the list
    @GET
    @Path("range")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("fst") int fst, @QueryParam("max") int n) {
        List<MovieCatalogueWrapper> wrapList = new ArrayList<>();
        List<Movie> pList = movieCatalogue.findRange(fst, n);
        for (Movie p : pList) {
            wrapList.add(new MovieCatalogueWrapper(p));
        }
        GenericEntity<List<MovieCatalogueWrapper>> ge = new GenericEntity<List<MovieCatalogueWrapper>>(wrapList) {
        };
        return Response.ok(ge).build();
    }
    
    //Get the amount of movies
    @GET
    @Path(value = "count")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response count() {
        int c = movieCatalogue.count();
        JsonObject value = Json.createObjectBuilder().add("value", c).build();
        return Response.ok(value).build();
    }
}