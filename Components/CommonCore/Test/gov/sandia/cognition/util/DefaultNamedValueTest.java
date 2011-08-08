/*
 * File:                DefaultNamedValueTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DefaultNamedValue
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-27",
    changesNeeded=false,
    comments="Looks fine."
)
public class DefaultNamedValueTest
    extends TestCase
{

    public DefaultNamedValueTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        DefaultNamedValue<Integer> instance = new DefaultNamedValue<Integer>();
        assertNull(instance.getName());
        assertNull(instance.getValue());

        instance = new DefaultNamedValue<Integer>("a name", -47);
        assertEquals("a name", instance.getName());
        assertEquals(-47, (int) instance.getValue());

        instance = new DefaultNamedValue<Integer>(instance);
        assertEquals("a name", instance.getName());
        assertEquals(-47, (int) instance.getValue());
    }

    public void testClone()
    {
        System.out.println( "Clone" );

        DefaultNamedValue<Integer> instance = new DefaultNamedValue<Integer>( "a name", 10 );
        DefaultNamedValue<Integer> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getName() );
        assertEquals( instance.getName(), clone.getName() );
        assertNotNull( clone.getValue() );
        
    }

    /**
     * Test of setName method, of class gov.sandia.cognition.util.DefaultNamedValue.
     */
    public void testSetName()
    {
        DefaultNamedValue<Integer> instance = new DefaultNamedValue<Integer>();
        assertNull(instance.getName());

        instance.setName("name1");
        assertEquals("name1", instance.getName());

        instance.setName("name2");
        assertEquals("name2", instance.getName());

        instance.setName(null);
        assertNull(instance.getName());
        System.out.println("setName");
    }

    /**
     * Test of getValue method, of class gov.sandia.cognition.util.DefaultNamedValue.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class gov.sandia.cognition.util.DefaultNamedValue.
     */
    public void testSetValue()
    {
        DefaultNamedValue<Integer> instance = new DefaultNamedValue<Integer>();
        assertNull(instance.getValue());

        instance.setValue(1);
        assertEquals(1, (int) instance.getValue());

        instance.setValue(-47);
        assertEquals(-47, (int) instance.getValue());

        instance.setValue(null);
        assertNull(instance.getValue());
    }

    /**
     * createNamedValuesList
     */
    public void testCreateNamedValuesList()
    {
        System.out.println( "createNamedValuesList" );

        LinkedList<DefaultNamedValue<? extends Number>> a =
            new LinkedList<DefaultNamedValue<? extends Number>>();
        a.add( new DefaultNamedValue<Integer>( "a", 1 ) );
        a.add( new DefaultNamedValue<Double>( "b", 2.0 ) );

        ArrayList<DefaultNamedValue<DefaultNamedValue<? extends Number>>> result =
            DefaultNamedValue.createNamedValuesList(a);

        assertEquals( a.size(), result.size() );

    }

}
