package com.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.stock.controller.LogController;
import com.stock.enums.Operacao;
import com.stock.models.Log;
import com.stock.models.User;
import com.stock.repository.LogRepository;
import com.stock.repository.UserRepository;


/**
 * 
 * @author isaias
 *
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LogRepository logRepository;
	
	private Log log;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user");
		}else {
			log = new Log(Operacao.LOGIN, user);
			logRepository.save(log);
		}
		
		return new MyUserDetails(user);
	}

}
