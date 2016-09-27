/*
 * File:                TwoWayIndexTest.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author jdwendt
 */
public class TwoWayIndexTest
{

    @Test
    public void basicTest()
    {
        TwoWayIndex<String> strIdx = new TwoWayIndex<>(10);
        int aIdx = strIdx.putValue("a");
        int bIdx = strIdx.putValue("b");
        int cIdx = strIdx.putValue("c");
        int dIdx = strIdx.putValue("d");
        int eIdx = strIdx.putValue("e");
        int fIdx = strIdx.putValue("f");
        int gIdx = strIdx.putValue("g");
        int hIdx = strIdx.putValue("h");

        assertEquals(aIdx, strIdx.getIndex("a"));
        assertEquals(bIdx, strIdx.getIndex("b"));
        assertEquals(cIdx, strIdx.getIndex("c"));
        assertEquals(dIdx, strIdx.getIndex("d"));
        assertEquals(eIdx, strIdx.getIndex("e"));
        assertEquals(fIdx, strIdx.getIndex("f"));
        assertEquals(gIdx, strIdx.getIndex("g"));
        assertEquals(hIdx, strIdx.getIndex("h"));
        try
        {
            strIdx.getIndex("i");
            assertTrue(false);
        }
        catch (IllegalArgumentException iae)
        {
            // Correct path!
        }
        try
        {
            strIdx.getIndex(null);
            assertTrue(false);
        }
        catch (IllegalArgumentException iae)
        {
            // Correct path!
        }

        assertTrue(strIdx.contains("a"));
        assertTrue(strIdx.contains("b"));
        assertTrue(strIdx.contains("c"));
        assertTrue(strIdx.contains("d"));
        assertTrue(strIdx.contains("e"));
        assertTrue(strIdx.contains("f"));
        assertTrue(strIdx.contains("g"));
        assertTrue(strIdx.contains("h"));
        assertFalse(strIdx.contains("i"));
        assertFalse(strIdx.contains(null));

        assertEquals("a", strIdx.getValue(aIdx));
        assertEquals("b", strIdx.getValue(bIdx));
        assertEquals("c", strIdx.getValue(cIdx));
        assertEquals("d", strIdx.getValue(dIdx));
        assertEquals("e", strIdx.getValue(eIdx));
        assertEquals("f", strIdx.getValue(fIdx));
        assertEquals("g", strIdx.getValue(gIdx));
        assertEquals("h", strIdx.getValue(hIdx));
        try
        {
            strIdx.getValue(hIdx + 1);
            assertTrue(false);
        }
        catch (IndexOutOfBoundsException ioobe)
        {
            // Correct path!
        }
        try
        {
            strIdx.getValue(-1);
            assertTrue(false);
        }
        catch (IndexOutOfBoundsException ioobe)
        {
            // Correct path!
        }

        assertEquals(8, strIdx.size());

        TwoWayIndex<String> copy = new TwoWayIndex<>(10);
        copy.putValue("a");
        copy.putValue("b");
        copy.putValue("c");
        copy.putValue("d");
        copy.putValue("e");
        copy.putValue("f");
        copy.putValue("g");
        copy.putValue("h");
        assertTrue(copy.equals(strIdx));
        assertTrue(strIdx.equals(copy));
        assertTrue(strIdx.equals(strIdx));
        assertEquals(strIdx.hashCode(), copy.hashCode());

        List<String> vals = strIdx.getValues();
        assertEquals(strIdx.size(), vals.size());
        for (int i = 0; i < strIdx.size(); ++i)
        {
            assertEquals(strIdx.getValue(i), vals.get(i));
        }
        try
        {
            vals.set(0, null);
            assertTrue(false);
        }
        catch (UnsupportedOperationException uoe)
        {
            // Correct path
        }

        strIdx.clear();
        assertEquals(0, strIdx.size());
        assertFalse(strIdx.contains("a"));
    }

}
