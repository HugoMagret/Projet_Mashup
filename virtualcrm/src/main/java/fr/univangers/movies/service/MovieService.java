package fr.univangers.movies.service;

import fr.univangers.movies.service.exceptions.InputValidationRestException;
import fr.univangers.movies.service.exceptions.InstanceNotFoundRestException;
import fr.univangers.movies.service.exceptions.MovieNotRemovableRestException;

import java.util.List;



public interface MovieService {

    public RestMovieDto addMovie(RestMovieDto movie) throws InputValidationRestException;

    public void updateMovie(RestMovieDto movie) throws InputValidationRestException,
            InstanceNotFoundRestException;

    public void removeMovie(Long movieId) throws InstanceNotFoundRestException,
            MovieNotRemovableRestException;

    public RestMovieDto findMovie(Long movieId) throws InstanceNotFoundRestException;

    public List<RestMovieDto> findMovies(String keywords);
    
    /*To implement*/
    //public RestSaleDto buyMovie(Long movieId, String userId, String creditCardNumber) throws InstanceNotFoundRestException, InputValidationRestException;
    //public RestSaleDto findSale(Long saleId) throws InstanceNotFoundRestException, SaleExpirationRestException;
}