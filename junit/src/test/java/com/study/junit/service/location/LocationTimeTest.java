package com.study.junit.service.location;

import com.study.junit.entity.Location;
import com.study.junit.entity.Movie;
import com.study.junit.entity.User;
import com.study.junit.helper.LoggingHelper;
import com.study.junit.repository.LocationRepository;
import com.study.junit.helper.CalendarHelper;
import com.study.junit.service.EmailService;
import com.study.junit.service.LocationService;
import com.study.junit.service.SPCService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.study.junit.util.DateUtil.*;
import static com.study.junit.util.DateUtil.isSameDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(value = {MockitoExtension.class})
public class LocationTimeTest {
    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private SPCService spcService;

    @Mock
    private LoggingHelper loggingHelper;

    @Spy
    private CalendarHelper calendarHelper;

    @Test
    public void shouldReturnOnMondayWhenLocatedOnSaturday() throws Exception {
        doReturn(getDate(16,4,2022))
                .when(calendarHelper).today();

        User user = new User("Bob");
        List<Movie> movies = Arrays.asList(
                new Movie("Avatar", 1, 4.0));

        Location location = locationService.locateMovie(user, movies);
        Date devolution = location.getDevolutionDate();

        assertTrue(verifyDayOfWeek(devolution, Calendar.MONDAY));
    }

    @Test
    public void shouldExtendLocationTime(){
        Location location = new Location(
                new User("Mika"),
                new Movie("Madagascar", 1, 4.0),
                calendarHelper.yesterday(),
                calendarHelper.beforeYesterday(),
                4.0
        );

        locationService.extendLocationTime(location, 3);

        ArgumentCaptor<Location> captor =
                ArgumentCaptor.forClass(Location.class);

        verify(locationRepository)
                .save(captor.capture());

        Location locationCaught = captor.getValue();

        assertAll(
                () -> assertEquals(16, locationCaught.getPrice()),
                () -> assertIsSameDate(calendarHelper.today(), locationCaught.getLocationDate()),
                () -> assertIsSameDate(calendarHelper.futureOf(3), locationCaught.getDevolutionDate())
        );
    }

    private void assertIsSameDate(Date expected, Date actual){
        assertTrue(isSameDate(expected, actual));
    }

    @Test
    public void shouldNotifyDelayToLateUsers(){
        User mockedLateUser1 = new User("Matheus");
        User mockedLateUser2 = new User("Mika");
        User mockedOnTimeUser = new User("Jonny");

        List<Location> mockedLocations = List.of(
                new Location(
                        mockedLateUser1, new Movie("Madagascar", 1, 4.0),
                        calendarHelper.yesterday(), calendarHelper.beforeYesterday(), 4.0
                ),
                new Location(
                        mockedLateUser2, new Movie("Spider Man", 1, 4.0),
                        calendarHelper.yesterday(), calendarHelper.beforeYesterday(), 4.0
                ),
                new Location(
                        mockedOnTimeUser, new Movie("Jumanji", 1, 4.0),
                        calendarHelper.tomorrow(), calendarHelper.today(), 4.0
                )
        );

        when(locationRepository.getPendingLocations())
                .thenReturn(mockedLocations);

        locationService.notifyDelays();

        verify(emailService, atLeastOnce())
                .notifyDelay(mockedLateUser1);

        verify(emailService, times(1))
                .notifyDelay(mockedLateUser2);

        verify(emailService, times(2))
                .notifyDelay(any());

        verify(emailService, never())
                .notifyDelay(mockedOnTimeUser);

        verifyNoMoreInteractions(emailService);
    }

}
