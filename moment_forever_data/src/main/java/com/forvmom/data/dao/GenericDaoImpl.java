package com.forvmom.data.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public abstract class GenericDaoImpl<T, ID> implements GenericDao<T, ID> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;

    public GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    /**
     * Persist the entity and immediately flush to the DB.
     * Use when the generated ID is needed within the same transaction
     * before another entity references it via FK.
     * Mirrors Spring Data JPA's {@code saveAndFlush()}.
     */
    public T saveAndFlush(T entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    @Override
    public T update(T entity) {
        em.merge(entity);
        return entity;
    }

    @Override
    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public T findById(ID id) {
        return em.find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getName() + " e", entityClass)
                .getResultList();
    }

    /**
     * Returns an EntityManager proxy — no SELECT is executed.
     * Use when you only need to set a FK reference without loading the entity.
     */
    public <E> E getReference(Class<E> clazz, Object id) {
        return em.getReference(clazz, id);
    }

    /**
     * Exposes the EntityManager for raw JPQL UPDATE/DELETE operations
     * (e.g., atomic capacity increment in booking flow).
     */
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Convenience: soft-delete (or hard-delete) by ID without loading the entity
     * first.
     */
    public void deleteById(ID id) {
        T ref = em.find(entityClass, id);
        if (ref != null)
            delete(ref);
    }
}
