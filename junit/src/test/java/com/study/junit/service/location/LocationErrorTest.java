package com.study.junit.service.location;

import com.study.junit.entity.Movie;
import com.study.junit.entity.User;
import com.study.junit.exception.NoMovieStockException;
import com.study.junit.exception.NullMovieException;
import com.study.junit.exception.NullUserException;
import com.study.junit.exception.SPCServiceException;
import com.study.junit.service.LocationService;
import com.study.junit.service.SPCService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(value = {MockitoExtension.class})
public class LocationErrorTest {

    @InjectMocks
    private LocationService service;

    @Mock
    private SPCService spcService;

    @Test
    public void locationTestNullUser(){
        List<Movie> movies = Arrays.asList(new Movie("Avatar", 1, 4.0));

        Executable locateWithNullUser = () ->
                service.locateMovie(null, movies);

        assertThrows(NullUserException.class, locateWithNullUser);
    }

    @Test
    public void locationTestNullMovie(){
        User user = new User("Bob");

        Executable locateNullMovie = () ->
                service.locateMovie(user, null);

        assertThrows(NullMovieException.class, locateNullMovie);
    }

    @Test
    public void shouldHandleUnexpectedSPCException(){
        User user = new User("Matheus");

        List<Movie> movies = List.of(
                new Movie("Madagascar", 1, 4.0));

        when(spcService.isBlocked(user))
                .thenThrow(RuntimeException.class);

        assertThrows(SPCServiceException.class,
                () -> service.locateMovie(user, movies));
    }
}
