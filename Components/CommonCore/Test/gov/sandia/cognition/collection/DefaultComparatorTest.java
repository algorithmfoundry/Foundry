/*
 * File:                DefaultComparatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 30, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

import junit.framework.TestCase;

/**
 * Unit tests for DefaultComparator.
 *
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultComparatorTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultComparatorTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Test of the constructors of class DefaultComparator.
     */
    public void testConstructors()
    {
        DefaultComparator<Double> instance = new DefaultComparator<Double>();
        assertNotNull(instance);
    }

    /**
     * Test of compare method, of class DefaultComparator.
     */
    public void testCompare()
    {
        DefaultComparator<Double> instance = new DefaultComparator<Double>();
        
        assertTrue(instance.compare(1.0, 2.0) < 0);
        assertTrue(instance.compare(3.1, 3.0) > 0);
        assertTrue(instance.compare(4.2, 4.2) == 0);
    }

}
