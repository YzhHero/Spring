package com.yzh.study.spring.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: HeroYang
 * @create: 2019-11-13 19:35
 **/
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowried {
	String value() default "";
}
