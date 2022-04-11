package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.poi.poifs.property.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	@Autowired
	CategoryRepository repo;
	
	public List<Category> listAll(){
		List<Category> rootCategories = repo.findRootCategories();
		return listHierarchicalCategories(rootCategories);
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories){
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for(Category rootCategory: rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			Set<Category> children = rootCategory.getChildern();
			for(Category subCategory: children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));		
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}	
		return hierarchicalCategories;
	}
	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
			Category parent, int subLevel) {
		Set<Category> children = parent.getChildern();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i< subLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));	
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, subLevel + 1);
		}
	}
	
	public Category save(Category category) {
		return repo.save(category);
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesUsedInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB = repo.findAll();
		
		for(Category category: categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				
				Set<Category> children = category.getChildern();
				
				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					
					listChildren(categoriesUsedInForm, subCategory, 1);
				}
			}
		}
		
		return categoriesUsedInForm;
	}
	
	private void listChildren(List<Category> categoriesUsedInForm, Category parent, int sublevel) {
		int newSubLevel = sublevel + 1;
		Set<Category> children = parent.getChildern();
		
		for(Category subCategory : children) {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name  += "--";
			}
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name + subCategory.getName()));
			listChildren(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}
	
	public Category getCategoryInfo(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		}catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any category with ID "+ id);
		}
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = repo.findByName(name);
		
		if(isCreatingNew) {
			if(categoryByName != null) {
				return "DuplicatedName";
			}else {
				Category categoryByAlias = repo.findByAlias(alias);
				if(categoryByAlias != null) {
					return "DuplicatedAlias";
				}
			}
		}else {
			if(categoryByName != null && categoryByName.getId() != id) {
				return "DuplicatedName";
			}
			Category categoryByAlias = repo.findByAlias(alias);
			if(categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicatedAlias";
			}
		}
		
		return "OK";
	}
	
	public void updateCategoryEnableStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
}
