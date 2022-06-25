package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.models.User;

/**
 * 
 * @author isaias
 *
 */
public interface UserRepository extends JpaRepository<User, Long>{

	@Query("SELECT u FROM User u WHERE u.username = :username")
	public User getByUsername(@Param("username") String username);
	
	List<User> findByOrderByNameAsc();
}
