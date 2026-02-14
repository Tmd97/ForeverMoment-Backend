package com.forvmom.data.dao;

import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.data.entities.ApplicationUser;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ApplicationUserDaoImpl extends GenericDaoImpl<ApplicationUser, Long> implements ApplicationUserDao {

    public ApplicationUserDaoImpl() {
        super(ApplicationUser.class);
    }

    @Override
    public Optional<ApplicationUser> findByEmailIgnoreCase(String email) {
        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT au FROM ApplicationUser au WHERE LOWER(au.email) = LOWER(:email)",
                ApplicationUser.class);
        query.setParameter("email", email);

        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ApplicationUser> findByAuthUserId(Long authUserId) {
        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT au FROM ApplicationUser au WHERE au.authUser.id = :authUserId",
                ApplicationUser.class);
        query.setParameter("authUserId", authUserId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            throw new ResourceNotFoundException("ApplicationUser with authUserId " + authUserId + " not found");
        }
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(au) FROM ApplicationUser au WHERE LOWER(au.email) = LOWER(:email)",
                Long.class);
        query.setParameter("email", email);

        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(au) FROM ApplicationUser au WHERE au.phoneNumber = :phoneNumber",
                Long.class);
        query.setParameter("phoneNumber", phoneNumber.trim());

        return query.getSingleResult() > 0;
    }

    @Override
    public List<ApplicationUser> findByPreferredCityIgnoreCase(String city) {
        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT au FROM ApplicationUser au WHERE LOWER(au.preferredCity) = LOWER(:city)",
                ApplicationUser.class);
        query.setParameter("city", city);

        return query.getResultList();
    }

    @Override
    public List<ApplicationUser> findByCreatedAtAfter(LocalDateTime date) {
        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT au FROM ApplicationUser au WHERE au.createdAt > :date",
                ApplicationUser.class);
        query.setParameter("date", date);

        return query.getResultList();
    }

    @Override
    public List<ApplicationUser> searchByNameOrEmail(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }

        String searchPattern = "%" + searchTerm.toLowerCase() + "%";

        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT au FROM ApplicationUser au WHERE " +
                        "LOWER(au.fullName) LIKE :pattern OR " +
                        "LOWER(au.email) LIKE :pattern",
                ApplicationUser.class);
        query.setParameter("pattern", searchPattern);

        return query.getResultList();
    }

    @Override
    public void deleteByAppUserId(Long authUserId) {
        em.createQuery("DELETE FROM ApplicationUser a WHERE a.authUser.id = :authUserId")
                .setParameter("authUserId", authUserId)
                .executeUpdate();
    }

    @Override
    public List<ApplicationUser> findAllWithAuthAndRoles() {
        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT DISTINCT au FROM ApplicationUser au " +
                        "LEFT JOIN FETCH au.authUser auth " +
                        "LEFT JOIN FETCH auth.userRoles ur " +
                        "LEFT JOIN FETCH ur.role",
                ApplicationUser.class);
        return query.getResultList();
    }

    @Override
    public Optional<ApplicationUser> findByIdWithAuthAndRoles(Long id) {
        TypedQuery<ApplicationUser> query = em.createQuery(
                "SELECT DISTINCT au FROM ApplicationUser au " +
                        "LEFT JOIN FETCH au.authUser auth " +
                        "LEFT JOIN FETCH auth.userRoles ur " +
                        "LEFT JOIN FETCH ur.role " +
                        "WHERE au.id = :id",
                ApplicationUser.class);
        query.setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}