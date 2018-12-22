package com.lihail.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lihail.annotation.MyAutowired;
import com.lihail.annotation.MyController;
import com.lihail.annotation.MyRequestMapping;
import com.lihail.annotation.MyRequestbody;
import com.lihail.dto.CheckForm;
import com.lihail.service.UserService;

@MyController
@MyRequestMapping("/user")
public class UserController {
	
	@MyAutowired
	UserService userService;
	
	@MyRequestMapping("/check.do")
	public void check(HttpServletRequest request, HttpServletResponse response, @MyRequestbody CheckForm form) {
		userService.check(request, response, form);
	}

}
