package CI646.test;

/**
 * Created by jb259 on 08/12/16.
 */
public class User {

    public int userID;

    public String firstName;

    public String lastName;

    public User(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String toString() {
        return String.format("%s %s [%d]", firstName, lastName, userID);
    }

}
