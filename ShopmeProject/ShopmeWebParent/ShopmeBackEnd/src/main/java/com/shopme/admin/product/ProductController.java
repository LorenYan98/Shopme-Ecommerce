package com.shopme.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Product;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@GetMapping("/products")
	public String listPage(Model model) {
		List<Product> listProduct = productService.listAll();
		model.addAttribute("listProducts",listProduct);
		return "products/products"; 
	}
}
