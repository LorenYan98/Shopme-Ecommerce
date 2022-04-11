package com.shopme.admin.category;

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
import com.shopme.common.entity.Category;





@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		List<Category> categories = service.listAll();
		model.addAttribute("listCategories", categories);
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listcCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create New Category");
		model.addAttribute("listCategories", listcCategories);
		return "categories/category_form";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(Model model, 
			@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) throws CategoryNotFoundException {
		List<Category> listcCategories = service.listCategoriesUsedInForm();
		Category currentCategory = service.getCategoryInfo(id);
		
		
		model.addAttribute("category", currentCategory);
		model.addAttribute("pageTitle", "Edit Current Category");
		model.addAttribute("listCategories", listcCategories);
		
		return "categories/category_form";
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnableStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		service.updateCategoryEnableStatus(id, enabled);
		System.out.println("Hello follow");
		String status =	enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/categories";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		
		if(!multipartFile.isEmpty()) {
		
			//Create custom fileName
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			
			Category savedCategory = service.save(category);
			String uploadDir = "../categories-images/" + savedCategory.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			service.save(category);
		}
		
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}
}
