package com.shopme.admin.brand;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.type.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Service
@Transactional
public class BrandService {
	@Autowired
	private BrandRepository brandRepo;
	
	public List<Brand> listAll(){
		return (List<Brand>) brandRepo.findAll();
	}

	public Brand save(Brand brand) {
		return brandRepo.save(brand);
	}

	public Brand getBrandInfo(Integer id) {
		return brandRepo.findById(id).get();
	}
	

	
	
	
}
