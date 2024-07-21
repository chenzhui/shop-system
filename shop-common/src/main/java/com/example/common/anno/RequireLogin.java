package com.example.common.anno;

import java.lang.annotation.*;


@Target({ElementType.METHOD})   //说明该注解用于方法上
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Documented
public @interface RequireLogin {
}
