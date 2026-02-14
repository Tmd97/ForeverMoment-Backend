package com.forvmom.core.services;

import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.data.dao.ReOrderingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReorderingService {

    @Autowired
    private ReOrderingDao reOrderingDao;

    public void reorderItems(Long entityId, Long newPosition, Class entityType) {
        // First, get the current position of the entity
        Long currentPosition = reOrderingDao.getCurrentPosition(entityId, entityType);

        if (currentPosition == null) {
            throw new ResourceNotFoundException("Entity or position is missing with id: " + entityId);
        }

        if (currentPosition.equals(newPosition)) {
            return; // Already at the desired position
        }

        // Get max position for validation
        Long maxPosition = reOrderingDao.getMaxOrder(entityType);
        if (newPosition < 1 || newPosition > maxPosition) {
            throw new IllegalArgumentException(
                    "New position " + newPosition + " is invalid. Must be between 1 and " + maxPosition
            );
        }

        // Determine direction and move
        if (newPosition > currentPosition) {
            reOrderingDao.moveDown(entityId, currentPosition, newPosition, entityType);
        } else {
            reOrderingDao.moveUp(entityId,currentPosition, newPosition, entityType);
        }
    }

    public Long getMaxOrder(Class entityType) {
        return reOrderingDao.getMaxOrder(entityType);
    }

    public Long getCurrentPosition(Long entityId, Class entityType) {
        return reOrderingDao.getCurrentPosition(entityId, entityType);
    }
}