package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.Set;

import org.apache.commons.math3.geometry.spherical.twod.SubCircle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Comfputers");
		Category savedCategory  = repo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(1);
		Category subCategory = new Category("Desktops", parent);
		Category saveCategory = repo.save(subCategory);
		
		assertThat(saveCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildern();
		for(Category c: children) {
			System.out.println(c.getName());
		}
		
		assertThat(children.size() > 0);
	}
	
	@Test
	public void testPrintCategories() {
		Iterable<Category> categoriesIterable = repo.findAll();
		for(Category subCategory: categoriesIterable) {
			if(subCategory.getParent() == null) {
				System.out.println(subCategory.getName());
				
				Set<Category> children = subCategory.getChildern();
				for(Category c :children){
					System.out.println("-- " + c.getName());
				}
			}
		}
	}
}
