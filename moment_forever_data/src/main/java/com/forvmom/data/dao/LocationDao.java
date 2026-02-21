package com.forvmom.data.dao;

import com.forvmom.data.entities.Location;

import java.util.List;

public interface LocationDao extends GenericDao<Location, Long> {

    boolean existsByName(String name);

    List<Location> findByCity(String city);

    List<Location> findByState(String state);

    Location findByIdWithPincodes(Long id);

    List<Location> findAllActive();
}
