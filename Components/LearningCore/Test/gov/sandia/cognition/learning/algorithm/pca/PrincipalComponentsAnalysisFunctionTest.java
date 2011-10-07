/*
 * File:                PrincipalComponentsAnalysisFunctionTest.java
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

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class PrincipalComponentsAnalysisFunctionTest extends TestCase
{
    /** The random number generator for the tests. */
    private Random random = new Random(1);
    
    public PrincipalComponentsAnalysisFunctionTest(String testName)
    {
        super(testName);
    }
    
    public PrincipalComponentsAnalysisFunction createInstance()
    {
        
        int N = random.nextInt(10) + 2;
        int M = N-1;
        double r = 1;
        Vector mean = VectorFactory.getDefault().createUniformRandom( N, -r, r, random );
        Matrix C = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, random );
        return new PrincipalComponentsAnalysisFunction(
            mean, new MultivariateDiscriminant( C ) );
    }
    
    /**
     * Test of clone method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        PrincipalComponentsAnalysisFunction f0 = new PrincipalComponentsAnalysisFunction();
        assertNull( f0.getDimensionReducer() );
        assertNull( f0.getMean() );
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        PrincipalComponentsAnalysisFunction clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getMean(), clone.getMean() );
        assertEquals( instance.getMean(), clone.getMean() );
        assertNotSame( instance.getDimensionReducer(), clone.getDimensionReducer() );
        assertEquals( instance.getDimensionReducer().getDiscriminant(), clone.getDimensionReducer().getDiscriminant() );
        
    }
    
    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        for( int i = 0; i < 100; i++ )
        {
            PrincipalComponentsAnalysisFunction instance = this.createInstance();
            Vector x = VectorFactory.getDefault().createUniformRandom( instance.getInputDimensionality(), -1, 1, random );
            
            Vector y = instance.getDimensionReducer().evaluate( x.minus( instance.getMean() ) );
            Vector yhat = instance.evaluate( x );
            if( y.equals( yhat, 1e-5 ) == false )
            {
                assertEquals( y, yhat );
            }
        }

    }
    
    /**
     * Test of getInputDimensionality method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        assertEquals( instance.getMean().getDimensionality(), instance.getInputDimensionality() );

    }
    
    /**
     * Test of getOutputDimensionality method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testGetOutputDimensionality()
    {
        System.out.println("getOutputDimensionality");
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        assertEquals( instance.getDimensionReducer().getDiscriminant().getNumRows(), instance.getOutputDimensionality() );

    }
    
    /**
     * Test of getMean method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        Vector mean = instance.getMean();
        assertNotNull( mean );
    }
    
    /**
     * Test of setMean method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testSetMean()
    {
        
        System.out.println("setMean");
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        Vector mean = instance.getMean();
        assertNotNull( mean );
        
        instance.setMean( null );
        assertNull( instance.getMean() );
        
        instance.setMean( mean );
        assertSame( mean, instance.getMean() );

    }
    
    /**
     * Test of getDimensionReducer method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testGetDimensionReducer()
    {
        System.out.println("getDimensionReducer");
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        MultivariateDiscriminant m = instance.getDimensionReducer();
        assertNotNull( m );    
    }
    
    /**
     * Test of setDimensionReducer method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysisFunction.
     */
    public void testSetDimensionReducer()
    {
        System.out.println("setDimensionReducer");
        
        PrincipalComponentsAnalysisFunction instance = this.createInstance();
        MultivariateDiscriminant m = instance.getDimensionReducer();
        assertNotNull( m );
        
        instance.setDimensionReducer( null );
        assertNull( instance.getDimensionReducer() );
        
        instance.setDimensionReducer( m );
        assertSame( m, instance.getDimensionReducer() );

    }
    
}
