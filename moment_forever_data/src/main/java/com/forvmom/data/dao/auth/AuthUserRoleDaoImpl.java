package com.forvmom.data.dao.auth;

import com.forvmom.data.dao.GenericDaoImpl;
import com.forvmom.data.entities.auth.AuthUserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AuthUserRoleDaoImpl extends GenericDaoImpl<AuthUserRole, Long> implements AuthUserRoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AuthUserRoleDaoImpl() {
        super(AuthUserRole.class);
    }

    @Override
    public boolean existsByAuthUserIdAndRoleId(Long authUserId, Long roleId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(aur) FROM AuthUserRole aur " +
                        "WHERE aur.authUser.id = :authUserId AND aur.role.id = :roleId",
                Long.class
        );
        query.setParameter("authUserId", authUserId);
        query.setParameter("roleId", roleId);

        return query.getSingleResult() > 0;
    }

    @Override
    public List<AuthUserRole> findByAuthUserId(Long authUserId) {
        return entityManager.createQuery(
                        "SELECT aur FROM AuthUserRole aur " +
                                "WHERE aur.authUser.id = :authUserId",
                        AuthUserRole.class
                )
                .setParameter("authUserId", authUserId)
                .getResultList();
    }

    @Override
    public List<AuthUserRole> findByRoleId(Long roleId) {
        return entityManager.createQuery(
                        "SELECT aur FROM AuthUserRole aur " +
                                "WHERE aur.role.id = :roleId",
                        AuthUserRole.class
                )
                .setParameter("roleId", roleId)
                .getResultList();
    }

    @Override
    public void deleteByAuthUserIdAndRoleId(Long authUserId, Long roleId) {
        entityManager.createQuery(
                        "DELETE FROM AuthUserRole aur " +
                                "WHERE aur.authUser.id = :authUserId AND aur.role.id = :roleId"
                )
                .setParameter("authUserId", authUserId)
                .setParameter("roleId", roleId)
                .executeUpdate();
    }
}