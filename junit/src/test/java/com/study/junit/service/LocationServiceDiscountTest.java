package com.study.junit.service;

import com.study.junit.entity.Location;
import com.study.junit.entity.Movie;
import com.study.junit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationServiceDiscountTest {

    private LocationService service;

    @BeforeEach
    public void setup(){
        service = new LocationService();
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
        Arguments args6 = Arguments.of(sub(movies, 7), 18.0, "7th movie with 0.50 discount");

        return Stream.of(args0, args1, args2, args3, args4, args5, args6);
    }

    private static List<Movie> sub(List<Movie> movies, Integer quantity){
        return movies.subList(0, quantity);
    }
}
