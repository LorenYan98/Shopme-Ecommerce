package com.shopme.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

/*
 * After creating our interface and extend CrudRepository. We need not to implement our interface, 
 * its implementation will be created automatically at runtime. Find some of CrudRepository methods.
 * After using CrudR, we will be able to use:
 * 
 * <S extends T> S save(S entity): Saves and updates the current entity and returns that entity.
 * 
 * Optional<T> findById(ID primaryKey): Returns the entity for the given id.
 * 
 * Iterable<T> findAll(): Returns all entities.
 * 
 * long count(): Returns the count.
 * 
 * void delete(T entity): Deletes the given entity.
 * 
 * boolean existsById(ID primaryKey): Checks if the entity for the given id exists or not.
 */
public interface UserRepository extends CrudRepository<User, Integer>{
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
}
