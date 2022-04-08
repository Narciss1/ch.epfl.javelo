package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

import static ch.epfl.javelo.data.Attribute.ALL;
import static ch.epfl.javelo.data.Attribute.COUNT;

/**
 * Represents a set of OpenStreetMap attributes
 * @author Lina Sadgal (342075)
 */

public record AttributeSet (long bits) {

    /**
     * constructs an AttributeSet
     * @throws IllegalArgumentException when the argument bits contains a 1 in a position
     * in which there is no valid attribute
     * @param bits the succession of bits given to initialize bits
     */
    public AttributeSet {
        long testBits = bits >>> COUNT;
        Preconditions.checkArgument(testBits == 0L);
    }

    /**
     * checks if the attribute given as an argument is in the set
     * @param attribute we want to check if it is the set
     * @return true if attribute is in the set
     */
    public boolean contains(Attribute attribute){
        long attributeMask = 1L << attribute.ordinal();
        return ((attributeMask & bits) != 0);
    }

    /**
     * Checks if two attributes sets contain at least one same attribute.
     * @param that another set of attributes
     * @return true if the two sets share at least one attribute
     */
    public boolean intersects(AttributeSet that){
        return ((bits & that.bits) != 0L);
    }

    /**
     * gives a String with keys and values of all the attributes that AttributeSet contains.
     * @return the name of all the attributes in the set
     */
    @Override
    public String toString(){
        StringJoiner attributes = new StringJoiner(",", "{", "}");
        for (Attribute a : ALL){
            if (contains(a)){
                attributes.add(a.toString());
            }
        }
        return attributes.toString();
    }

    /**
     * creates an AttributeSet which attribute bits has 1 in the positions (beginning from 0)
     * of the given attributes in the list Attribute.
     * @param attributes list of attributes we want in the set
     * @return a new set containing the attributes given; an object of type AttributeSet
     */
    public static AttributeSet of (Attribute... attributes){
        long bitsIncremented = 0L;

        for (Attribute attribute : attributes){
            long mask = 1L << attribute.ordinal();
            bitsIncremented = bitsIncremented + mask;
        }

        return new AttributeSet(bitsIncremented);
    }

}




