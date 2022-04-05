package ch.epfl.javelo.data;


import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.*;

public class AttributeSetTest

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

    @Test
    void constructorToThrowException(){
        assertThrows(IllegalArgumentException.class, () ->{
            var actual1 = new AttributeSet(0b000011111111111111111111111111111111);
        });

    }

    @Test
    void ofToWorkOnValidValue(){
        var actual1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE);
        var expected1 = new AttributeSet(0b00000000001);
        assertEquals(expected1, actual1);
    }

    @Test
    void containsToWork(){
        var test1 = AttributeSet.of(Attribute.HIGHWAY_CYCLEWAY);
        var actual1 = test1.contains(Attribute.HIGHWAY_CYCLEWAY);
        var actual2 = test1.contains(Attribute.ACCESS_NO);
        assertEquals(true, actual1);
        assertEquals(false, actual2);
    }

    @Test
    void toStringWorksFine(){
        var actual1 = AttributeSet.of(Attribute.ACCESS_PRIVATE, Attribute.BICYCLE_DESIGNATED);
        var expected1=("{access=private" + "," + "bicycle=designated}");
        assertEquals(expected1, actual1.toString());
    }

    @Test
    void intersectToWork(){
        var actual1 = AttributeSet.of(Attribute.HIGHWAY_CYCLEWAY, Attribute.BICYCLE_NO);
        var actual2 = AttributeSet.of(Attribute.ACCESS_NO, Attribute.CYCLEWAY_OPPOSITE);
        assertFalse(actual1.intersects(actual2));

        var actual3 = AttributeSet.of(Attribute.HIGHWAY_CYCLEWAY, Attribute.BICYCLE_YES);

        assertTrue(actual1.intersects(actual3));
    }


}
