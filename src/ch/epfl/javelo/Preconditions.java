package ch.epfl.javelo;

/**
 * Provides a method to check that the arguments of a method satisfy certain conditions
 * @author Aya Hamane (345565)
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
