package com.example.moment_forever.data.dao.auth;

import com.example.moment_forever.data.dao.GenericDaoImpl;
import com.example.moment_forever.data.entities.auth.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RoleDaoImpl extends GenericDaoImpl<Role, Long> implements RoleDao {

    public RoleDaoImpl() {
        super(Role.class);
    }

    @Override
    public Optional<Role> findByNameIgnoreCase(String name) {
        if(name==null || name.isEmpty()){
            return Optional.empty();
        }
        TypedQuery<Role> query = em.createQuery(
                "SELECT r FROM Role r WHERE UPPER(r.name) = UPPER(:name)",
                Role.class
        );
        query.setParameter("name", name.trim());

        List<Role> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Role r WHERE UPPER(r.name) = UPPER(:name)",
                Long.class
        );
        query.setParameter("name", name);

        return query.getSingleResult() > 0;
    }

    @Override
    public List<Role> findByActiveTrue() {
        return em.createQuery(
                "SELECT r FROM Role r WHERE r.active = true",
                Role.class
        ).getResultList();
    }

    @Override
    public List<Role> findBySystemRoleTrue() {
        return em.createQuery(
                "SELECT r FROM Role r WHERE r.systemRole = true",
                Role.class
        ).getResultList();
    }
}