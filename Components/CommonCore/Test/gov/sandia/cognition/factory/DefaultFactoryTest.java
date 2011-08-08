/*
 * File:                DefaultFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 01, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.factory;

import gov.sandia.cognition.math.matrix.AbstractVector;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.DenseVector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * @author Justin Basilico
 */
public class DefaultFactoryTest 
    extends TestCase
{

    public DefaultFactoryTest(
        final String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        Class<? extends Vector> createdClass = Vector3.class;
        DefaultFactory<Vector> instance = new DefaultFactory<Vector>(createdClass);
        assertSame(createdClass, instance.getCreatedClass());
    }

    /**
     * Test of create method, of class DefaultFactory.
     */
    public void testCreate()
    {
        DefaultFactory<?> instance = DefaultFactory.get(Vector3.class);
        assertEquals(new Vector3(), instance.create());
        assertEquals(instance.create(), instance.create());
        assertNotSame(instance.create(), instance.create());
        
        instance = DefaultFactory.get(String.class);
        assertEquals("", instance.create());
        
        instance = DefaultFactory.get(ArrayList.class);
        assertEquals(new ArrayList<Object>(), instance.create());
    }

    /**
     * Test of getCreatedClass method, of class DefaultFactory.
     */
    public void testGetCreatedClass()
    {
        this.testSetCreatedClass();
    }

    /**
     * Test of setCreatedClass method, of class DefaultFactory.
     */
    public void testSetCreatedClass()
    {
        Class<? extends Vector> createdClass = Vector3.class;
        DefaultFactory<Vector> instance = new DefaultFactory<Vector>(createdClass);
        assertSame(createdClass, instance.getCreatedClass());
        
        createdClass = Vector2.class;
        instance.setCreatedClass(createdClass);
        assertSame(createdClass, instance.getCreatedClass());
        
        
        boolean exceptionThrown = false;
        try
        {
            instance.setCreatedClass(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance.setCreatedClass(Vector.class);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance.setCreatedClass(AbstractVector.class);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance.setCreatedClass(DenseVector.class);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of get method, of class DefaultFactory.
     */
    public void testGet()
    {
        assertNotNull(DefaultFactory.get(String.class));
        assertNotNull(DefaultFactory.get(Vector3.class));
        
        boolean exceptionThrown = false;
        try
        {
            DefaultFactory.get(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
            
        exceptionThrown = false;
        try
        {
            DefaultFactory.get(Vector.class);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            DefaultFactory.get(AbstractCloneableSerializable.class);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            DefaultFactory.get(DenseVector.class);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
    }

}
