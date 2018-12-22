package com.lihail.listener;

import java.lang.reflect.Method;

public class HandlerMapping {
	
	private Method method;
	
	private Object controller;

	public HandlerMapping(Method method, Object controller) {
		super();
		this.method = method;
		this.controller = controller;
	}

	public HandlerMapping() {
		super();
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}
	
	

}
