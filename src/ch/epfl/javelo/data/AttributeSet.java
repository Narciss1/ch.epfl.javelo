package ch.epfl.javelo.data;


import ch.epfl.javelo.Preconditions;

public record AttributeSet (long bits) {

    //Extraction avec modulo
    public AttributeSet {
        int modulo = 10;
        //Preconditions.checkArgument();
    }

    public boolean contains(Attribute attribute){
        long attributeMask = 1L << attribute.ordinal();
        long testMask = attributeMask & bits;
        return (testMask != 0L);
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




