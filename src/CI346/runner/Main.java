package CI346.runner;

import CI346.annotations.DefaultValue;
import CI346.test.User;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Main {

    public static void main(String[] args) {
        User u = new User("Jim", null);
        Field[] fields = u.getClass().getDeclaredFields();


        for (Field f : fields) {
            if (f.isAnnotationPresent(DefaultValue.class)) {
                DefaultValue a = (DefaultValue) f.getAnnotation(DefaultValue.class);
                Class t = f.getType();
                Object v = null;
                try {
                    v = f.get(u);
                    if(t == boolean.class && Boolean.FALSE.equals(v)) {
                        f.set(u, Boolean.parseBoolean(a.value()));
                    }
                    else if (t.isPrimitive() && ((Number) v).doubleValue() == 0) {
                        f.set(u, Integer.parseInt(a.value()));
                    }
                    else if (!t.isPrimitive() && v == null) {
                        f.set(u, a.value());
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(u);
    }
}
