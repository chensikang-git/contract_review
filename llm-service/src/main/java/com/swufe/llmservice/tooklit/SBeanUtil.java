package com.swufe.llmservice.tooklit;

import java.lang.reflect.Field;

public class SBeanUtil {
    public static void superCopy(Object src, Object desc) {
        Class<?> srcClass = src.getClass();
        Class<?> descClass = desc.getClass();
        try {
            Field[] descFields = descClass.getDeclaredFields();
            for (Field df : descFields) {
                Module module = df.getType().getModule();
                boolean flag = module.isNamed() && module.getName().startsWith("java.");

                df.setAccessible(true);
                Object srcFieldValue = null;
                try {
                    Field sf = srcClass.getDeclaredField(df.getName());
                    sf.setAccessible(true);
                    srcFieldValue = sf.get(src);
                } catch (NoSuchFieldException e) {
                    srcFieldValue = getFieldFromParent(srcClass.getSuperclass(), src, df.getName());
                }

                if (flag) { // 内置类型
                    df.set(desc, srcFieldValue);
                } else { // 用户自定义类型
                    df.set(desc, df.getType().getDeclaredConstructor().newInstance());
                    superCopy(srcFieldValue, df.get(desc));

                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("属性映射错误");
        }
    }

    private static Object getFieldFromParent(Class<?> srcClass, Object src, String name) {
        if (srcClass != Object.class) {
            try {
                Field field = srcClass.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(src);
            } catch (Exception e) {
                return getFieldFromParent(srcClass.getSuperclass(), src, name);
            }
        }
        throw new RuntimeException("父类无法寻找到指定属性");
    }

    public static <T> T superCopy(Object src, Class<T> tClass) {
        Class<?> srcClass = src.getClass();
        T instance = null;
        try {
            instance = tClass.getDeclaredConstructor().newInstance();
            superCopy(src, instance);
        } catch (Exception ex) {
            throw new RuntimeException("属性映射错误");
        }
        return instance;
    }
}
