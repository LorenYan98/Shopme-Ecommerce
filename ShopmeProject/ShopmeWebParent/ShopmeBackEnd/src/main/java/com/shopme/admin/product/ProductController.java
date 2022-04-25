package com.shopme.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired CategoryService categoryService;
	
	@GetMapping("/products")
	public String listPage(Model model) {
		List<Product> listProduct = productService.listAll();
		model.addAttribute("listProducts",listProduct);
		return "products/products"; 
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Product");
		
		return "products/product_form"; 
		
	}
	
	@PostMapping("/products/save")
	public String saveProducts(Product product) {
		return "redirect:/products";
	}
}
