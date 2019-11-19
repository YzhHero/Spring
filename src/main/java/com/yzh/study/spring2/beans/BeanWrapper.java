package com.yzh.study.spring2.beans;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-18 14:04
 **/
public class BeanWrapper {

	private Object proxyInstance;

	private Object realInstance;

	public Object getProxyInstance() {
		return proxyInstance;
	}

	public void setProxyInstance(Object proxyInstance) {
		this.proxyInstance = proxyInstance;
	}

	public Object getRealInstance() {
		return realInstance;
	}

	public void setRealInstance(Object realInstance) {
		this.realInstance = realInstance;
	}
}
