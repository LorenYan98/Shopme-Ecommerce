package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.functors.FalsePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import com.shopme.common.exception.ProductNotFoundException;



@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired 
	private CategoryService categoryService;
	
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null,0); 
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		Integer numberOfExistingImages = 0;
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingImages", numberOfExistingImages);
		
		return "products/product_form"; 
		
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId
			) {
		if(sortDir == null || sortDir.isEmpty()) sortDir = "asc";
		Page<Product> listProducts = productService.listByPage(pageNum,sortField, sortDir, keyword, categoryId);
		List<Category> listcCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNum - 1) * ProductService.ROOT_PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.ROOT_PRODUCTS_PER_PAGE - 1;
		if(endCount > listProducts.getTotalElements()) {
			endCount = listProducts.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc")? "desc" : "asc";
		
		if(categoryId !=null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listCategories", listcCategories);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", listProducts.getTotalPages());
		model.addAttribute("totalItem", listProducts.getTotalElements());
		return "products/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingImages = product.getImages().size();
			
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("product",product); 
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingImages", numberOfExistingImages);
			return "products/product_form";
			
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Product product = productService.get(id);

			model.addAttribute("product",product); 

			return "products/product_detail_modal";
			
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}
	
	
	
	@PostMapping("/products/save")
	public String saveProducts(Product product,
			@RequestParam(value = "fileImage",required = false) MultipartFile mainMultipartFile,
			@RequestParam(value = "extraImage",required = false) MultipartFile[] extraMultipartFiles,
			@RequestParam(name = "detailIDs",required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			RedirectAttributes redirectAttributes) throws IOException {
			
		if(loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
			return "redirect:/products";
		}
		ProductSaveHelper.setMainImageName(mainMultipartFile, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageName(extraMultipartFiles, product);
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);
		Product savedProduct = productService.save(product);
		
		ProductSaveHelper.saveUploadedImages(mainMultipartFile, extraMultipartFiles, savedProduct);
		
		ProductSaveHelper.deleteExtraImageWereRemoved(product);
		
		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}
	
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnableStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		productService.updateEnableStatus(id, enabled);
		
		String status =	enabled ? "enabled" : "disabled";
		String message = "The product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String delete(@PathVariable("id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			String productExtraImageDir = "../product-images/" + id + "/extras";
			String productImageDir = "../product-images/" + id;
			
			FileUploadUtil.removeDir(productExtraImageDir);
			FileUploadUtil.removeDir(productImageDir);
			
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}
}
