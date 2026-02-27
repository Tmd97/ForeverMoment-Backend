package com.forvmom.data.dao;

import com.forvmom.data.entities.Media;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MediaDaoImpl extends GenericDaoImpl<Media, Long> implements MediaDao {

    public MediaDaoImpl() {
        super(Media.class);
    }

    @Override
    public List<Media> findAllActive() {
        String jpql = "SELECT m FROM Media m WHERE m.isActive = true AND m.deleted = false ORDER BY m.createdOn DESC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        return query.getResultList();
    }

    @Override
    public List<Media> findByMediaType(String mediaType) {
        String jpql = "SELECT m FROM Media m WHERE m.mediaType = :mediaType AND m.deleted = false ORDER BY m.createdOn DESC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        query.setParameter("mediaType", mediaType);
        return query.getResultList();
    }

    @Override
    public Media findByFilePath(String filePath) {
        try {
            String jpql = "SELECT m FROM Media m WHERE m.filePath = :filePath AND m.deleted = false";
            TypedQuery<Media> query = em.createQuery(jpql, Media.class);
            query.setParameter("filePath", filePath);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Media> findUnusedMedia() {
        // Find media that are not linked to any experience via ExperienceMediaMapper
        String jpql = "SELECT m FROM Media m WHERE m.id NOT IN " +
                "(SELECT emm.media.id FROM ExperienceMediaMapper emm WHERE emm.deleted = false) " +
                "AND m.deleted = false " +
                "ORDER BY m.createdOn DESC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        return query.getResultList();
    }

    @Override
    public List<Media> findPopularMedia(int limit) {
        // Find most accessed media
        String jpql = "SELECT m FROM Media m WHERE m.deleted = false " +
                "ORDER BY m.accessCount DESC, m.lastAccessed DESC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public boolean existsByFilePath(String filePath) {
        String jpql = "SELECT COUNT(m) FROM Media m WHERE m.filePath = :filePath AND m.deleted = false";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("filePath", filePath);
        return query.getSingleResult() > 0;
    }

    @Override
    public String findGridFsIdByStorageFileName(String storageFileName) {
        try {
            String jpql = "SELECT m.filePath FROM Media m WHERE m.storageFileName = :storageFileName AND m.deleted = false";
            TypedQuery<String> query = em.createQuery(jpql, String.class);
            query.setParameter("storageFileName", storageFileName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find media uploaded after a specific date
     */
    public List<Media> findMediaUploadedAfter(java.util.Date date) {
        String jpql = "SELECT m FROM Media m WHERE m.createdOn > :date AND m.deleted = false ORDER BY m.createdOn DESC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    /**
     * Find media by version (for cache validation)
     */
    public Media findByVersion(Long version) {
        try {
            String jpql = "SELECT m FROM Media m WHERE m.version = :version AND m.deleted = false";
            TypedQuery<Media> query = em.createQuery(jpql, Media.class);
            query.setParameter("version", version);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    /**
     * Find media by original filename (partial match)
     */
    public List<Media> searchByFileName(String fileNamePattern) {
        String jpql = "SELECT m FROM Media m WHERE LOWER(m.fileName) LIKE LOWER(:pattern) AND m.deleted = false " +
                "ORDER BY m.createdOn DESC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        query.setParameter("pattern", "%" + fileNamePattern + "%");
        return query.getResultList();
    }

    /**
     * Get media statistics
     */
    public MediaStatistics getMediaStatistics() {
        String totalCountJpql = "SELECT COUNT(m) FROM Media m WHERE m.deleted = false";
        String activeCountJpql = "SELECT COUNT(m) FROM Media m WHERE m.isActive = true AND m.deleted = false";
        String totalSizeJpql = "SELECT SUM(m.fileSizeBytes) FROM Media m WHERE m.deleted = false";
        String imageCountJpql = "SELECT COUNT(m) FROM Media m WHERE m.mediaType = 'IMAGE' AND m.deleted = false";
        String videoCountJpql = "SELECT COUNT(m) FROM Media m WHERE m.mediaType = 'VIDEO' AND m.deleted = false";

        Long totalCount = em.createQuery(totalCountJpql, Long.class).getSingleResult();
        Long activeCount = em.createQuery(activeCountJpql, Long.class).getSingleResult();
        Long totalSize = em.createQuery(totalSizeJpql, Long.class).getSingleResult();
        Long imageCount = em.createQuery(imageCountJpql, Long.class).getSingleResult();
        Long videoCount = em.createQuery(videoCountJpql, Long.class).getSingleResult();

        return new MediaStatistics(totalCount, activeCount, totalSize, imageCount, videoCount);
    }

    /**
     * Batch update media active status
     */
    public int bulkUpdateActiveStatus(List<Long> ids, boolean isActive) {
        String jpql = "UPDATE Media m SET m.isActive = :isActive, m.updatedOn = CURRENT_TIMESTAMP " +
                "WHERE m.id IN :ids AND m.deleted = false";
        return em.createQuery(jpql)
                .setParameter("isActive", isActive)
                .setParameter("ids", ids)
                .executeUpdate();
    }

    /**
     * Find media that haven't been accessed since a given date
     */
    public List<Media> findUnusedSince(java.util.Date date) {
        String jpql = "SELECT m FROM Media m WHERE (m.lastAccessed IS NULL OR m.lastAccessed < :date) " +
                "AND m.deleted = false ORDER BY m.createdOn ASC";
        TypedQuery<Media> query = em.createQuery(jpql, Media.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    /**
     * Inner class for media statistics
     */
    public static class MediaStatistics {
        private final Long totalCount;
        private final Long activeCount;
        private final Long totalSizeBytes;
        private final Long imageCount;
        private final Long videoCount;

        public MediaStatistics(Long totalCount, Long activeCount, Long totalSizeBytes,
                Long imageCount, Long videoCount) {
            this.totalCount = totalCount;
            this.activeCount = activeCount;
            this.totalSizeBytes = totalSizeBytes;
            this.imageCount = imageCount;
            this.videoCount = videoCount;
        }

        // Getters
        public Long getTotalCount() {
            return totalCount;
        }

        public Long getActiveCount() {
            return activeCount;
        }

        public Long getTotalSizeBytes() {
            return totalSizeBytes;
        }

        public String getTotalSizeFormatted() {
            if (totalSizeBytes == null)
                return "0 B";
            if (totalSizeBytes < 1024)
                return totalSizeBytes + " B";
            if (totalSizeBytes < 1024 * 1024)
                return String.format("%.2f KB", totalSizeBytes / 1024.0);
            if (totalSizeBytes < 1024 * 1024 * 1024)
                return String.format("%.2f MB", totalSizeBytes / (1024.0 * 1024.0));
            return String.format("%.2f GB", totalSizeBytes / (1024.0 * 1024.0 * 1024.0));
        }

        public Long getImageCount() {
            return imageCount;
        }

        public Long getVideoCount() {
            return videoCount;
        }
    }
}