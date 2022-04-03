package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
public class CategoryService {
	@Autowired
	CategoryRepository repo;
	
	public List<Category> listAll(){
		return (List<Category>) repo.findAll();
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesUsedInForm = new ArrayList<>();
		
		Iterable<Category> categoriesInDB = repo.findAll();
		
		for(Category category: categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(new Category(category.getName()));
				
				Set<Category> children = category.getChildern();
				
				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(new Category(name));
					
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
			categoriesUsedInForm.add(new Category(name + subCategory.getName()));
			listChildren(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}
	
}
