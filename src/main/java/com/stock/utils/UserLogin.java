package com.stock.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.stock.MyUserDetails;
import com.stock.models.User;
import com.stock.repository.UserRepository;

@Component
public class UserLogin {

	@Autowired
	private UserRepository userRepository;
	
	public User getUserLogin() {
			
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			String user;    

			if (principal instanceof MyUserDetails) {
			    user = (( MyUserDetails)principal).getUsername();
			} else {
			    user = principal.toString();
			}
			
			User userByBd = userRepository.getByUsername(user);
			
			if(userByBd != null) {
				return userByBd;
			}
			
			return null;
	}
}
