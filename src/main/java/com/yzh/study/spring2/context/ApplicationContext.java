package com.yzh.study.spring2.context;

import com.yzh.study.spring.annotation.Autowried;
import com.yzh.study.spring.annotation.Controller;
import com.yzh.study.spring.annotation.Service;
import com.yzh.study.spring2.beans.BeanDefinition;
import com.yzh.study.spring2.beans.BeanFactory;
import com.yzh.study.spring2.beans.BeanWrapper;
import com.yzh.study.spring2.context.support.BeanDefinitionReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-18 11:40
 **/
public class ApplicationContext implements BeanFactory {

	private String[] configLocations;

	Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

	Map<String, BeanWrapper> beanWrapperCacheMap = new ConcurrentHashMap<>();

	private BeanDefinitionReader reader;

	public ApplicationContext(String... configLocations) {
		this.configLocations = configLocations;
		refresh();
	}

	private void refresh() {
		try {
			//定位
			reader = new BeanDefinitionReader(this.configLocations);
			//加载
			reader.loadBeanDefintion();
			//注册
			registerBeans();
			//依赖注入，只考虑自动加载的
			for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
				BeanDefinition beanDefinition = entry.getValue();
				//对于非懒加载的，开始自动装配
				if (!beanDefinition.isLazyInit()) {
					getBean(beanDefinition.getFactoryBeanName());
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//此处没有考虑工厂bean的get
	@Override
	public Object getBean(String beanName) {
		Object bean = null;
		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if (beanDefinition == null) {
			return null;
		}
		//如果单例  且  在缓存中存在
		if (beanDefinition.isSingleton() && beanWrapperCacheMap.containsKey(beanName)) {
			bean = beanWrapperCacheMap.get(beanName).getProxyInstance();
		} else {
			try {
				bean = createBean(beanDefinition);
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
		return bean;
	}

	private void populateBean(BeanDefinition beanDefinition, BeanWrapper beanWrapper)
			throws ClassNotFoundException, IllegalAccessException {
		Class clazz = Class.forName(beanDefinition.getBeanClassName());
		Field[] fields = clazz.getDeclaredFields();
		//如果有@Autowried ，@Autowried有值，取beanName为该值
		//@Autowried无值，按类型取
		for (Field field : fields) {
			if (field.isAnnotationPresent(Autowried.class)) {
				Autowried autowried = field.getAnnotation(Autowried.class);
				String fieldBeanName = autowried.value().trim();
				if ("".equals(fieldBeanName)) {
					fieldBeanName = field.getType().getSimpleName();
				}
				field.setAccessible(true);
				field.set(beanWrapper.getProxyInstance(), getBean(fieldBeanName));
			}
		}
	}

	private Object createBean(BeanDefinition beanDefinition)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		//创建实例
		BeanWrapper beanWrapper = createBeanInstance(beanDefinition);
		//依赖注入
		populateBean(beanDefinition, beanWrapper);
		beanWrapperCacheMap.put(beanDefinition.getFactoryBeanName(), beanWrapper);
		return beanWrapper.getProxyInstance();
	}

	private BeanWrapper createBeanInstance(BeanDefinition beanDefinition)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class clazz = Class.forName(beanDefinition.getBeanClassName());
		BeanWrapper beanWrapper = new BeanWrapper();
		beanWrapper.setProxyInstance(clazz.newInstance());
		beanWrapper.setRealInstance(clazz.newInstance());
		return beanWrapper;
	}

	private void registerBeans() throws ClassNotFoundException {
		for (String className : reader.getBeanDefinitionRegistry()) {
			BeanDefinition beanDefinition = reader.registerBean(className);
			if (beanDefinition == null) {
				return;
			}
			Class beanClass = Class.forName(className);
			if (isContainTheAnnotation(beanClass, Controller.class)) {
				beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
			} else if (isContainTheAnnotation(beanClass, Service.class)) {
				Service service = (Service) beanClass.getAnnotation(Service.class);
				if (!"".equals(service.value().trim())) {
					beanDefinition.setFactoryBeanName(service.value().trim());
					beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
				}
				Class[] interfaces = beanClass.getInterfaces();
				for (Class i : interfaces) {
					beanDefinitionMap.put(i.getSimpleName(), beanDefinition);
				}

			}
		}
	}

	private boolean isContainTheAnnotation(Class beanClass, Class<? extends Annotation> annotationClass) {
		return beanClass.isAnnotationPresent(annotationClass);
	}

	@Override
	public boolean isSingleton(String name) {
		return false;
	}

}
