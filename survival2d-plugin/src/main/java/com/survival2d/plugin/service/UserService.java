package com.survival2d.plugin.service;


import com.survival2d.common.entity.User;
import java.util.List;

public interface UserService {
	
	void saveUser(User user);
	
	User createUser(String username, String password);
	
	User getUser(String username);
	
	List<User> getAllUsers();
}
