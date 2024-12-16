package service;

import java.util.List;

public interface BaseService<T, ID> {
    T create(T entity);
    T findById(ID id);
    List<T> findAll();
    T update(T entity);
    void delete(ID id);
    boolean exists(ID id);
} 