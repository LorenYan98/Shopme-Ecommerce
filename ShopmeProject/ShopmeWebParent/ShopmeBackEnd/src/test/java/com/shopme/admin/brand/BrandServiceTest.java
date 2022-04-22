package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockitoSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {
	
	@MockBean
	private BrandRepository repo;
	
	@InjectMocks
	private BrandService service;
	
	@Test
	public void testCheckUnique() {
		Integer id = null;
		String name = "AMD";

		Brand brand = new Brand(name);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		
		String resultString = service.checkUnique(id, name);
		assertThat(resultString).isEqualTo("OK");
	}
	
	@Test
	public void testCheckUniqueDuplicate() {
		Integer id = 1;
		String name = "Canon";

		Brand brand = new Brand(name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String resultString = service.checkUnique(2, "Canon");
		assertThat(resultString).isEqualTo("Duplicate");
	}
	
	@Test
	public void testCheckUniqueOK() {
		Integer id = 1;
		String name = "Acer";

		Brand brand = new Brand(id,name);
		
		Mockito.when(repo.findByName(name)).thenReturn(brand);
		
		String resultString = service.checkUnique(id, name);
		assertThat(resultString).isEqualTo("Duplicate");
	}
}
