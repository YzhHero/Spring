package com.yzh.study.spring2;

import com.yzh.study.spring.mvcDemo.DemoAction;
import com.yzh.study.spring2.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-18 11:25
 **/
public class DispatcherServlet extends HttpServlet {

	private static final String LOCATION_KEY = "contextConfigLocation";

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
		String configLocations = getInitParameter(LOCATION_KEY);
		ApplicationContext applicationContext = new ApplicationContext(configLocations.replace("classpath:",""));
		DemoAction demoAction = (DemoAction) applicationContext.getBean("demoAction");
		demoAction.sayHello();
	}
}
