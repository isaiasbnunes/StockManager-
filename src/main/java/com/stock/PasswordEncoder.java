package com.stock;

import java.util.Scanner;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.yaml.snakeyaml.parser.ParserImpl;

public class PasswordEncoder {
	
	
	public static void main(String[] args) {
		 String senhaCriptografado = new BCryptPasswordEncoder().encode("12");
	     System.out.println(senhaCriptografado);
	
	}
	
}
