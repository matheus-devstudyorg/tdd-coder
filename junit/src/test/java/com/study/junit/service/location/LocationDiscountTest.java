package com.study.junit.service.location;

import com.study.junit.entity.Location;
import com.study.junit.entity.Movie;
import com.study.junit.entity.User;
import com.study.junit.fake.FakeLocationRepository;
import com.study.junit.helper.CalendarHelper;
import com.study.junit.helper.LoggingHelper;
import com.study.junit.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class LocationDiscountTest {

    private LocationService service;

    @BeforeEach
    public void setup(){
        service = new LocationService(
                new FakeLocationRepository(),
                mock(SPCService.class),
                mock(EmailService.class),
                mock(LoggingHelper.class),
                spy(CalendarHelper.class)
        );
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

    @ParameterizedTest(name = "{2}")
    @MethodSource("provideMoviesAndLocationPrice")
    public void locationTestTotalPriceWithDiscount(List<Movie> movies, Double expectedPrice, String title){
        User user = new User("Bob");
        Location location = service.locateMovie(user, movies);

        assertEquals(location.getPrice(), expectedPrice);
    }

    public static Stream<Arguments> provideMoviesAndLocationPrice(){
        List<Movie> movies = List.of(
                new Movie("Aladdin", 1, 4.0),
                new Movie("Harry Potter", 1, 4.0),
                new Movie("Batman", 1, 4.0),
                new Movie("Jumanji", 1, 4.0),
                new Movie("Spider Man", 1, 4.0),
                new Movie("Avengers", 1, 4.0),
                new Movie("Shrek", 1, 4.0)
        );

        Arguments args0 = Arguments.of(sub(movies, 1), 4.0, "1st movie with no discount");
        Arguments args1 = Arguments.of(sub(movies, 2), 8.0, "2nd movie with no discount");
        Arguments args2 = Arguments.of(sub(movies, 3), 11.0, "3rd movie with 0.25 discount");
        Arguments args3 = Arguments.of(sub(movies, 4), 13.0, "4th movie with 0.50 discount");
        Arguments args4 = Arguments.of(sub(movies, 5), 14.0, "5th movie with 0.75 discount");
        Arguments args5 = Arguments.of(sub(movies, 6), 14.0, "6th movie with 1.00 discount");
        Arguments args6 = Arguments.of(sub(movies, 7), 18.0, "7th movie with no discount");

        return Stream.of(args0, args1, args2, args3, args4, args5, args6);
    }

    private static List<Movie> sub(List<Movie> movies, Integer quantity){
        return movies.subList(0, quantity);
    }
}
