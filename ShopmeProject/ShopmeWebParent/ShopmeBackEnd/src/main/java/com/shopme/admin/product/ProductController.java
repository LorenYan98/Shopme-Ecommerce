package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		
		return "products/product_form"; 
		
	}
	
	@PostMapping("/products/save")
	public String saveProducts(Product product,
			@RequestParam("fileImage") MultipartFile mainMultipartFile,
			@RequestParam("extraImage") MultipartFile[] extraMultipartFiles,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			RedirectAttributes redirectAttributes) throws IOException {
			
		setMainImageName(mainMultipartFile, product);
		setExtraImageName(extraMultipartFiles, product);
		setProductDetails(detailNames, detailValues, product);
		Product savedProduct = productService.save(product);
		
		saveUploadedImages(mainMultipartFile, extraMultipartFiles, savedProduct);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}
	
	private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			
			if(!name.isEmpty() && !value.isEmpty()) {
				product.addExtraDetails(name, value);
			}
		}
		
	}

	private void saveUploadedImages(MultipartFile mainMultipartFile, 
			MultipartFile[] extraMultipartFiles,
			Product savedProduct) throws IOException {
		
		// Save main image
		if(!mainMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainMultipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainMultipartFile);
		}
		
		//Save extra images
		if(extraMultipartFiles.length > 0) {
			for(MultipartFile multipartFile : extraMultipartFiles) {
				String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
				if(multipartFile.isEmpty()) {
					continue;
				}
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
		
	}

	private void setExtraImageName(MultipartFile[] extraMultipartFiles, Product product) {
		if(extraMultipartFiles.length > 0) {
			for(MultipartFile multipartFile : extraMultipartFiles) {
				if(!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);
				}
			}
		}
	}

	private void setMainImageName(MultipartFile mainMultipartFile, Product product) {
		if(!mainMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainMultipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
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
