package com.forvmom.data.dao;

import com.forvmom.data.entities.Pincode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PincodeDaoImpl extends GenericDaoImpl<Pincode, Long> implements PincodeDao {

    public PincodeDaoImpl() {
        super(Pincode.class);
    }

    @Override
    public List<Pincode> findByLocationId(Long locationId) {
        return em.createQuery(
                "FROM Pincode p WHERE p.location.id = :locationId ORDER BY p.pincodeCode ASC",
                Pincode.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    @Override
    public List<Pincode> findByLocationIdWithLocation(Long locationId) {
        return em.createQuery(
                "SELECT DISTINCT p FROM Pincode p LEFT JOIN FETCH p.location WHERE p.location.id = :locationId ORDER BY p.pincodeCode ASC",
                Pincode.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    @Override
    public Pincode findByPincodeCode(String pincodeCode) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Pincode p LEFT JOIN FETCH p.location WHERE p.pincodeCode = :code",
                    Pincode.class)
                    .setParameter("code", pincodeCode)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Pincode findByIdWithLocation(Long id) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Pincode p LEFT JOIN FETCH p.location WHERE p.id = :id",
                    Pincode.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean existsByPincodeCode(String pincodeCode) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Pincode p WHERE p.pincodeCode = :code", Long.class)
                .setParameter("code", pincodeCode)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByPincodeCodeAndLocationId(String pincodeCode, Long locationId) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Pincode p WHERE p.pincodeCode = :code AND p.location.id = :locationId",
                Long.class)
                .setParameter("code", pincodeCode)
                .setParameter("locationId", locationId)
                .getSingleResult();
        return count > 0;
    }
}
