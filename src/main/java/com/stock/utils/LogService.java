package com.stock.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.enums.Operacao;
import com.stock.models.Log;
import com.stock.repository.LogRepository;

@Service
public class LogService {

	@Autowired
	private UserLogin userLogin;
	
	@Autowired
	private LogRepository logRepository;
	
	public void save(String dadosAntes, String dadosDepois, Operacao o) {
		Log log = new Log(dadosAntes, dadosDepois, o, userLogin.getUserLogin());
		logRepository.save(log);
	}
}
