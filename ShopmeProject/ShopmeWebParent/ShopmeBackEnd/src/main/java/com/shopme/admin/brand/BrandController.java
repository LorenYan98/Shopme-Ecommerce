package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BrandController {
	@Autowired 
	private BrandService service;
	
	@GetMapping("brands")
	public String listPage() {
		return "brands/brands";
	}
}
