package com.yzh.study.spring.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-13 19:18
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
	String value() default "";
}
