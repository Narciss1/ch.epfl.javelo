package ch.epfl.javelo.data;


import ch.epfl.javelo.Preconditions;


import static ch.epfl.javelo.data.Attribute.ALL;
import static ch.epfl.javelo.data.Attribute.COUNT;

public record AttributeSet (long bits) {



    public AttributeSet {
        long testBits = bits >>> COUNT;
        Preconditions.checkArgument(testBits == 0L);
    }

    /**
     *
     * @param attribute we want to check if it is the set
     * @return true if attribute is in the set
     */
    public boolean contains(Attribute attribute){
        long attributeMask = 1L << attribute.ordinal();
        long testMask = attributeMask & bits;
        return (testMask != 0L);
    }

    /**
     *
     * @param that another set of attributes
     * @return true if the two sets share at least one attribute
     */
    public boolean intersects(AttributeSet that){
        return ((bits & that.bits) != 0L);
    }

    /**
     *
     * @return the name of all the attributes in the set
     */
    @Override
    public String toString(){
        String toReturn = "{";
        boolean starting = true;
        for (int i = 0; i < ALL.toArray().length; ++i){
            if (contains(ALL.get(i))){
                if (!starting)
                    toReturn = toReturn + "," + ALL.get(i).toString();
                else {
                    toReturn = toReturn + ALL.get(i).toString();
                    starting = false;
                }
            }
        }
        toReturn = toReturn + "}";

        return toReturn;
    }


    /**
     *
     * @param attributes list of attributes we want in the set
     * @return a new set containing the attributes given
     */
    public static AttributeSet of (Attribute... attributes){
        long bitsIncremented = 0L;
        for (int i = 0; i < attributes.length; ++i){
            long mask = 1L << attributes[i].ordinal();
            bitsIncremented = bitsIncremented + mask;
        }
        return new AttributeSet(bitsIncremented);
    }

}




