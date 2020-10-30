package CI646.runner;

import CI646.annotations.DefaultInt;
import CI646.annotations.DefaultString;
import CI646.test.User;

import java.lang.reflect.Field;

public class Runner {

    public static void main(String[] args) {
        User u = new User(null, "Milton");
        Field[] fields = u.getClass().getDeclaredFields();

        for (Field f : fields) {
            if (f.isAnnotationPresent(DefaultString.class)) {
                DefaultString a = (DefaultString) f.getAnnotation(DefaultString.class);
                try {
                    if(f.get(u) == null) {
                        f.set(u, a.value());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.isAnnotationPresent(DefaultInt.class)) {
                DefaultInt a = (DefaultInt) f.getAnnotation(DefaultInt.class);
                try {
                    if(f.get(u) == null || ((Integer) f.get(u)).intValue() == 0) {
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
