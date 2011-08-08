/*
 * File:                AbstractCloneableSerializableTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.util;

import junit.framework.TestCase;

/**
 * JUnit tests for class AbstractCloneableSerializableTest
 * @author Kevin R. Dixon
 */
public class AbstractCloneableSerializableTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class AbstractCloneableSerializableTest
     * @param testName name of this test
     */
    public AbstractCloneableSerializableTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class AbstractCloneableSerializable.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        AbstractCloneableSerializable instance =
            new DefaultPair<Double,Integer>( new Double( Math.PI ), new Integer( -1 ) );        
        
        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        
    }

    /**
     * Test of toString method, of class AbstractCloneableSerializable.
     */
    public void testToString()
    {
        System.out.println( "toString" );
        AbstractCloneableSerializable instance =
            new DefaultPair<Double,Integer>( new Double( Math.PI ), new Integer( -1 ) );
        
        String result = instance.toString();
        System.out.println( "Result = " + result );
    }

}
