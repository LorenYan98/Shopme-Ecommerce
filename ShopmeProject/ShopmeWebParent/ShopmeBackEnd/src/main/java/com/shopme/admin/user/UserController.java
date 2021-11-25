package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listAll(Model model) {
		List<User> listUsers = service.listAll();
		model.addAttribute("listUsers", listUsers);
		// users is the name of the html file in the templates, getMapping mapping all GET requests in the html file
		return "users";
	}
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		
		User user = new User();
		model.addAttribute("user",user);
		user.setEnabled(true);
		model.addAttribute("listRoles",listRoles);
		return "user_form";
	}
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes){
		System.out.println(user);
		service.save(user);
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
		return "redirect:/users";
	}
	
}
