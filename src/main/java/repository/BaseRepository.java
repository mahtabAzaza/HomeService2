package repository;

import entity.Order;

import java.util.List;

public interface BaseRepository <T, Long> {
    void save (T entity);
    void update (T entity);
    void delete (T entity);
    T findById(Long id);
    List<T> findAll(int pageNumber, int pageSize);
    List<T> findAll();

}
