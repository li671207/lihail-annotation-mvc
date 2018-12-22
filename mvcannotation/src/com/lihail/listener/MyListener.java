package com.lihail.listener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.lihail.annotation.MyAutowired;
import com.lihail.annotation.MyController;
import com.lihail.annotation.MyRequestMapping;
import com.lihail.annotation.MyService;

/**
 * Application Lifecycle Listener implementation class MyListener
 *
 */
@WebListener
public class MyListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public MyListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	ServletContext context = servletContextEvent.getServletContext();
		String path = context.getInitParameter("basePackage");
		List<String> classes = new ArrayList<String>();
		List<Object> objects = new ArrayList<Object>();
		Map<String, HandlerMapping> applicationContext = new HashMap<String, HandlerMapping>();
		Map<String, Object> objectMap = new HashMap<String, Object>();
		//加载配置包下所有类名
		addClassList(path, classes);
		System.out.println("===classes==="+classes);
		//初始化@MyController和@MyService注解的对象
		initClasses(objects, classes);
		System.out.println("===object==="+objects);
		//映射对象为Map
		initObjectMap(objects, objectMap);
		System.out.println("===objectMap==="+objectMap);
		//初始化@MyAutowired注解注入的属性
		initAutowired(objects, objectMap);
		//映射@MyRequestMapping注解的值为Map
		initContext(objects, applicationContext);
		System.out.println("===applicationContext==="+applicationContext);
		context.setAttribute("applicationContext", applicationContext);
    }

	private void initContext(List<Object> objects,
			Map<String, HandlerMapping> applicationContext) {
		String prefix = "";
		String postfix = "";
		for (Object object : objects) {
			Class<? extends Object> class1 = object.getClass();
			if (class1.isAnnotationPresent(MyRequestMapping.class)) {
				prefix = class1.getAnnotation(MyRequestMapping.class).value();
			}
			Method[] methods = class1.getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(MyRequestMapping.class)) {
					postfix = method.getAnnotation(MyRequestMapping.class).value();
					applicationContext.put(prefix+postfix, new HandlerMapping(method, object));
				}
			}
		}
		
	}

	private void initAutowired(List<Object> objects,
			Map<String, Object> objectMap) {
		for (Object object : objects) {
			Field[] declaredFields = object.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				if (field.isAnnotationPresent(MyAutowired.class)) {
					field.setAccessible(true);
					try {
						field.set(object, objectMap.get(field.getType().getSimpleName()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void initObjectMap(List<Object> objects,
			Map<String, Object> objectMap) {
		for (Object object : objects) {
			Class<?>[] interfaces = object.getClass().getInterfaces();
			if (interfaces.length>0) {
				for (Class<?> class1 : interfaces) {
					objectMap.put(class1.getSimpleName(), object);
				}
			}else {
				objectMap.put(object.getClass().getSimpleName(), object);
			}
		}
	}

    private void initClasses(List<Object> objects, List<String> classes) {
		for (String clazz : classes) {
			try {
				Class<?> class1 = Class.forName(clazz);
				if (class1.isAnnotationPresent(MyController.class) || class1.isAnnotationPresent(MyService.class)) {
					objects.add(class1.newInstance());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void addClassList(String path, List<String> classes) {
    	Properties properties = new Properties();
    	try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	String basePackage = properties.getProperty("basePackage");
		URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
		File file = new File(url.getFile());
		scanBasePackage(file, classes,basePackage);
	}

	private void scanBasePackage(File file, List<String> classes, String basePackage) {
		File[] files = file.listFiles();
		for (File file2 : files) {
			if(file2.isFile()){
				classes.add(basePackage+"."+file2.getName().replaceAll(".class", ""));
			}else{
				scanBasePackage(file2, classes, basePackage+"."+file2.getName());
			}
		}
	}

	public static void main(String[] args) {
    	Properties properties = new Properties();
		try {
			properties.load(MyListener.class.getClassLoader().getResourceAsStream("resources/application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String basePackage = properties.getProperty("basePackage");
		URL url = MyListener.class.getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
		String file = url.getFile();
		System.out.println(file);
	}
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
