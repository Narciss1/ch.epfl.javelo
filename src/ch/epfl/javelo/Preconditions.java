package ch.epfl.javelo;

/**
 *  Represents the class that checks if the arguments of a method in the project are valid
 and throws an IllegalArgumentException through its checkArgument method if not.
 * @author Lina Sadgal (342075)
 */

public final class Preconditions {

    private Preconditions() {}

    /**
     * Throws an Illegal Argument Exception when the argument taken is false.
     * @param shouldBeTrue the argument that determines whether throw the exception.
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}
