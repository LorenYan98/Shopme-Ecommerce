package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.hibernate.type.ListType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;



@Service
@Transactional
public class BrandService {
	public static final int ROOT_BRANDS_PER_PAGE = 10;
	@Autowired
	private BrandRepository brandRepo;
	
	public List<Brand> listAll(){
		return (List<Brand>) brandRepo.findAll();
	}

	public Brand save(Brand brand) {
		return brandRepo.save(brand);
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Brand brandByName = brandRepo.findByName(name);
		
		if(isCreatingNew) {
			if(brandByName != null) return "Duplicate";
		}else {
			if(brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
	
	public Brand getBrandInfo(Integer id) throws BrandNotFoundException {
		try {
			return brandRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoundException{
		Long countById = brandRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
		
		brandRepo.deleteById(id);
	}


	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		
		Sort sort = Sort.by(sortField);
		
		if(sortDir.equals("asc")) { 
			sort = sort.ascending();
		}else if(sortDir.equals("desc")){
			sort = sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum -1, ROOT_BRANDS_PER_PAGE, sort);

		if(keyword != null && !keyword.isEmpty()) {
			return brandRepo.findAll(keyword, pageable);
		}
		
		return brandRepo.findAll(pageable);
	}

	
	
	
}
