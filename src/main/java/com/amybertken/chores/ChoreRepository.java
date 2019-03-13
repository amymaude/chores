package com.amybertken.chores;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoreRepository extends CrudRepository<Chore, Long> {
}
