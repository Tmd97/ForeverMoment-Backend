package com.forvmom.data.dao;

import com.forvmom.data.entities.Location;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class LocationDaoImpl extends GenericDaoImpl<Location, Long> implements LocationDao {

    public LocationDaoImpl() {
        super(Location.class);
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                "SELECT COUNT(l) FROM Location l WHERE l.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Location> findByCity(String city) {
        return em.createQuery(
                "FROM Location l WHERE l.city = :city ORDER BY l.name ASC", Location.class)
                .setParameter("city", city)
                .getResultList();
    }

    @Override
    public List<Location> findByState(String state) {
        return em.createQuery(
                "FROM Location l WHERE l.state = :state ORDER BY l.name ASC", Location.class)
                .setParameter("state", state)
                .getResultList();
    }

    @Override
    public Location findByIdWithPincodes(Long id) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT l FROM Location l LEFT JOIN FETCH l.pincodes WHERE l.id = :id",
                    Location.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Location> findAllActive() {
        return em.createQuery(
                "FROM Location l WHERE l.isActive = true ORDER BY l.name ASC", Location.class)
                .getResultList();
    }
}
