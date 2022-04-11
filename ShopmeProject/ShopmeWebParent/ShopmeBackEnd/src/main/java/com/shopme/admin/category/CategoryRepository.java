package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories();
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
//	@Query("SELECT c FROM Category c WHERE c.name = name")
//	public Category getCategoryByName(@Param("name") String name);
//	
//	@Query("SELECT c FROM Category c WHERE c.alias = alias")
//	public Category getCategoryByAlias(@Param("alias") String alias);
}
