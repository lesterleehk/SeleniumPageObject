
package com.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD, ElementType.FIELD}) //can use in class, method and field.

public @interface Schedule {
 
	public boolean Monthly() default false;
	public boolean Weekly() default false;
 
}
