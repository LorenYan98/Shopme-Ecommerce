package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 5;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listAll(){
		return (List<User>)userRepo.findAll(Sort.by("firstName").ascending());
	}
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyWord){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc")? sort.ascending(): sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if(keyWord != null) {
			return userRepo.findAll(keyWord, pageable);
		}
		
		return userRepo.findAll(pageable);
	}
	public List<Role> listRoles(){
		return (List<Role>) roleRepo.findAll();
	}
	
	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		
		//If userId is not null, we are updating the existing user
		if(isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			//If user.getPassword is empty, it means the user didn't edit the password
			//Therefore, we need to keep the current password.
			//The way to get password is to find it in the existing database.
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}else {
				// if there is an input value for the password, it means the user want to update the password.
				// we need to encode it and save it to the user information
				encodePassowrd(user);
				}
			
		}else {
			encodePassowrd(user);
		}
		return userRepo.save(user);
	}
	public void encodePassowrd(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	public boolean isEmailUnique(Integer id, String email) {
		// Find the user holds this email address
		User userByEmail = userRepo.getUserByEmail(email);
		// User == null, means there is nobody holds this email, the email is unique
		if(userByEmail == null) return true;
		
		//If id == null, means we are creating the new user.
		boolean isCreatingNew = (id == null);
		if(isCreatingNew) {
			//If userByEmail != null, means there is a user in the database holds this email
			if(userByEmail != null) return false;
		}else {
			// There is a user that already holds this email, the id is not the current id.
			if(userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch(NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		userRepo.deleteById(id);
	}
	
	public void updateUserEnableStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
