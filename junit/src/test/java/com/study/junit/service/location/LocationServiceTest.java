package com.study.junit.service.location;


import com.study.junit.entity.Location;
import com.study.junit.entity.Movie;
import com.study.junit.entity.User;
import com.study.junit.exception.*;
import com.study.junit.helper.CalendarHelper;
import com.study.junit.helper.LoggingHelper;
import com.study.junit.repository.LocationRepository;
import com.study.junit.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.study.junit.util.DateUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

@ExtendWith(value = {MockitoExtension.class})
public class LocationServiceTest {

	@InjectMocks
	private LocationService service;

	@Mock
	private SPCService spcService;

	@Mock
	private LocationRepository locationRepository;

	@Spy
	private CalendarHelper calendarHelper;

	@Spy
	private LoggingHelper loggingHelper;

	@Test
	public void locationTest(){
		assumeFalse(verifyDayOfWeek(calendarHelper.today(), Calendar.SATURDAY));
		doNothing().when(loggingHelper).infoLocationDone(any());

		User user = new User("Bia");
		List<Movie> movies = Arrays.asList(new Movie("Spider Man", 1, 5.0));

		Location location = service.locateMovie(user, movies);

		assertAll(
				() -> assertEquals(location.getPrice(), 5.0),
				() -> assertTrue(isSameDate(location.getLocationDate(), new Date())),
				() -> assertTrue(isSameDate(location.getDevolutionDate(), calendarHelper.futureOf(1)))
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
	public void shouldNotLocateToBlockedUser(){
		User user = new User("Matheus");

		List<Movie> movies = List.of(
				new Movie("Madagascar", 1, 4.0));

		when(spcService.isBlocked(user))
				.thenReturn(true);

		assertThrows(UserBlockedException.class,
				() -> service.locateMovie(user, movies));
	}
}
