package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockitoSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {
	
	@MockBean
	private CategoryRepository repo;
	
	@InjectMocks
	private CategoryService service;
	
	@Test
	public void testCheckUnique() {
		Integer id = null;
		String name = "Computers";
		String alias = "ABC";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		String resultString = service.checkUnique(id, name, alias);
		
		assertThat(resultString).isEqualTo("Duplicated");
	}
}
