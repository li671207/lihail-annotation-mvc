package com.lihail.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lihail.annotation.MyService;
import com.lihail.dto.CheckForm;

@MyService
public class UserServiceImpl implements UserService{

	@Override
	public void check(HttpServletRequest request, HttpServletResponse response, CheckForm form) {
		if("admin".equals(form.getText())){
			request.setAttribute("result", "成功");
		}else {
			request.setAttribute("result", "失败");
		}
		try {
			request.getRequestDispatcher("result.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
