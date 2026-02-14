package com.forvmom.data.dao;

public interface ReOrderingDao {
    void moveUp(Long entityId, Long currentPosition, Long newOrder, Class entityClass);
    void moveDown(Long entityId,Long currentPosition, Long newOrder, Class entityClass);
    void moveToTop(Long entityId,Long currentPosition, Class entityType);
    void moveToBottom(Long entityId,Long currentPosition, Class entityType);
    Long getCurrentPosition(Long entityId, Class entityClass);
    Long getMaxOrder(Class entityClass);
    void normalizeOrders(Class entityClass);
}