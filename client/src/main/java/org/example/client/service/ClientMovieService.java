package org.example.client.service;

import org.example.client.service.dto.ClientMovieDto;
import org.example.client.service.exceptions.ClientMovieNotRemovableException;
import org.example.client.service.exceptions.ClientSaleExpirationException;
import org.example.client.service.utils.exceptions.InputValidationException;
import org.example.client.service.utils.exceptions.InstanceNotFoundException;

import java.util.List;



public interface ClientMovieService {

    public Long addMovie(ClientMovieDto movie)
            throws InputValidationException;

    public void updateMovie(ClientMovieDto movie)
            throws InputValidationException, InstanceNotFoundException;

    public void removeMovie(Long movieId) throws InstanceNotFoundException,
            ClientMovieNotRemovableException;

    public List<ClientMovieDto> findMovies(String keywords);

    public Long buyMovie(Long movieId, String userId, String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException;

    public String getMovieUrl(Long saleId)
            throws InstanceNotFoundException, ClientSaleExpirationException;

}