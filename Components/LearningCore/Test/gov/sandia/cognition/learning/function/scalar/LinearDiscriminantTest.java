/*
 * File:                LinearDiscriminantTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class LinearDiscriminantTest
    extends TestCase
{
    
    /** The random number generator for the tests. */
    public Random random = new Random(1);
    
    public LinearDiscriminantTest(String testName)
    {
        super(testName);
    }
    
    public LinearDiscriminant createInstance()
    {
        int M = random.nextInt(5) + 1;
        return new LinearDiscriminant( VectorFactory.getDefault().createUniformRandom( M, -1, 1, random ) );
    }


    public void testConstructors()
    {
        System.out.println( "Constructors" );

        LinearDiscriminant instance = new LinearDiscriminant();
        assertNull( instance.getWeightVector() );

    }


    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        LinearDiscriminant instance = this.createInstance();
        
        LinearDiscriminant clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getWeightVector(), clone.getWeightVector() );
        assertEquals( instance.getWeightVector(), clone.getWeightVector() );
    }
    
    /**
     * Test of getInputDimensionality method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        
        LinearDiscriminant instance = this.createInstance();
        assertEquals( instance.getWeightVector().getDimensionality(), instance.getInputDimensionality() );
        
    }
    
    /**
     * Test of getWeightVector method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testGetWeightVector()
    {
        System.out.println("getWeightVector");
        
        LinearDiscriminant instance = this.createInstance();
        assertNotNull( instance.getWeightVector() );
        
    }
    
    /**
     * Test of setWeightVector method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testSetWeightVector()
    {
        System.out.println("setWeightVector");
        
        LinearDiscriminant instance = this.createInstance();
        assertNotNull( instance.getWeightVector() );
        
        Vector w = instance.getWeightVector();
        instance.setWeightVector( null );
        assertNull( instance.getWeightVector() );
        instance.setWeightVector( w );
        assertSame( w, instance.getWeightVector() );
    }
    
    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        final double EPS = 1e-5;
        double r = 10;
        for( int i = 0; i < 100; i++ )
        {
            LinearDiscriminant instance = this.createInstance();
            Vector w = instance.getWeightVector();
            int M = instance.getInputDimensionality();
            Vector x = VectorFactory.getDefault().createUniformRandom( M, -r, r, random );
            double yhat = instance.evaluate( x );
            assertEquals( w.dotProduct( x ), yhat, EPS );
        }
        
    }
    
    /**
     * Test of convertToVector method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        
        LinearDiscriminant instance = this.createInstance();
        assertSame( instance.getWeightVector(), instance.convertToVector() );
        
    }
    
    /**
     * Test of convertFromVector method, of class gov.sandia.cognition.learning.util.function.LinearDiscriminant.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        
        LinearDiscriminant instance = this.createInstance();
        
        Vector p = instance.getWeightVector().scale( random.nextGaussian() );
        instance.convertFromVector( p );
        assertSame( p, instance.getWeightVector() );
    }
    
}
