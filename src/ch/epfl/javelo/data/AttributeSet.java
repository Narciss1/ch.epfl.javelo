package ch.epfl.javelo.data;


import ch.epfl.javelo.Preconditions;


import static ch.epfl.javelo.data.Attribute.ALL;
import static ch.epfl.javelo.data.Attribute.COUNT;

public record AttributeSet (long bits) {


    public AttributeSet {
        long testBits = bits >>> COUNT;
        Preconditions.checkArgument(testBits == 0L);
    }

    public boolean contains(Attribute attribute){
        long attributeMask = 1L << attribute.ordinal();
        long testMask = attributeMask & bits;
        return (testMask != 0L);
    }

    public boolean intersects(AttributeSet that){
        return ((bits & that.bits) != 0L);
    }

    @Override
    public String toString(){
        String toReturn = "{";
        boolean starting = true;
        for (int i = 0; i < ALL.toArray().length; ++i){
            if (contains(ALL.get(i))){
                if (!starting)
                    toReturn = toReturn + "," + ALL.get(i).toString();
                if (starting){
                    toReturn = toReturn + ALL.get(i).toString();
                    starting = false;
                }
            }
        }
        toReturn = toReturn + "}";

        return toReturn;
    }


    public static AttributeSet of (Attribute... attributes){
        long bitsIncremented = 0L;
        for (int i = 0; i < attributes.length; ++i){
            long mask = 1L << attributes[i].ordinal();
            bitsIncremented = bitsIncremented + mask;
        }
        return new AttributeSet(bitsIncremented);
    }

}




