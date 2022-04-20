package com.study.junit.fake;

import com.study.junit.entity.Location;
import com.study.junit.repository.LocationRepository;

import java.util.List;

public class FakeLocationRepository implements LocationRepository {
    @Override
    public void save(Location location) {
    }

    @Override
    public List<Location> getPendingLocations() {
        return List.of();
    }
}
