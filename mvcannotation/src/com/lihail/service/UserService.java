package com.lihail.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lihail.dto.CheckForm;


public interface UserService {
	
	public void check(HttpServletRequest request, HttpServletResponse response, CheckForm form);

}
