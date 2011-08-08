/*
 * File:                CategoricalDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 24, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import java.util.ArrayList;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDiscreteDistributionTestHarness;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class CategoricalDistributionTest.
 * @author krdixon
 */
public class CategoricalDistributionTest
    extends MultivariateClosedFormComputableDiscreteDistributionTestHarness<Vector>
{

    /**
     * Default Constructor
     */
    public CategoricalDistributionTest()
    {
        super( "test" );
    }


    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        CategoricalDistribution instance = new CategoricalDistribution();
        assertEquals( CategoricalDistribution.DEFAULT_NUM_CLASSES, instance.getInputDimensionality() );
        assertNotNull( instance.getParameters() );

        int dim = 3;
        instance = new CategoricalDistribution( dim );
        assertEquals( dim, instance.getInputDimensionality() );
        assertTrue( instance.getParameters().norm1() > 0.0 );

        Vector p = VectorFactory.getDefault().createVector(dim, RANDOM.nextDouble());
        instance = new CategoricalDistribution(p);
        assertSame( p, instance.getParameters() );

        CategoricalDistribution i2 = new CategoricalDistribution( instance );
        assertNotSame( i2, instance );
        assertNotSame( instance.getParameters(), i2.getParameters() );
        assertEquals( instance.getParameters(), i2.getParameters() );

    }

    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PMF constructors" );

        CategoricalDistribution.PMF instance = new CategoricalDistribution.PMF();
        assertEquals( CategoricalDistribution.DEFAULT_NUM_CLASSES, instance.getInputDimensionality() );
        assertNotNull( instance.getParameters() );

        int dim = 3;
        instance = new CategoricalDistribution.PMF( dim );
        assertEquals( dim, instance.getInputDimensionality() );
        assertTrue( instance.getParameters().norm1() > 0.0 );

        Vector p = VectorFactory.getDefault().createVector(dim, RANDOM.nextDouble());
        instance = new CategoricalDistribution.PMF(p);
        assertSame( p, instance.getParameters() );

        CategoricalDistribution.PMF i2 = new CategoricalDistribution.PMF( instance );
        assertNotSame( i2, instance );
        assertNotSame( instance.getParameters(), i2.getParameters() );
        assertEquals( instance.getParameters(), i2.getParameters() );
    }


    /**
     * Test of getParameters method, of class CategoricalDistribution.
     */
    @Test
    public void testGetParameters()
    {
        System.out.println("getParameters");
        Vector p = VectorFactory.getDefault().createVector(3,RANDOM.nextDouble());
        CategoricalDistribution instance = new CategoricalDistribution(p);
        assertSame( p, instance.getParameters() );
    }

    /**
     * Test of setParameters method, of class CategoricalDistribution.
     */
    @Test
    public void testSetParameters()
    {
        System.out.println("setParameters");
        CategoricalDistribution instance = this.createInstance();
        Vector p = instance.getParameters().clone();
        instance.setParameters(p);
        assertSame( p, instance.getParameters() );
        Vector scale = p.scale(-1.0);
        try
        {
            instance.setParameters(scale);
            fail( "Elements must be positive" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        Vector p1 = VectorFactory.getDefault().createVector(1,1.0);
        try
        {
            instance.setParameters(p1);
            fail( "dim must be > 1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    @Override
    public CategoricalDistribution createInstance()
    {
        int dim = 3;
        DirichletDistribution d = new DirichletDistribution( dim );
        return new CategoricalDistribution( d.sample(RANDOM) );
    }

    @Override
    public void testKnownGetDomain()
    {

        int dim = 3;
        CategoricalDistribution instance = new CategoricalDistribution( dim );
        ArrayList<Vector> domain = instance.getDomain();
        assertEquals( dim, domain.size() );
        for( int i = 0; i < dim; i++ )
        {
            Vector v = VectorFactory.getDefault().createVector(dim);
            v.setElement(i, 1.0);
            assertEquals( v, domain.get(i) );
        }

    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "PMF Known Values" );

        CategoricalDistribution.PMF instance =
            this.createInstance().getProbabilityFunction();
        Vector p = instance.getParameters();
        double psum = p.norm1();
        int dim = instance.getInputDimensionality();
        for( int i = 0; i < dim; i++ )
        {
            Vector x = VectorFactory.getDefault().createVector(dim);
            x.setElement(i, 1.0);
            assertEquals( p.getElement(i)/psum, instance.evaluate(x) );
        }

        Vector x = VectorFactory.getDefault().createVector(dim+1);
        try
        {
            instance.evaluate(x);
            fail( "Vector input is wrong size!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        x = VectorFactory.getDefault().createVector(dim);
        try
        {
            instance.evaluate(x);
            fail( "input is all zeros!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        x.setElement(0, RANDOM.nextDouble() );
        try
        {
            instance.evaluate(x);
            fail( "input isn't 1.0 or 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        x.setElement(0, 1.0);
        x.setElement(1, 1.0);
        try
        {
            instance.evaluate(x);
            fail( "input has more than one 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known ConvertToVector" );

        CategoricalDistribution instance = this.createInstance();
        Vector p = instance.getParameters();
        assertEquals( p, instance.convertToVector() );
        assertNotSame( p, instance.convertToVector() );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "known values" );
        this.testProbabilityFunctionKnownValues();
    }

}