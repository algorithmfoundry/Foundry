/*
 * File:                DefaultIdentifiedValueTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 13, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultIdentifiedValue.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class DefaultIdentifiedValueTest
{
    protected Random random = new Random(411);

    /**
     * Creates a new test.
     */
    public DefaultIdentifiedValueTest()
    {
    }
    
    /**
     * Test of constructors of class DefaultIdentifiedValue.
     */
    @Test
    public void testConstructors()
    {
        Long identifier = null;
        String value = null;
        DefaultIdentifiedValue<Long, String> instance = new DefaultIdentifiedValue<Long, String>();
        assertSame(identifier, instance.getIdentifier());
        assertSame(value, instance.getValue());
        
        identifier = random.nextLong();
        value = "this is a value";
        instance = new DefaultIdentifiedValue<Long, String>(identifier, value);
        assertSame(identifier, instance.getIdentifier());
        assertSame(value, instance.getValue());
    }

    /**
     * Test of getIdentifier method, of class DefaultIdentifiedValue.
     */
    @Test
    public void testGetIdentifier()
    {
        this.testSetIdentifier();
    }

    /**
     * Test of setIdentifier method, of class DefaultIdentifiedValue.
     */
    @Test
    public void testSetIdentifier()
    {
        Long identifier = null;
        DefaultIdentifiedValue<Long, String> instance = new DefaultIdentifiedValue<Long, String>();
        assertSame(identifier, instance.getIdentifier());

        identifier = random.nextLong();
        instance.setIdentifier(identifier);
        assertSame(identifier, instance.getIdentifier());
        
        identifier = 1L;
        instance.setIdentifier(identifier);
        assertSame(identifier, instance.getIdentifier());

        identifier = null;
        instance.setIdentifier(identifier);
        assertSame(identifier, instance.getIdentifier());

        identifier = random.nextLong();
        instance.setIdentifier(identifier);
        assertSame(identifier, instance.getIdentifier());
    }

    /**
     * Test of getValue method, of class DefaultIdentifiedValue.
     */
    @Test
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class DefaultIdentifiedValue.
     */
    @Test
    public void testSetValue()
    {
        String value = null;
        DefaultIdentifiedValue<Long, String> instance = new DefaultIdentifiedValue<Long, String>();
        assertSame(value, instance.getValue());

        value = "this is a value";
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = "this is another value";
        instance.setValue(value);
        assertSame(value, instance.getValue());

        value = null;
        instance.setValue(value);
        assertSame(value, instance.getValue());
        
        value = "";
        instance.setValue(value);
        assertSame(value, instance.getValue());
    }

    /**
     * Test of create method, of class DefaultIdentifiedValue.
     */
    @Test
    public void testCreate()
    {
        Long identifier = null;
        String value = null;
        DefaultIdentifiedValue<Long, String> instance = DefaultIdentifiedValue.create();
        assertSame(identifier, instance.getIdentifier());
        assertSame(value, instance.getValue());

        identifier = random.nextLong();
        value = "this is a value";
        instance = DefaultIdentifiedValue.create(identifier, value);
        assertSame(identifier, instance.getIdentifier());
        assertSame(value, instance.getValue());
    }

}