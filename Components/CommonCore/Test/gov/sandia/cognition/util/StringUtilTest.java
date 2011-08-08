/*
 * File:                StringsTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: StringUtil
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-25",
    changesNeeded=false,
    comments="Looks fine"
)
public class StringUtilTest
    extends TestCase
{

    public StringUtilTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );
        StringUtil su = new StringUtil();
        assertNotNull( su );
    }

    /**
     * Test of isEmpty method, of class gov.sandia.cognition.util.Strings.
     */
    public void testIsEmpty()
    {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("a"));
        assertFalse(StringUtil.isEmpty(" "));
    }

    /**
     * Test of isWhitespace method, of class gov.sandia.cognition.util.Strings.
     */
    public void testIsWhitespace()
    {
        assertTrue(StringUtil.isWhitespace(null));
        assertTrue(StringUtil.isWhitespace(""));
        assertTrue(StringUtil.isWhitespace(" "));
        assertTrue(StringUtil.isWhitespace("    "));
        assertTrue(StringUtil.isWhitespace(" \t "));
        assertFalse(StringUtil.isWhitespace("a"));
        assertFalse(StringUtil.isWhitespace(" a "));
    }
    
    public void testCapitalizeFirstCharacter()
    {
        assertNull(StringUtil.capitalizeFirstCharacter(null));
        assertEquals("", StringUtil.capitalizeFirstCharacter(""));
        assertEquals(" ", StringUtil.capitalizeFirstCharacter(" "));
        assertEquals("  ", StringUtil.capitalizeFirstCharacter("  "));
        assertEquals("A", StringUtil.capitalizeFirstCharacter("a"));
        assertEquals("B", StringUtil.capitalizeFirstCharacter("B"));
        assertEquals("Cd", StringUtil.capitalizeFirstCharacter("cd"));
        assertEquals("EF", StringUtil.capitalizeFirstCharacter("eF"));
        assertEquals("GhI", StringUtil.capitalizeFirstCharacter("ghI"));
    }

}
