package com.forvmom.data.dao.auth;

import com.forvmom.data.dao.GenericDaoImpl;
import com.forvmom.data.entities.auth.AuthUserRole;
import com.forvmom.data.entities.auth.Role;
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
    public Optional<Role> findByNameIgnoreCase(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return Optional.empty();
        }
        TypedQuery<Role> query = em.createQuery(
                "SELECT r FROM Role r WHERE UPPER(r.name) = UPPER(:roleName)",
                Role.class
        );
        query.setParameter("roleName", roleName.trim());

        List<Role> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    @Override
    public boolean existsByNameIgnoreCase(String roleName) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Role r WHERE UPPER(r.name) = UPPER(:roleName)",
                Long.class
        );
        query.setParameter("roleName", roleName);

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

    @Override
    public List<Role> getAllActiveRoles() {
        return em.createQuery(
                "SELECT r FROM Role r " +
                        "WHERE r.isActive= true",
                Role.class
        ).getResultList();
    }

    @Override
    public List<Role> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        TypedQuery<Role> query = em.createQuery(
                "SELECT r FROM Role r WHERE r.id IN :ids",
                Role.class
        );
        query.setParameter("ids", ids);
        List<Role> roleList = query.getResultList();
        return roleList;
    }
}