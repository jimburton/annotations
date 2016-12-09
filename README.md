# Annotations

This is a lab exercise for the CI346 module at the University of Brighton.

The Java Reflection API provides methods and techniques to inspect objects
at runtime. In the lecture we showed a fragment of code which retrieves all methods
defined on the `String` class, then invokes the ones that only take one parameter:

````java
try {
  Class c = Class.forName("java.lang.String");
  Method[] methods = c.getDeclaredMethods();//inc. private ones
  for (Method m : methods) {
    if (m.getParameterCount() == 0)
      System.out.println(m.getName() + ": " + m.invoke("Banana"));
  }
} catch (Exception ignore) {}

// OUTPUT
toString: Banana
hashCode: 1982479237
length: 6
isEmpty: false
getBytes: [B@14ae5a5
toLowerCase: banana
toUpperCase: BANANA
trim: Banana
toCharArray: [C@7f31245a
intern: Banana
````

Annotations can be used to provide structured documentation (such as Javadocs)
but also to provide hooks for pre-compilers, frameworks and other external 
tools. For instance, the JUnit framework uses annotations to identify
code that should be run before and after tests, and to identify and run the 
tests themselves. You've all seen and used classes like this:
 
 ````java
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MyTests {
  MyClass tester; // The class to be tested
  
  @Before
  public void setup() {
    tester = new MyClass();    
  }
  
  @Test
  public void multiplyByZero() {
    // assert statements
    assertEquals("10 x 0 must be 0", 0, tester.multiply(10, 0));
    assertEquals("0 x 10 must be 0", 0, tester.multiply(0, 10));
    assertEquals("0 x 0 must be 0", 0, tester.multiply(0, 0));
  }
}
````

JUnit uses the annotations in the code above (`@Before`, `@Test`) and the Reflection API 
to run the tests. Many other frameworks make extensive use of annotations, like
the web framework Spring, and the Hibernate ORM. Another use for them is in IDEs:
IntelliJ provides a [`@NotNull`](https://www.jetbrains.com/help/idea/2016.2/nullable-and-notnull-annotations.html) 
annotation that can help identify NPEs before they happen (Eclipse has 
something similar).
 
You are going to create your own annotations that does something similar
: `@DefaultString` and `@DefaultInt` annotations that can be applied to fields 
to make sure objects aren't instantiated and used with missing data. 
So, you will use the Annotations API to find all the fields in a class
that have these annotations attached to them, then use the Reflection API to
fill in the default value where the field hasn't been set.

In some settings, it may not make much sense to talk about an object 
having a default value, but it could be useful in the case of optional 
data collected via a web form. The alternative to using this 
annotation would be to check for null pointers throughout the code,
something which is messy and error prone.

Start off by cloning the repository:

    $ git clone https://github.com/jimburton/annotations.git

Your first task is to create an annotation. Annotations are declared with the 
`@interface` keyword like this:

````java
public @interface MyAnnotation {
    String foo;
}
````

This defines an annotation that would appear in code as `@MyAnnotation(foo="Hello world")`.
You need to state what kind of element this annotation can be applied to (class, 
constructor, method, field, etc). You also need to say how the annotation data should
be retained -- does it only relate to source code, to the compilation phase, or
should it be available at runtime? (Only annotations which are retained at runtime
can be accessed through reflection, since that is something that happens at runtime.)

So, if we want this annotation to be applied to methods and want it to be discarded 
before compilation time, the full example from above would look like this:
 
 ````java
 import java.lang.annotation.ElementType;
 import java.lang.annotation.Retention;
 import java.lang.annotation.RetentionPolicy;
 import java.lang.annotation.Target;
 
 @Target(value = ElementType.METHOD)
 @Retention(value = RetentionPolicy.SOURCE)
 public @interface MyAnnotation {
     String foo;
 }
 ````
 
 Create the `@DefaultString` and `@DefulatInt` annotations in the package `CI346.annotations`.
 Each of them is an annotation for *fields* and should be available at *runtime*. They should 
 each have one member, called value: for `@DefaultString`, `value` has the type `String`, 
 while for `@DefaultInt` it is an `int`.
 
 Next, add annotations to fields in the class `CI346.test.User`. Because your
 annotations have only member and in each case that member has the name `value`, 
 you can write these as `@DefaultString("foo")`, or `@DefaultInt(42)`. That is, 
 you don't need to write `@DefaultValue(value="foo")`, as you would if there were 
 several members. 
 
 Next you will create some "framework" code that will check whether any fields 
 in `User` objects that are marked with your annotations are `null` or, in the case 
 of `@DefaultInt`, contain the builtin default value of 0, and will apply 
 the appropriate default value from the annotation if they are. (Because `int` is a
 primitive rather than a reference type, an `int` is never `null`.)
 
 Edit the class `CI346.runner.Runner`. In the `main` method, you need to retrieve
 all of the fields of the `User` object, `o`. Have a look at the Oracle docs, which
  [summarise the methods for retrieving class members](https://docs.oracle.com/javase/tutorial/reflect/class/classMembers.html).
  You need to get a reference to the `User` class then retrieve the collection 
  of fields:
  
````java
Field[] fields = u.getClass().getDeclaredFields();
````

Loop though the fields and use the `isAnnotationPresent` method of the `Field` class
to identify those fields that have either of your annotations. If an annotation is present, 
get a reference to it like so:

````java
DefaultString def = (DefaultString) f.getAnnotation(DefaultString.class);
````

(And similarly for `DefaultInt`). For a given field, `f`, you can use `f.get(u)` 
to retrieve the value of that field in the context of the object `u`. If a field 
has your `DefaultString` annotation on it, but its value is `null`, you want to set 
it to the value supplied in the annotation. Similarly, if `f` has the `DefaultInt`
annotation but its value is 0, you should set it to the value supplied in the annotation.
This is done using the `set` method: `f.set(u, def.value())`. Your code should
have this sort of structure:

````java
for (Field f : fields) {
  if (f.isAnnotationPresent(DefaultString.class)) {
    DefaultString a = (DefaultString) f.getAnnotation(DefaultString.class);
    // get the value of f, check if it is null, set it to the 
    //value from the annotation if so
    } else if (f.isAnnotationPresent(DefaultInt.class)) {
      DefaultInt a = (DefaultInt) f.getAnnotation(DefaultInt.class);
      //as above         
    }       
}
````

The code that gets and sets the value of fields will throw various exceptions
that you will need `try` and `catch` blocks for. Run the `main` method to check
that your default values are being applied.

If you were designing a framework for which these kind of default values made
sense, you could easily extend this approach to other "defaultable" types 
(`@DefaultBoolean`, etc). 

To have a look at my version of the code, switch to the `solution` branch.
 