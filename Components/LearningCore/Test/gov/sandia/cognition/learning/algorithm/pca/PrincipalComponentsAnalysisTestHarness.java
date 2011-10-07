/*
 * File:                PrincipalComponentsAnalysisTest.java
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

import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.decomposition.SingularValueDecomposition;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public abstract class PrincipalComponentsAnalysisTestHarness extends TestCase
{

    public PrincipalComponentsAnalysisTestHarness( String testName )
    {
        super( testName );
    }

    /** The random number generator for the tests. */
    Random random = new Random(4);

    public int INPUT_DIM = random.nextInt( 3 ) + 3;

    public int OUTPUT_DIM = random.nextInt( 2 ) + 1;

    /**
     * Creates a PCA
     * @return PCA
     */
    abstract public PrincipalComponentsAnalysis createPCAInstance();

    public void testPCAClone()
    {
        System.out.println( "PCA.clone" );

        PrincipalComponentsAnalysis instance = this.createPCAInstance();
        PrincipalComponentsAnalysis clone =
            (PrincipalComponentsAnalysis) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getNumComponents(), clone.getNumComponents() );

    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.pca.PrincipalComponentsAnalysis.
     *
     * The example data is based on: http://www.kernel-machines.org/code/kpca_toy.m
     */
    public void testPCALearn()
    {
        System.out.println( "PCA.learn" );

        int num = random.nextInt( 100 ) + 10;
        ArrayList<Vector> data = new ArrayList<Vector>( num );
        final double r1 = random.nextDouble();
        final double r2 = r1 / random.nextDouble();
        for (int i = 0; i < num; i++)
        {
            data.add( VectorFactory.getDefault().createUniformRandom( INPUT_DIM, r1, r2, random ) );
        }

        Vector mean = MultivariateStatisticsUtil.computeMean( data );

        DenseMatrix X = DenseMatrixFactoryMTJ.INSTANCE.createMatrix( INPUT_DIM, num );
        for (int n = 0; n < num; n++)
        {
            X.setColumn( n, data.get( n ).minus( mean ) );
        }

        final ArrayList<Vector> dataCopy = ObjectUtil.cloneSmartElementsAsArrayList(data);

        long startsvd = System.currentTimeMillis();
        SingularValueDecomposition svd = SingularValueDecompositionMTJ.create( X );
        long stopsvd = System.currentTimeMillis();

        long start = System.currentTimeMillis();
        PrincipalComponentsAnalysis instance = this.createPCAInstance();
        PrincipalComponentsAnalysisFunction f = instance.learn( data );
        long stop = System.currentTimeMillis();

        assertEquals(dataCopy, data);

        System.out.println( "Uhat:\n" + f.getDimensionReducer().getDiscriminant().transpose() );
        System.out.println( "U:\n" + svd.getU() );

        System.out.println( "Time taken: SVD = " + (stopsvd - startsvd) + ", PCA = " + (stop - start) );

        // Make sure the PCA algorithm subtracted off the sample mean
        if (mean.equals( f.getMean(), 1e-5 ) == false)
        {
            assertEquals( mean, f.getMean() );
        }

        assertEquals( OUTPUT_DIM, instance.getNumComponents() );
        assertEquals( instance.getNumComponents(), f.getOutputDimensionality() );
        assertEquals( INPUT_DIM, f.getInputDimensionality() );

        if (mean.equals( f.getMean(), 1e-5 ) == false)
        {
            assertEquals( mean, f.getMean() );
        }

        double absnorm = 0.0;
        int nc = instance.getNumComponents() * INPUT_DIM;
        for (int i = 0; i < instance.getNumComponents(); i++)
        {
            Vector uihat = f.getDimensionReducer().getDiscriminant().getRow( i );
            for (int j = 0; j < i; j++)
            {
                Vector ujhat = f.getDimensionReducer().getDiscriminant().getRow( j );
                assertEquals( "Dot product between " + i + " and " + j + " is too large!", 0.0, uihat.dotProduct( ujhat ), 1e-2 );
            }
            assertEquals( 1.0, uihat.norm2(), 1e-5 );
            Vector ui = svd.getU().getColumn( i );
            absnorm += Math.min( ui.minus( uihat ).norm2(), ui.minus( uihat.scale( -1 ) ).norm2() );
        }
        absnorm /= nc;

        System.out.println( "U 1-norm: " + absnorm );
        assertEquals( 0.0, absnorm, 1e-1 );

    }

}
