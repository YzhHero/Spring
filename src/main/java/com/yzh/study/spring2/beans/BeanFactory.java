package com.yzh.study.spring2.beans;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-18 11:30
 **/
public interface BeanFactory {

	Object getBean(String beanName);

	boolean isSingleton(String name);

}
