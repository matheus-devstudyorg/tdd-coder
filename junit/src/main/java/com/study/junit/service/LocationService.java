package com.study.junit.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.IntPredicate;

import com.study.junit.entity.Movie;
import com.study.junit.entity.Location;
import com.study.junit.entity.User;
import com.study.junit.exception.*;
import com.study.junit.helper.CalendarHelper;
import com.study.junit.helper.LoggingHelper;
import com.study.junit.repository.LocationRepository;
import lombok.AllArgsConstructor;

import static com.study.junit.util.DateUtil.*;

@AllArgsConstructor
public class LocationService {

	private LocationRepository locationRepository;
	private SPCService spcService;
	private EmailService emailService;
	private LoggingHelper loggingHelper;
	private CalendarHelper calendarHelper;

	public void extendLocationTime(Location location, int qttDays){
		Location newLocation = new Location(
				location.getUser(), location.getMovies(),
				calendarHelper.today(),
				calendarHelper.futureOf(qttDays),
				extendedLocationPrice(location, qttDays)
		);

		locationRepository.save(newLocation);
	}

	private double extendedLocationPrice(Location location, int qttDays){
		double locationBasePrice = getTotalPrice(location.getMovies());
		double extendedLocationPrice = location.getPrice() + qttDays * locationBasePrice;

		return extendedLocationPrice;
	}

	public void notifyDelays(){
		for (Location location : locationRepository.getPendingLocations()){
			if (calendarHelper.today().after(location.getLocationDate())) {
				emailService.notifyDelay(location.getUser());
			}
		}
	}
	
	public Location locateMovie(User user, List<Movie> movies){
		checkIfUserNotNull(user);
		checkIfMovieNotNull(movies);
		checkIfMovieIsOnStock(movies);
		checkIfIsUserNotBlocked(user);

		Location location = buildNewLocation(user, movies);
		locationRepository.save(location);
		loggingHelper.infoLocationDone(location);

		return location;
	}

	private void checkIfUserNotNull(User user){
		if(user == null) {
			throw new NullUserException();
		}
	}

	private void checkIfMovieNotNull(List<Movie> movies){
		if(movies == null || movies.size() == 0) {
			throw new NullMovieException();
		}
	}

	private void checkIfMovieIsOnStock(List<Movie> movies){
		IntPredicate hasStock = stock -> stock > 0;

		if(!movies.stream().mapToInt(Movie::getStock).allMatch(hasStock)) {
			throw new NoMovieStockException();
		}
	}

	private void checkIfIsUserNotBlocked(User user) {
		try {
			if (spcService.isBlocked(user)) {
				throw new UserBlockedException();
			}
		}
		catch (UserBlockedException e){
			throw e;
		}
		catch (Exception e){
			throw new SPCServiceException(e);
		}
	}

	private Location buildNewLocation(User user, List<Movie> movies){
		Location location = new Location();

		location.setUser(user);
		location.setLocationDate(getLocationDate());
		location.setDevolutionDate(getDevolutionDate());

		List<Movie> moviesWithDiscount = getMoviesWithDiscount(movies);
		location.setPrice(getTotalPrice(moviesWithDiscount));
		location.setMovies(moviesWithDiscount);

		return location;
	}

	private Date getLocationDate(){
		return new Date();
	}

	private Double getTotalPrice(List<Movie> movies){
		return movies.stream().mapToDouble(Movie::getPrice).sum();
	}

	private Date getDevolutionDate(){
		Date devolution = calendarHelper.futureOf(1);

		if (verifyDayOfWeek(devolution, Calendar.SUNDAY)) {
			devolution = addDays(devolution, 1);
		}

		return devolution;
	}

	private List<Movie> getMoviesWithDiscount(List<Movie> movies) {
		List<Movie> moviesWithDiscount = new ArrayList<>();

		for (int i = 0; i < movies.size(); i++){
			Movie movie = movies.get(i);
			Movie newMovie = getMovieWithDiscount(movie, i);

			moviesWithDiscount.add(newMovie);
		}

		return moviesWithDiscount;
	}

	private Movie getMovieWithDiscount(Movie movie, int index){
		return new Movie(
				movie.getName(),
				movie.getStock(),
				getPriceWithDiscount(movie.getPrice(), index)
		);
	}

	private Double getPriceWithDiscount(Double price, int index) {
		if (index == 0){return price;}
		if (index == 1){return price;}
		if (index == 2){return price * 0.75;}
		if (index == 3){return price * 0.5;}
		if (index == 4){return price * 0.25;}
		if (index == 5){return 0.0;}

		return price;
	}
}