package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import com.shopme.common.exception.CategoryNotFoundException;





@Controller
public class CategoryController {
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, null, model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			Model model) {
		if(sortDir == null || sortDir.isEmpty()) sortDir = "asc";
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> categories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if(endCount > pageInfo.getTotalElement()) {
			endCount = pageInfo.getTotalElement();
		}
		String reverseSortDir = sortDir.equals("asc")? "desc" : "asc";
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listCategories", categories);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField","name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItem", pageInfo.getTotalElement());
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
		for(int curIndex = 0; curIndex<listcCategories.size(); curIndex++) {
			if(listcCategories.get(curIndex).getId() ==currentCategory.getId()) {
				listcCategories.remove(curIndex);
				break;
			}
		}
		
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
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted successfully");
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
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
	
	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Category> listCategories = service.listCategoriesUsedInForm();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, response);
	}
	
}
