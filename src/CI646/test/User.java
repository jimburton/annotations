package CI646.test;

import CI346.annotations.DefaultInt;
import CI346.annotations.DefaultString;

/**
 * Created by jb259 on 08/12/16.
 */
public class User {

    @DefaultInt(42)
    public int userID;

    @DefaultString(value="John")
    public String firstName;

    @DefaultString(value="Doe")
    public String lastName;

    public User(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String toString() {
        return String.format("%s %s [%d]", firstName, lastName, userID);
    }

}
