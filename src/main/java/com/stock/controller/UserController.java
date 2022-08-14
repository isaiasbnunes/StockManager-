package com.stock.controller;


import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.stock.MyUserDetails;
import com.stock.enums.Role;
import com.stock.models.User;
import com.stock.repository.UserRepository;



@Controller
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/admin/user/new-admin")
	public ModelAndView  cadastrar(User user) {
		ModelAndView mv = new ModelAndView("admin/user/new-user");
		mv.addObject("user", user);
		mv.addObject("roles", Role.values());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/admin/user/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("admin/user/lista");
		
		mv.addObject("listaUser", userRepository.findByOrderByNameAsc());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@PostMapping(value = "/admin/user/salvar" )
    public ModelAndView salvar(User user) {
		   
		   User userName = userRepository.getByUsername(user.getUsername());
		   
		   if(userName != null && userName.getUsername().equalsIgnoreCase(user.getUsername())) {
			   ModelAndView mv = new ModelAndView("admin/user/new-user"); 
				
				mv.addObject("msg", "error");
				mv.addObject("roles", Role.values());
			   return mv;
		   }
		   
		 
			   String password = new BCryptPasswordEncoder().encode(user.getPassword());
			   user.setPassword(password);
			
			   User u = userRepository.saveAndFlush(user);	
			   if(u.getUsername().equals(user.getUsername())) {
					ModelAndView mv = new ModelAndView("admin/user/new-user"); 
					mv.addObject("user", new User());
					mv.addObject("msg", "success");
					mv.addObject("roles", Role.values());
					
					return mv;
			   }
					
			
		   return cadastrar(user);
	}
	
	
	//Quem edita é o admin
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@PostMapping(value = "/admin/user/editar-admin" )
    public ModelAndView editarAdmin(@Valid User user) {
		   ModelAndView mv = new ModelAndView("admin/user/editar-user-admin");
		  
			  
		   userRepository.saveAndFlush(user);
		   mv.addObject("roles", Role.values());
		   mv.addObject("user", user);
		   mv.addObject("msg", "success");
			
		   return mv;
		   
	}
	
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping(value = "/admin/user/editar" )
    public ModelAndView editar(@Valid User user) {
		   ModelAndView mv = new ModelAndView("admin/user/edit-user"); 
			
		   if(!senhaForte(user.getPassword())) {
			   
			   mv.addObject("msg", "errorSenhaFraca");
			   return mv;
			   
		   }else if(confirmPassword(user.getConfirmPassword())) {
			   String password = new BCryptPasswordEncoder().encode(user.getPassword());
			   user.setPassword(password);
			   userRepository.saveAndFlush(user);
					
				  mv.addObject("user", user);
				  mv.addObject("msg", "success");
					
				return mv;
		   }
		   
		   mv.addObject("msg", "error");
		   return mv;
		   
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@GetMapping("/admin/user/edit_user{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		
		Optional<User> user = userRepository.findById(id);
		ModelAndView mv = new ModelAndView("admin/user/editar-user-admin");
		mv.addObject("user", user);
		mv.addObject("roles", Role.values());
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@GetMapping("/admin/user/edit_user")
	public ModelAndView editar() {
		User u = getUserLogin();
		Optional<User> user = userRepository.findById(u.getId());
		ModelAndView mv = new ModelAndView("admin/user/edit-user");
		mv.addObject("user", user);
		String senha = "";
		mv.addObject("senha", senha);
		
		
		return mv;
	}
	
	
	private boolean confirmPassword(String senha) {

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);
		boolean senhaAtualValida =  passwordEncoder.matches(senha, getUserLogin().getPassword());
		
		return senhaAtualValida;
	}
	
	private User getUserLogin() {
		
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
	
	
	public static boolean senhaForte(String senha) {
	    if (senha.length() < 6) return false;

	    boolean achouNumero = false;
	    boolean achouMaiuscula = false;
	    boolean achouMinuscula = false;
	    boolean achouSimbolo = false;
	    for (char c : senha.toCharArray()) {
	         if (c >= '0' && c <= '9') {
	             achouNumero = true;
	         } else if (c >= 'A' && c <= 'Z') {
	             achouMaiuscula = true;
	         } else if (c >= 'a' && c <= 'z') {
	             achouMinuscula = true;
	         } else if (c == '@' || c == '#' || c == '$' || c == '%' || c == '&') {
	        	
	             achouSimbolo = true;
	         }
	    }
	    return achouNumero && achouMaiuscula && achouMinuscula && achouSimbolo;
	}
}
	
	





