package com.yzh.study.spring.mvcDemo;

import com.yzh.study.spring.annotation.Autowried;
import com.yzh.study.spring.annotation.Controller;
import com.yzh.study.spring.annotation.RequestMapping;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-13 19:35
 **/
@Controller
public class DemoAction {
	@Autowried
	private IDemoService demoService;

	@RequestMapping("/sayHello")
	public void sayHello() {
		demoService.sayHello();
	}
}
