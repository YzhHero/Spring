package com.yzh.study.spring.mvcDemo;

import com.yzh.study.spring.annotation.Service;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-13 19:36
 **/
@Service
public class DemoService implements IDemoService{

	@Override
	public void sayHello() {
		System.out.println("hello");
	}
}
