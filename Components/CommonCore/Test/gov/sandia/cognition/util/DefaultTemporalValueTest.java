/*
 * File:                DefaultTemporalValueTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright August 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author Justin Basilico
 */
public class DefaultTemporalValueTest extends TestCase {
    
    public DefaultTemporalValueTest(String testName) {
        super(testName);
    }
    
    public void testConstructors()
    {
        Date time = null;
        String value = null;
        
        DefaultTemporalValue<String> instance = new DefaultTemporalValue<String>();
        assertSame(time, instance.getTime());
        assertSame(value, instance.getValue());
        
        time = new Date();
        value = "value";
        
        instance = new DefaultTemporalValue<String>(time, value);
        assertSame(time, instance.getTime());
        assertSame(value, instance.getValue());
        
    }

    /**
     * Test of getTime method, of class DefaultTemporalValue.
     */
    public void testGetTime()
    {
        this.testSetTime();
    }

    /**
     * Test of setTime method, of class DefaultTemporalValue.
     */
    public void testSetTime()
    {
        Date time = null;
        
        DefaultTemporalValue<String> instance = new DefaultTemporalValue<String>();
        assertSame(time, instance.getTime());
        
        time = new Date();
        instance.setTime(time);
        assertSame(time, instance.getTime());
        
        time = new Date();
        instance.setTime(time);
        assertSame(time, instance.getTime());
        
        
        time = new Date(0);
        instance.setTime(time);
        assertSame(time, instance.getTime());
        
        
        time = null;
        instance.setTime(time);
        assertSame(time, instance.getTime());
    }

    /**
     * Test of getValue method, of class DefaultTemporalValue.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class DefaultTemporalValue.
     */
    public void testSetValue()
    {
        String value = null;
        
        DefaultTemporalValue<String> instance = 
            new DefaultTemporalValue<String>();
        assertSame(value, instance.getValue());
        
        value = "value";
        instance.setValue(value);
        assertSame(value, instance.getValue());
        
        value = "value2";
        instance.setValue(value);
        assertSame(value, instance.getValue());
        
        value = null;
        instance.setValue(value);
        assertSame(value, instance.getValue());
    }

    /**
     * Test of getFirst method, of class DefaultTemporalValue.
     */
    public void testGetFirst()
    {
        Date time = new Date();
        
        DefaultTemporalValue<String> instance = 
            new DefaultTemporalValue<String>(time, null);
        assertSame(time, instance.getFirst());
    }

    /**
     * Test of getSecond method, of class DefaultTemporalValue.
     */
    public void testGetSecond()
    {
        String value = "value";
        
        DefaultTemporalValue<String> instance = 
            new DefaultTemporalValue<String>(null, value);
        assertSame(value, instance.getSecond());
    }

}
