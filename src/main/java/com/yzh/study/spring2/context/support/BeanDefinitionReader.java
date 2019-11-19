package com.yzh.study.spring2.context.support;

import com.yzh.study.spring2.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-18 17:37
 **/
public class BeanDefinitionReader {

	private Properties properties = new Properties();

	private static final String BASE_PACKAGE_KEY = "basePackage";

	private List<String> BeanDefinitionRegistry = new ArrayList<>();

	public BeanDefinitionReader(String... configLocations) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0]);
		try {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadBeanDefintion() {
		doScanner(this.properties.getProperty(BASE_PACKAGE_KEY));
	}

	private void doScanner(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
		File file = new File(url.getFile());
		for (File f : file.listFiles()) {
			if (f.isDirectory()) {
				doScanner(basePackage + "." + f.getName());
			} else {
				BeanDefinitionRegistry.add(basePackage + "." + f.getName().replace(".class", ""));
			}
		}
	}

	public List<String> getBeanDefinitionRegistry() {
		return BeanDefinitionRegistry;
	}

	public BeanDefinition registerBean(String className) {
		if (BeanDefinitionRegistry.contains(className)){
			BeanDefinition beanDefinition = new BeanDefinition();
			beanDefinition.setLazyInit(false);
			beanDefinition.setSingleton(true);
			beanDefinition.setBeanClassName(className);
			beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
			return beanDefinition;
		}
		return null;
	}

	private String lowerFirstCase(String str) {
		char[] charArray = str.toCharArray();
		charArray[0] += 32;
		return String.valueOf(charArray);
	}
}
