/*
 * File:                PublicationTypeTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 24, 2008, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *  
 * 
 */
package gov.sandia.cognition.annotation;

import junit.framework.TestCase;

/**
 * JUnit tests for class PublicationTypeTest
 * @author Kevin R. Dixon
 */
public class PublicationTypeTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class PublicationTypeTest
     * @param testName name of this test
     */
    public PublicationTypeTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of values method, of class PublicationType.
     */
    public void testValues()
    {
        System.out.println( "values" );
        PublicationType[] result = PublicationType.values();
        for( PublicationType t : result )
        {
            System.out.println( "Type: " + t );
        }
    }

    /**
     * Test of valueOf method, of class PublicationType.
     */
    public void testValueOf()
    {
        System.out.println( "valueOf" );
        PublicationType[] types = PublicationType.values();
        for( PublicationType t : types )
        {
            PublicationType result = PublicationType.valueOf(t.toString());
            assertEquals( t, result );
            
        }        

    }

}
