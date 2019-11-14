package com.yzh.study.spring;

import com.yzh.study.spring.annotation.Autowried;
import com.yzh.study.spring.annotation.Controller;
import com.yzh.study.spring.annotation.Service;
import com.yzh.study.spring.mvcDemo.DemoAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-13 19:25
 **/
public class DispatcherServlet extends HttpServlet {

	private Properties contextConfig = new Properties();

	private Map<String, Object> beansMap = new ConcurrentHashMap<>();

	private List<String> classNames = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	public void init() throws ServletException {
		String location = getInitParameter("contextConfigLocation");
		try {
			//定位
			doLoadCofig(location);
			//加载
			doScanner(contextConfig.getProperty("basePackage"));
			//注册
			deRegister();
			//自动依赖注入
			doAutowried();
			DemoAction demoAction = (DemoAction) beansMap.get("demoAction");
			demoAction.sayHello();
		} catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}

	}

	//将配置读取进来
	private void doLoadCofig(String location) throws IOException {
		location = location.replace("classpath:", "");
		//获取加载指定路径下的properties
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(location);
		contextConfig.load(is);
	}

	//扫包加载类
	private void doScanner(String basePackage) {
		//获取指定路径下的文件数组
		URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
		File classDir = new File(url.getFile());
		for (File file : classDir.listFiles()) {
			if (file.isDirectory()) {
				doScanner(basePackage + "." + file.getName());
			} else {
				classNames.add(basePackage + "." + file.getName().replace(".class",""));
			}
		}
	}

	private void deRegister() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		if (classNames.isEmpty()) {
			return;
		}
		for (String className : classNames) {
			Class clazz = Class.forName(className);
			//判断类是否被Controller修饰
			if (clazz.isAnnotationPresent(Controller.class)) {
				String beanName = lowerFirstCase(clazz.getSimpleName());
				beansMap.put(beanName, clazz.newInstance());
			} else if (clazz.isAnnotationPresent(Service.class)) {
				Service service = (Service) clazz.getAnnotation(Service.class);
				String beanName = service.value().trim();
				if ("".equals(beanName)) {
					beanName = lowerFirstCase(clazz.getSimpleName());
				}
				beansMap.put(beanName, clazz.newInstance());

				Class<?>[] interfaces = clazz.getInterfaces();
				for (Class<?> anInterface : interfaces) {
					beansMap.put(anInterface.getSimpleName(), clazz.newInstance());
				}
			} else {
				continue;
			}
		}
	}

	private void doAutowried() throws IllegalAccessException {
		if (beansMap.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> stringObjectEntry : beansMap.entrySet()) {
			Field[] fields = stringObjectEntry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				if (!field.isAnnotationPresent(Autowried.class)) {
					return;
				}
				Autowried autowried = field.getAnnotation(Autowried.class);
				String beanName = autowried.value().trim();
				if ("".equals(beanName)) {
					beanName = field.getType().getName();
				}
				field.setAccessible(true);
				//field赋值
				field.set(stringObjectEntry.getValue(), beansMap.get(beanName));
			}
		}
	}

	private String lowerFirstCase(String str) {
		char[] charArray = str.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
	}

}
