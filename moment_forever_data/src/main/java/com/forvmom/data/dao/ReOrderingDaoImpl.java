package com.forvmom.data.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ReOrderingDaoImpl implements ReOrderingDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void moveUp(Long entityId, Long currentPosition, Long newOrder, Class entityClass) {
        // First, get current position

        if (currentPosition == null || currentPosition <= newOrder) {
            throw new IllegalArgumentException(
                    "Invalid moveUp operation. Current: " + currentPosition + ", New: " + newOrder
            );
        }

        // Shift items between newOrder and currentOrder-1 DOWN by 1
        // This creates a gap at newOrder
        String shiftJpql =
                "UPDATE " + entityClass.getName() + " e " +
                        "SET e.displayOrder = e.displayOrder + 1 " +
                        "WHERE e.displayOrder >= :newOrder " +
                        "AND e.displayOrder < :currentOrder";

        em.createQuery(shiftJpql)
                .setParameter("newOrder", newOrder)
                .setParameter("currentOrder", currentPosition)
                .executeUpdate();

        // Update the moved entity to the new position
        String updateJpql =
                "UPDATE " + entityClass.getName() + " e " +
                        "SET e.displayOrder = :newOrder " +
                        "WHERE e.id = :entityId";

        em.createQuery(updateJpql)
                .setParameter("newOrder", newOrder)
                .setParameter("entityId", entityId)
                .executeUpdate();

        em.flush();
        em.clear();
    }

    @Override
    @Transactional
    public void moveDown(Long entityId,Long currentPosition, Long newOrder, Class entityClass) {

        if (currentPosition == null || currentPosition >= newOrder) {
            throw new IllegalArgumentException(
                    "Invalid moveDown operation. Current: " + currentPosition + ", New: " + newOrder
            );
        }

        // Shift items between currentOrder+1 and newOrder UP by 1
        // This creates a gap at newOrder
        String shiftJpql =
                "UPDATE " + entityClass.getName() + " e " +
                        "SET e.displayOrder = e.displayOrder - 1 " +
                        "WHERE e.displayOrder > :currentOrder " +
                        "AND e.displayOrder <= :newOrder";

        em.createQuery(shiftJpql)
                .setParameter("currentOrder", currentPosition)
                .setParameter("newOrder", newOrder)
                .executeUpdate();

        // Update the moved entity to the new position
        String updateJpql =
                "UPDATE " + entityClass.getName() + " e " +
                        "SET e.displayOrder = :newOrder " +
                        "WHERE e.id = :entityId";

        em.createQuery(updateJpql)
                .setParameter("newOrder", newOrder)
                .setParameter("entityId", entityId)
                .executeUpdate();

        em.flush();
        em.clear();
    }

    @Override
    public Long getCurrentPosition(Long entityId, Class entityClass) {
        String jpql =
                "SELECT e.displayOrder FROM " + entityClass.getName() + " e " +
                        "WHERE e.id = :entityId";

        try {
            Query query = em.createQuery(jpql);
            query.setParameter("entityId", entityId);
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void moveToTop(Long entityId, Long currentPosition, Class entityType) {
        // Move to position 1
        moveUp(entityId, currentPosition, 1L, entityType);
    }

    @Override
    @Transactional
    public void moveToBottom(Long entityId, Long currentPosition, Class entityType) {
        Long maxOrder = getMaxOrder(entityType);
        if (maxOrder > 0) {
            // Get current position
            if (currentPosition != null && currentPosition < maxOrder) {
                moveDown(entityId, currentPosition, maxOrder, entityType);
            }
        }
    }

    @Override
    public Long getMaxOrder(Class entityClass) {
        String jpql = "SELECT COALESCE(MAX(e.displayOrder), 0) FROM " + entityClass.getName() + " e";
        try {
            Long maxOrder = em.createQuery(jpql, Long.class).getSingleResult();
            return maxOrder != null ? maxOrder : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    @Transactional
    public void normalizeOrders(Class entityClass) {
        // Get all entities in order
        String selectJpql =
                "SELECT e FROM " + entityClass.getName() + " e " +
                        "ORDER BY e.displayOrder ASC, e.id ASC";

        Query selectQuery = em.createQuery(selectJpql);
        var entities = selectQuery.getResultList();

        // Update display orders sequentially starting from 1
        for (int i = 0; i < entities.size(); i++) {
            Object entity = entities.get(i);

            // Use reflection to set displayOrder
            try {
                var field = entity.getClass().getDeclaredField("displayOrder");
                field.setAccessible(true);
                field.set(entity, (long) (i + 1));

                em.merge(entity);
            } catch (Exception e) {
                throw new RuntimeException("Failed to normalize orders", e);
            }
        }

        em.flush();
        em.clear();
    }
}