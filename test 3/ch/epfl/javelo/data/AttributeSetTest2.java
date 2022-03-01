package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.*;

public class AttributeSetTest2

{

    @Test
    void toStringWorks(){
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void toStringWorksForEmptySet(){
        AttributeSet set = AttributeSet.of();
        assertEquals("{}", set.toString());
    }

    @Test
    void toStringWorksForExtremeAttributes(){
        AttributeSet set = AttributeSet.of(Attribute.LCN_YES, Attribute.HIGHWAY_SERVICE);
        assertEquals("{highway=service,lcn=yes}", set.toString());
    }

    @Test
    void intersectShouldBeTrue(){
        AttributeSet set1 = AttributeSet.of(BICYCLE_DISMOUNT, BICYCLE_PERMISSIVE);
        AttributeSet set2 = AttributeSet.of(BICYCLE_PERMISSIVE, HIGHWAY_SERVICE, LCN_YES);
        assertEquals(true, set1.intersects(set2));
    }

    @Test
    void intersectShouldBeFalse(){
        AttributeSet set1 = AttributeSet.of(BICYCLE_DISMOUNT, BICYCLE_PERMISSIVE);
        AttributeSet set2 = AttributeSet.of(HIGHWAY_SERVICE, LCN_YES);
        assertEquals(false, set1.intersects(set2));
    }

    @Test
    void contrainsShouldBeTrue(){
        AttributeSet set = AttributeSet.of(BICYCLE_DISMOUNT, BICYCLE_PERMISSIVE);
        assertEquals(true, set.contains(BICYCLE_PERMISSIVE));
    }

    @Test
    void containsShouldBeFalse(){
        AttributeSet set = AttributeSet.of(BICYCLE_DISMOUNT, BICYCLE_PERMISSIVE);
        assertEquals(false, set.contains(SURFACE_COBBLESTONE));
    }

    @Test
    void constructorThrowsCondition(){
        long wrong = 1L << 62;
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(wrong);
        });

    }


}
