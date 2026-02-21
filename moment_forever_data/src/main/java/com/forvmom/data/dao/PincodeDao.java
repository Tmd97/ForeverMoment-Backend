package com.forvmom.data.dao;

import com.forvmom.data.entities.Pincode;

import java.util.List;

public interface PincodeDao extends GenericDao<Pincode, Long> {

    List<Pincode> findByLocationId(Long locationId);

    List<Pincode> findByLocationIdWithLocation(Long locationId);

    Pincode findByPincodeCode(String pincodeCode);

    Pincode findByIdWithLocation(Long id);

    boolean existsByPincodeCode(String pincodeCode);

    boolean existsByPincodeCodeAndLocationId(String pincodeCode, Long locationId);
}
