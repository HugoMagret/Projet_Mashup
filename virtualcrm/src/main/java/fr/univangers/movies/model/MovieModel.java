package fr.univangers.movies.model;

import fr.univangers.movies.model.exceptions.InputValidationException;
import fr.univangers.movies.model.exceptions.InstanceNotFoundException;
import fr.univangers.movies.model.exceptions.MovieNotRemovableException;

import java.util.List;


public interface MovieModel {

    public Movie addMovie(Movie movie) throws InputValidationException;

    public void updateMovie(Movie movie) throws InputValidationException,
            InstanceNotFoundException;

    public void removeMovie(Long movieId) throws InstanceNotFoundException,
            MovieNotRemovableException;

    public Movie findMovie(Long movieId) throws InstanceNotFoundException;

    public List<Movie> findMovies(String keywords);

    
    /*To complete*/
    //public Sale buyMovie(Long movieId, String userId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException;
    //public Sale findSale(Long saleId) throws InstanceNotFoundException, SaleExpirationException;
}