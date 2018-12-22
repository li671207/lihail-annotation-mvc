package com.lihail.servlet;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lihail.annotation.MyRequestbody;
import com.lihail.listener.HandlerMapping;

public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-type", "text/html;charset=utf-8");
		Map<String, HandlerMapping> context = (Map<String, HandlerMapping>) request.getServletContext().getAttribute("applicationContext");
		String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		if (context.containsKey(url)) {
			HandlerMapping handlerMapping = context.get(url);
			try {
				Method method = handlerMapping.getMethod();
				Annotation[][] parameterAnnotations = method.getParameterAnnotations();
				int index = 0;
				//获取@MyRequestbody注解的参数对象
				for (int i = 0; i < parameterAnnotations.length; i++) {
					for (Annotation annotations : parameterAnnotations[i]) {
						if (annotations instanceof MyRequestbody) {
							index = i;
							break;
						}
					}
				}
				Object param = method.getParameters()[index].getType().newInstance();
				Field[] fields = param.getClass().getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					field.set(param, request.getParameter(field.getName()));
				}
				method.invoke(handlerMapping.getController(), new Object[]{request, response, param});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}
	}

}
