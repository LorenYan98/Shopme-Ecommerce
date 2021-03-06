package com.shopme.common.entity;

import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/*
	 * Unique in @Column is used only if you let your JPA provider create the database for you
	 *  - it will create the unique constraint on the specified column. But if you already have the database,
	 *  or you alter it once created, then unique doesn't have any effect.
	 */
	@Column(length = 128, nullable = false, unique = true)
	private String name;
	
	@Column(length = 64, nullable = false, unique = true)
	private String alias;
	
	@Column(length = 128, nullable = false)
	private String image;
	
	private boolean enabled;
	
	@Column(name = "all_parent_ids", length = 256, nullable = true)
	private String allParentIDString;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;
	
	@OneToMany(mappedBy = "parent")
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();
	
	

	public Category(Integer id) {
		this.id = id;
	}
	
	public Category() {
	}

	public static Category copyIdAndName(Category category) {
		Category newCategory = new Category();
		newCategory.setId(category.getId());
		newCategory.setName(category.getName());
		return newCategory;
	}
	
	public static Category copyIdAndName(Integer id, String name) {
		Category newCategory = new Category();
		newCategory.setId(id);
		newCategory.setName(name);
		return newCategory;
	}
	
	public static Category copyFull(Category category) {
		Category newCategory = new Category();
		newCategory.setId(category.getId());
		newCategory.setName(category.getName());
		newCategory.setAlias(category.getAlias());
		newCategory.setEnabled(category.isEnabled());
		newCategory.setImage(category.getImage());
		newCategory.setHasChildren(category.getChildren().size() > 0);
		return newCategory;
	}
	
	public static Category copyFull(Category category, String name) {
		Category newCategory = Category.copyFull(category);
		newCategory.setName(name);
		return newCategory;
	}

	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default";
	}
	
	public Category(Integer id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent= parent;
	}
	
	@Transient
	public String getImagePath() {
		if(id == null || image == null || image.equals("") || image.equals("default")) {
			return "/images/image-thumbnail.png";
		}
		return "/categories-images/" + this.id + "/" + this.getImage();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildern(Set<Category> children) {
		this.children = children;
	}
	
	public String getAllParentIDString() {
		return allParentIDString;
	}
	public void setAllParentIDString(String allParentIDString) {
		this.allParentIDString = allParentIDString;
	}
	
	@Transient
	private boolean hasChildren;
	
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	public boolean isHasChildren() {
		return hasChildren;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
