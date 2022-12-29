package org.example.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<E,ID> {
    E insert(E e);

    E update(E e);

    Optional<E> getById(ID id);

    List<E> getAll();

    void delete(ID id);

    boolean existById(ID id);
}
