package com.forvmom.data.dao;

import com.forvmom.data.entities.Addon;

import java.util.List;

public interface AddonDao extends GenericDao<Addon, Long> {
    List<Addon> findAll();

    boolean existsById(Long id);
}
