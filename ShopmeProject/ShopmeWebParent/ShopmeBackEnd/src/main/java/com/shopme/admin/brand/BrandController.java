package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	@Autowired 
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String listPage(Model model) {
		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("listBrands",listBrands);
		
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword
			) {
		if(sortDir == null || sortDir.isEmpty()) sortDir = "asc";
	
		Page<Brand> listBrands = brandService.listByPage(pageNum,sortField, sortDir, keyword);
		
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if(endCount > listBrands.getTotalPages()) {
			endCount = listBrands.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc")? "desc" : "asc";
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField","name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", listBrands.getTotalPages());
		model.addAttribute("totalItem", listBrands.getTotalElements());
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listcaCategories = categoryService.listCategoriesUsedInForm();
		model.addAttribute("listCategories",listcaCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");
		
		return "brands/brand_form";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(Model model,
			@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes) {
		try {
			List<Category> listcaCategories = categoryService.listCategoriesUsedInForm();
			Brand currentBrand = brandService.getBrandInfo(id);
			model.addAttribute("listCategories",listcaCategories);
			model.addAttribute("brand",currentBrand);
			model.addAttribute("pageTitle", "Edit Current Brand");
			
			return "brands/brand_form";
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/brands";
		}
		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(Model model,
			@PathVariable(name = "id") Integer id, 
			RedirectAttributes redirectAttributes) {
		try {
			brandService.delete(id);
			String categoryDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(categoryDir);
			redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully");
		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
		}
		return "redirect:/brands";
	}
	
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			
			Brand savedBrand = brandService.save(brand);
			String uploadDir = "../brand-logos/" + savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			brandService.save(brand);
		}
		
		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		return "redirect:/brands";
	}
	
	
}
