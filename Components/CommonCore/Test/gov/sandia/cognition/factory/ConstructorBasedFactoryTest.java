/*
 * File:                ConstructorBasedFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 02, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.factory;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.lang.reflect.Constructor;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Justin Basilico
 */
public class ConstructorBasedFactoryTest extends TestCase
{
    protected Random random;
    
    public ConstructorBasedFactoryTest(String testName)
    {
        super(testName);
        
        this.random = new Random(1);
    }
    
    public void testConstructors()
        throws Exception
    {
        Constructor<Vector3> constructor = Vector3.class.getConstructor(Vector.class);
        Vector3 parameter = Vector3.createRandom(random);
        ConstructorBasedFactory<Vector> instance = 
            new ConstructorBasedFactory<Vector>(constructor, parameter);
        assertSame(constructor, instance.getConstructor());
        assertEquals(1, instance.getParameters().length);
        assertSame(parameter, instance.getParameters()[0]);
    }

    public void testClone() throws NoSuchMethodException
    {
        Constructor<Vector3> constructor = Vector3.class.getConstructor(Vector.class);
        Vector3 parameter = Vector3.createRandom(random);
        ConstructorBasedFactory<Vector> instance =
            new ConstructorBasedFactory<Vector>(constructor,parameter);
        assertEquals( 1, instance.getParameters().length );
        assertSame( parameter, instance.getParameters()[0] );

        ConstructorBasedFactory<Vector> clone = instance.clone();
        assertNotNull( clone );
        assertSame( instance.getConstructor(), clone.getConstructor() );
        assertNotSame( instance.getParameters(), clone.getParameters() );
        assertEquals( instance.getParameters().length, clone.getParameters().length );
        for( int i = 0; i < instance.getParameters().length; i++ )
        {
            assertNotSame( instance.getParameters()[i], clone.getParameters()[i] );
        }

        assertEquals( instance.create(), clone.create() );

    }

    /**
     * Test of create method, of class ConstructorBasedFactory.
     */
    public void testCreate()
        throws Exception
    {
        Constructor<Vector3> constructor = Vector3.class.getConstructor(Vector.class);
        Vector3 parameter = Vector3.createRandom(random);
        ConstructorBasedFactory<Vector> instance = 
            new ConstructorBasedFactory<Vector>(constructor, parameter);
        
        Vector created = instance.create();
        assertEquals(parameter, created);
        assertNotSame(parameter, created);
    }

    /**
     * Test of getConstructor method, of class ConstructorBasedFactory.
     */
    public void testGetConstructor()
        throws Exception
    {
        this.testSetConstructor();
    }

    /**
     * Test of setConstructor method, of class ConstructorBasedFactory.
     */
    public void testSetConstructor()
        throws Exception
    {
        Constructor<? extends Vector> constructor = Vector3.class.getConstructor();
        ConstructorBasedFactory<Vector> instance = 
            new ConstructorBasedFactory<Vector>(constructor, (Object) null);
        assertSame(constructor, instance.getConstructor());
        
        constructor = Vector2.class.getConstructor();
        instance.setConstructor(constructor);
        assertSame(constructor, instance.getConstructor());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setConstructor(null);
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
     * Test of getParameters method, of class ConstructorBasedFactory.
     */
    public void testGetParameters()
        throws Exception
    {
        this.testSetParameters();
    }

    /**
     * Test of setParameters method, of class ConstructorBasedFactory.
     */
    public void testSetParameters()
        throws Exception
    {
        Constructor<Vector3> constructor = Vector3.class.getConstructor(Vector.class);
        Vector3 parameter = null;
        ConstructorBasedFactory<Vector> instance = 
            new ConstructorBasedFactory<Vector>(constructor, parameter);
        assertEquals(1, instance.getParameters().length);
        assertSame(parameter, instance.getParameters()[0]);
        
        instance.setParameters("a", "b");
        assertEquals(2, instance.getParameters().length);
        assertEquals("a", instance.getParameters()[0]);
        assertEquals("b", instance.getParameters()[1]);
        
        instance.setParameters((Object[]) null);
    }

    public static class Barfer
    {
        /**
         * Default constructor
         */
        public Barfer()
        {
            throw new IllegalArgumentException( "Barf" );
        }
    }

    /**
     * Create(null)
     */
    public void testCreateNull() throws NoSuchMethodException
    {
        System.out.println( "Create Null" );
        Constructor<Barfer> constructor = Barfer.class.getConstructor();
        ConstructorBasedFactory<Barfer> instance =
            new ConstructorBasedFactory<ConstructorBasedFactoryTest.Barfer>( constructor );
        try
        {
            instance.create();
            fail( "Should have barfed" );
        }
        catch (RuntimeException e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
