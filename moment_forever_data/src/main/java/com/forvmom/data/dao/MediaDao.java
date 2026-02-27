package com.forvmom.data.dao;

import com.forvmom.data.entities.Media;

import java.util.List;

public interface MediaDao extends GenericDao<Media, Long> {
    List<Media> findAllActive();

    List<Media> findByMediaType(String mediaType);

    Media findByFilePath(String filePath);

    List<Media> findUnusedMedia();

    List<Media> findPopularMedia(int limit);

    boolean existsByFilePath(String filePath);

    String findGridFsIdByStorageFileName(String storageFileName);
}