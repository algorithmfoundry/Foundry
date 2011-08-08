/*
 * File:                AbstractEigenDecompositionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 17, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.decomposition;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AbstractEigenDecomposition
 *
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments={
        "Added proper file header.",
        "Added some documentation.",
        "Otherwise looks fine."
    }
)
public class AbstractEigenDecompositionTest
    extends TestCase
{

    /** 
     * Creates a new instance of AbstractEigenDecompositionTest.
     * @param testName 
     */
    public AbstractEigenDecompositionTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Returns the test.
     * @return 
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( AbstractEigenDecompositionTest.class );

        return suite;
    }

    /**
     * Test of getLogDeterminant method, of class 
     * gov.sandia.isrc.math.matrix.AbstractEigenDecomposition.
     */
    public void testGetLogDeterminant()
    {
        System.out.println( "getLogDeterminant" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( -7.0, -8.0, 9.0 );
        DenseMatrix m1 = DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors( column1, column2, column3 );

        AbstractEigenDecomposition evd = EigenDecompositionRightMTJ.create( m1 );

        ComplexNumber r1 = evd.getLogDeterminant();

        ComplexNumber e1 = new ComplexNumber( 3.9890, 3.1416 );
        assertTrue( e1.equals( r1, 0.0001 ) );

    }

}
