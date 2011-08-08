/*
 * File:                EigenDecompositionRightMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 30, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj.decomposition;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Kevin R. Dixon
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-25",
    changesNeeded=false,
    comments="Added header information"
)
public class EigenDecompositionRightMTJTest
    extends TestCase
{

    /**
     * Random number generator
     */
    protected Random random = new Random( 1 );

    /**
     * 
     * @param testName
     */
    public EigenDecompositionRightMTJTest(
        String testName )
    {
        super( testName );
    }

    /**
     * 
     * @return
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( EigenDecompositionRightMTJTest.class );

        return suite;
    }

    /**
     * 
     */
    public static void testEVD()
    {
        DenseMatrixFactoryMTJ matrixFactory = DenseMatrixFactoryMTJ.INSTANCE;
        DenseMatrix m1 = matrixFactory.createMatrix( 3, 3 );
        m1.setElement( 0, 0, 1.0 );
        m1.setElement( 0, 1, 2.0 );
        m1.setElement( 0, 2, 3.0 );

        m1.setElement( 1, 0, 5.0 );
        m1.setElement( 1, 1, 6.0 );
        m1.setElement( 1, 2, -1.0 );

        m1.setElement( 2, 0, -3.0 );
        m1.setElement( 2, 1, -4.0 );
        m1.setElement( 2, 2, -5.0 );

        EigenDecompositionRightMTJ evd = EigenDecompositionRightMTJ.create( m1 );

        ComplexNumber[] eigenvalues = new ComplexNumber[3];
        eigenvalues[0] = new ComplexNumber( 7.10348, 0.0 );
        eigenvalues[1] = new ComplexNumber( -4.61546, 0.0 );
        eigenvalues[2] = new ComplexNumber( -0.48802, 0.0 );

        ComplexNumber[] r1 = evd.getEigenValues();
        for (int i = 0; i < r1.length; i++)
        {
            System.out.println( "Expected: " + eigenvalues[i] +
                " Result: " + r1[i] );
            assertEquals( 0.0,
                eigenvalues[i].minus( r1[i] ).getMagnitude(), 0.0001 );
        }

        Vector3 column1 = new Vector3( -0.136975, -0.929929, 0.341277 );
        Vector3 column2 = new Vector3( -0.533044, 0.324671, 0.781315 );
        Vector3 column3 = new Vector3( -0.792966, 0.609132, -0.012773 );
        DenseMatrix e2 = DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors( column1, column2, column3 );

        System.out.println(
            "Real EigenVectors: " + evd.getEigenVectorsRealPart() );
        System.out.println( "Real Expected: " + e2 );

        assertTrue( e2.equals( evd.getEigenVectorsRealPart(), 0.0001 ) );

        DenseMatrix e2i = matrixFactory.createMatrix( 3, 3 );
        assertEquals( e2i, evd.getEigenVectorsImaginaryPart() );

        column1 = new Vector3( -2, -3, 0 );
        column2 = new Vector3( 6, 4, 0 );
        column3 = new Vector3( 1, 1, 0 );
        DenseMatrix m2 = DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors( column1, column2, column3 );
        evd = EigenDecompositionRightMTJ.create( m2 );

        eigenvalues[0] = new ComplexNumber( 1, -3 );
        eigenvalues[1] = new ComplexNumber( 1, 3 );
        eigenvalues[2] = new ComplexNumber( 0.0, 0.0 );
        r1 = evd.getEigenValues();

        for (int i = 0; i < r1.length; i++)
        {
            System.out.println( "Expected: " + eigenvalues[i] +
                " Result: " + r1[i] );
            assertEquals( 0.0,
                eigenvalues[i].minus( r1[i] ).getMagnitude(), 0.0001 );
        }

        column1 = new Vector3( 0.81650, 0.40825, 0.00000 );
        column2 = new Vector3( 0.81650, 0.40825, 0.00000 );
        column3 = new Vector3( 0.19518, -0.09759, 0.97590 );
        DenseMatrix e3 = DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors( column1, column2, column3 );
        System.out.println(
            "Real EigenVectors: " + evd.getEigenVectorsRealPart() );
        System.out.println( "Real Expected: " + e3 );

        assertTrue( e3.equals( evd.getEigenVectorsRealPart(), 0.0001 ) );

        column1 = new Vector3( 0, -0.40825, 0.00000 );
        column2 = new Vector3( 0, 0.40825, 0.00000 );
        column3 = new Vector3( 0, 0, 0 );
        DenseMatrix e3i = DenseMatrixFactoryMTJ.INSTANCE.copyColumnVectors( column1, column2, column3 );

        System.out.println(
            "Imag EigenVectors: " + evd.getEigenVectorsImaginaryPart() );
        System.out.println( "Imag Expected: " + e3i );

        assertTrue( e3i.equals( evd.getEigenVectorsImaginaryPart(), 0.0001 ) );

    }

    /**
     * 
     */
    public void testSquareException()
    {
        DenseMatrix matrix = DenseMatrixFactoryMTJ.INSTANCE.createUniformRandom( 10, 15, 10, 100, random );
        try
        {
            EigenDecompositionRightMTJ evd =
                EigenDecompositionRightMTJ.create( matrix );
            fail( "Must throw exception for nonsquare matrices." );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }
    }

}
