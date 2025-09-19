package fr.univangers.movies.service;

import java.util.List;

import fr.univangers.movies.model.Movie;
import fr.univangers.movies.model.MovieModelFactory;
import fr.univangers.movies.model.exceptions.InputValidationException;
import fr.univangers.movies.model.exceptions.InstanceNotFoundException;
import fr.univangers.movies.model.exceptions.MovieNotRemovableException;
import fr.univangers.movies.service.exceptions.InputValidationRestException;
import fr.univangers.movies.service.exceptions.InstanceNotFoundRestException;
import fr.univangers.movies.service.exceptions.MovieNotRemovableRestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MovieServiceImpl implements MovieService {	
	
	
	@RequestMapping(value="/movies", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
    public RestMovieDto addMovie(@RequestBody RestMovieDto movie) throws InputValidationRestException {
    	Movie m = MovieToRestMovieDtoConversor.toMovie(movie);
    	try {
			Movie r = MovieModelFactory.getModel().addMovie(m);
			RestMovieDto dto = MovieToRestMovieDtoConversor.toRestMovieDto(r);
			return dto;
		} catch (InputValidationException e) {
			throw new InputValidationRestException(e.getMessage());
		}
    	
    }

    
    @RequestMapping(value="/movies/{movieId}", method=RequestMethod.PUT)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateMovie(@RequestBody RestMovieDto movie) throws InputValidationRestException,InstanceNotFoundRestException {
    	Movie m = MovieToRestMovieDtoConversor.toMovie(movie);
		try {
			MovieModelFactory.getModel().updateMovie(m);
		} catch (InputValidationException e) {
				throw new InputValidationRestException(e.getMessage());
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundRestException(movie.getMovieId(), InstanceNotFoundRestException.class.getName());
		}
    }

    @RequestMapping(value="/movies/{movieId}", method=RequestMethod.DELETE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeMovie(@PathVariable(value="movieId")Long movieId) throws InstanceNotFoundRestException,  MovieNotRemovableRestException{
    	try {
			MovieModelFactory.getModel().removeMovie(movieId);
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundRestException(movieId, InstanceNotFoundRestException.class.getName());
		} catch (MovieNotRemovableException e) {
			throw new MovieNotRemovableRestException(movieId);
		}
    	
    }

    
    public RestMovieDto findMovie(Long movieId) throws InstanceNotFoundRestException {
    	try {
    		Movie r = MovieModelFactory.getModel().findMovie(movieId);
			return MovieToRestMovieDtoConversor.toRestMovieDto(r);
		} catch (InstanceNotFoundException e) {
			throw new InstanceNotFoundRestException(movieId, InstanceNotFoundRestException.class.getName());
		}
    	
    }

    @RequestMapping(value="/movies", method=RequestMethod.GET)
    public List<RestMovieDto> findMovies(@RequestParam(name = "keywords", defaultValue = "")String keywords) {
    	List<Movie> l = MovieModelFactory.getModel().findMovies(keywords);
    	return MovieToRestMovieDtoConversor.toRestMovieDtos(l);	
    }
    
    /*To implement*/
    //public RestSaleDto buyMovie(Long movieId, String userId, String creditCardNumber) throws InstanceNotFoundRestException, InputValidationRestException;
    //public RestSaleDto findSale(Long saleId) throws InstanceNotFoundRestException, SaleExpirationRestException;
    
}