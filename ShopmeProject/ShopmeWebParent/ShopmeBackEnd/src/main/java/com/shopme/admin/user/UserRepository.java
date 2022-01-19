package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
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
public interface UserRepository extends PagingAndSortingRepository<User, Integer>{
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	public Long countById(Integer id);
	/* Using Like:   select ... like :username
	 * StartingWith: select ... like :username%
	 * EndingWith:   select ... like %:username
	 * Containing:   select ... like %:username%
	  */
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	//Set User u 中的u.enabled == the second parameter (enabled),and u.id == the first parameter (id)
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
}
