package com.shopme.admin.product;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {
	@Autowired
	private ProductRepository productRepo;
	
	public List<Product> listAll(){
		return (List<Product>) productRepo.findAll(); 
	}
}
