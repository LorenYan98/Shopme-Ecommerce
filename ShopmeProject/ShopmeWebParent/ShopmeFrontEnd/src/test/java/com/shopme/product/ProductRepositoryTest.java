package com.shopme.product;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.shopme.category.CategoryRepository;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest

@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {
	
	@Autowired ProductRepository repository;
	
	@Test
	public void testFindByAlias() {
		String alias = "something";
		
		Product product = repository.findByAlias(alias);
		
		assertThat(product).isNull();
	}

}
