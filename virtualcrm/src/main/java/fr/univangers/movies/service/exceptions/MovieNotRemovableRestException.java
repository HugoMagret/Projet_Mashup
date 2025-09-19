package fr.univangers.movies.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MovieNotRemovableRestException extends Exception {
    private Long movieId;

    public MovieNotRemovableRestException(Long movieId) {
        super("Movie with id=\"" + movieId + "\n cannot be deleted because it has sales");
        this.movieId = movieId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

}