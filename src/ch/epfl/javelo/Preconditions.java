package ch.epfl.javelo;

public final class Preconditions {

    private Preconditions() {}

    /**
     * checks if a method throws an Illegal Argument Exception.
     * @param shouldBeTrue the argument that should be true.
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}
