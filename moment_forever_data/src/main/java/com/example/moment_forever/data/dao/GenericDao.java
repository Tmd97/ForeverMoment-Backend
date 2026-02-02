package com.example.moment_forever.data.dao;

import java.util.List;

public interface GenericDao<T, ID> {

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    T findById(ID id);

    List<T> findAll();
}
