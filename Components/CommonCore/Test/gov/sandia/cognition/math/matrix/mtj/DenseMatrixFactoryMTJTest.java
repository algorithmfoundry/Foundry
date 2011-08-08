/*
 * File:                DenseMatrixFactoryMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.MatrixFactoryTestHarness;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit tests for DenseMatrixFactoryMTJTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class DenseMatrixFactoryMTJTest
    extends MatrixFactoryTestHarness
{
    
    public DenseMatrixFactoryMTJTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DenseMatrixFactoryMTJTest.class);
        
        return suite;
    }    

    public DenseMatrixFactoryMTJ createFactory()
    {
        return DenseMatrixFactoryMTJ.INSTANCE;
    }

    @Override
    public DenseMatrix createRandomMatrix()
    {
        int M = random.nextInt(10) + 1;
        int N = random.nextInt(10) + 1;
        return this.createFactory().createUniformRandom(M, N, -RANGE, RANGE, random);
    }

    public void testCreateWrapper()
    {
        System.out.println( "createWrapper" );

        DenseMatrixFactoryMTJ factory = this.createFactory();

        DenseMatrix original = this.createRandomMatrix();

        DenseMatrix m = factory.createWrapper(
            new no.uib.cipr.matrix.DenseMatrix( original.getInternalMatrix() ) );
        assertNotNull( original );
        assertNotNull( m );
        assertEquals( original, m );

    }

}
