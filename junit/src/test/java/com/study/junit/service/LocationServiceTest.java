package com.study.junit.service;


import com.study.junit.entity.Location;
import com.study.junit.entity.Movie;
import com.study.junit.entity.User;
import com.study.junit.exception.NoMovieStockException;
import com.study.junit.exception.NullMovieException;
import com.study.junit.exception.NullUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.study.junit.util.DateUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class LocationServiceTest {

	private LocationService service;
	
	@BeforeEach
	public void setup(){
		service = new LocationService();
	}
	
	@Test
	public void locationTest(){
		assumeFalse(verifyDayOfWeek(new Date(), Calendar.SATURDAY));

		User user = new User("Bia");
		List<Movie> movies = Arrays.asList(new Movie("Spider Man", 1, 5.0));

		Location location = service.locateMovie(user, movies);

		assertAll(
				() -> assertEquals(location.getPrice(), 5.0),
				() -> assertTrue(isSameDate(location.getLocationDate(), new Date())),
				() -> assertTrue(isSameDate(location.getDevolutionDate(), getFutureDateAddingDays(1)))
		);
	}
	
	@Test
	public void testLocationNoMovieStock(){
		User user = new User("James");
		List<Movie> movies = Arrays.asList(new Movie("Batman", 0, 4.0));

		Executable locateMovie = () ->
				service.locateMovie(user, movies);

		assertThrows(NoMovieStockException.class, locateMovie);
	}
	
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
	public void moviesPercentageDiscount(){
		User user = new User("Jack");

		List<Movie> movies = Arrays.asList(
				new Movie("Aladdin", 1, 4.0),
				new Movie("Harry Potter", 1, 4.0),
				new Movie("Batman", 1, 4.0),
				new Movie("Jumanji", 1, 4.0),
				new Movie("Spider Man", 1, 4.0),
				new Movie("Avengers", 1, 4.0),
				new Movie("Shrek", 1, 4.0)
		);

		Location location = service.locateMovie(user, movies);

		List<Double> moviesPrices = location.getMovies().stream()
				.map(Movie::getPrice).collect(Collectors.toList());

		assertAll(
				() -> assertEquals(4.0, moviesPrices.get(0)),
				() -> assertEquals(4.0, moviesPrices.get(1)),
				() -> assertEquals(3.0, moviesPrices.get(2)),
				() -> assertEquals(2.0, moviesPrices.get(3)),
				() -> assertEquals(1.0, moviesPrices.get(4)),
				() -> assertEquals(0.0, moviesPrices.get(5)),
				() -> assertEquals(4.0, moviesPrices.get(6)),
				() -> assertEquals(18.0, location.getPrice())
		);
	}

	@Test
	public void shouldReturnOnMondayWhenLocatedOnSaturday(){
		assumeTrue(verifyDayOfWeek(new Date(), Calendar.SATURDAY));

		User user = new User("Bob");
		List<Movie> movies = Arrays.asList(new Movie("Avatar", 1, 4.0));

		Location location = service.locateMovie(user, movies);
		Date devolution = location.getDevolutionDate();

		assertTrue(verifyDayOfWeek(devolution, Calendar.MONDAY));
	}
}
