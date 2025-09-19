package fr.univangers.movies.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



import fr.univangers.movies.model.exceptions.InputValidationException;
import fr.univangers.movies.model.exceptions.InstanceNotFoundException;
import fr.univangers.movies.model.exceptions.MovieNotRemovableException;
import fr.univangers.movies.model.utils.ModelConstants;
import fr.univangers.movies.model.utils.PropertyValidator;

public class MovieModelImpl implements MovieModel {
	private static List<Movie> lMovies;
	private static Long counter;

	
	static{
		lMovies = new ArrayList<Movie>();
		counter = (long) 1;
		Movie m = new Movie("The Dark Knight",(short) 1,"Description 1 ...", 20);
		m.setMovieId(counter++);
		lMovies.add(m);
		m = new Movie("The Dark Knight Rises",(short) 2,"Description 2 ...", 30);
		m.setMovieId(counter++);
		lMovies.add(m);
	}
	
	public MovieModelImpl() {
	
	}

	private void validateMovie(Movie movie) throws InputValidationException {
		PropertyValidator.validateMandatoryString("title", movie.getTitle());
		PropertyValidator.validateLong("runtime", movie.getRuntime(), 0, ModelConstants.MAX_RUNTIME);
		PropertyValidator.validateMandatoryString("description", movie.getDescription());
		PropertyValidator.validateDouble("price", movie.getPrice(), 0, ModelConstants.MAX_PRICE);
	}

	@Override
	public Movie addMovie(Movie movie) throws InputValidationException {
		validateMovie(movie);
		movie.setMovieId(counter++);
		movie.setCreationDate(LocalDateTime.now());
		lMovies.add(movie);
		return movie;
	}

	@Override
	public void updateMovie(Movie movie) throws InputValidationException, InstanceNotFoundException {
		validateMovie(movie);
		Movie m = this.findMovie(movie.getMovieId());
		m.setCreationDate(movie.getCreationDate());
		m.setDescription(movie.getDescription());
		m.setPrice(movie.getPrice());
		m.setRuntime(movie.getRuntime());
		m.setTitle(movie.getTitle());
	}

	@Override
	public void removeMovie(Long movieId) throws InstanceNotFoundException, MovieNotRemovableException {
		Movie m = this.findMovie(movieId);
		lMovies.remove(m);
	}

	@Override
	public Movie findMovie(Long movieId) throws InstanceNotFoundException {
		for (Movie m: lMovies) {
			if (m.getMovieId()==movieId)
				return m;
		}	
		throw new InstanceNotFoundException(movieId, Movie.class.getName());
	}

	@Override
	public List<Movie> findMovies(String keywords) {
		List<Movie> result = new ArrayList<Movie>();
		for (Movie m: lMovies) {
			if (m.getTitle().contains(keywords))
				result.add(m);
			
		}
		return result;
	}

	/*To complete*/
	
/*	@Override
	public Sale buyMovie(Long movieId, String userId, String creditCardNumber)
			throws InstanceNotFoundException, InputValidationException {}*/

/*	@Override
	public Sale findSale(Long saleId) throws InstanceNotFoundException, SaleExpirationException {}*/

/*	private static String getMovieUrl(Long movieId) {
		return BASE_URL + movieId + "/" + UUID.randomUUID().toString();
	}
	*/
}