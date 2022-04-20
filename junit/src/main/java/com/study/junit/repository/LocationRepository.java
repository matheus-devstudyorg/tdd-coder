package com.study.junit.repository;

import com.study.junit.entity.Location;

import java.util.List;

public interface LocationRepository {
    void save(Location location);
    List<Location> getPendingLocations();
}
