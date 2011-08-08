/*
 * File:                StudentizedRangeDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 10, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormUnivariateDistributionTestHarness;
import static org.junit.Assert.*;

/**
 * Tests for class StudentizedRangeDistributionTest.
 * @author krdixon
 */
public class StudentizedRangeDistributionTest
    extends ClosedFormUnivariateDistributionTestHarness<Double>
{

    /**
     * Default Constructor
     */
    public StudentizedRangeDistributionTest()
    {
        super( "StudentizedRangeDistributionTest" );
    }

    /**
     * Tests the constructors of class ${name}.
     */
    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        StudentizedRangeDistribution instance = new StudentizedRangeDistribution();
        assertEquals( StudentizedRangeDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom(), TOLERANCE );
        assertEquals( StudentizedRangeDistribution.DEFAULT_TREATMENT_COUNT, instance.getTreatmentCount() );

        double dof = RANDOM.nextDouble() + 1.0;
        int num = RANDOM.nextInt(100) + 1;
        instance = new StudentizedRangeDistribution( num, dof );
        assertEquals( dof, instance.getDegreesOfFreedom(), TOLERANCE );
        assertEquals( num, instance.getTreatmentCount() );

        StudentizedRangeDistribution i2 = new StudentizedRangeDistribution( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getDegreesOfFreedom(), i2.getDegreesOfFreedom(), TOLERANCE );
        assertEquals( instance.getTreatmentCount(), i2.getTreatmentCount() );
        
        try
        {
            instance = new StudentizedRangeDistribution( 1, dof );
            fail( "numTreatments must be > 1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance = new StudentizedRangeDistribution( num, 0.0 );
            fail( "DOF must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }


    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        StudentizedRangeDistribution.CDF instance = new StudentizedRangeDistribution.CDF();
        assertEquals( StudentizedRangeDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom(), TOLERANCE );
        assertEquals( StudentizedRangeDistribution.DEFAULT_TREATMENT_COUNT, instance.getTreatmentCount() );

        double dof = RANDOM.nextDouble() + 1.0;
        int num = RANDOM.nextInt(100) + 1;
        instance = new StudentizedRangeDistribution.CDF( num, dof );
        assertEquals( dof, instance.getDegreesOfFreedom(), TOLERANCE );
        assertEquals( num, instance.getTreatmentCount() );

        StudentizedRangeDistribution.CDF i2 = new StudentizedRangeDistribution.CDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getDegreesOfFreedom(), i2.getDegreesOfFreedom(), TOLERANCE );
        assertEquals( instance.getTreatmentCount(), i2.getTreatmentCount() );


    }

    @Override
    public StudentizedRangeDistribution createInstance()
    {
        return new StudentizedRangeDistribution( 5, 20.0 );
    }

    @Override
    public void testCDFBoundaryConditions()
    {
        double temp = TOLERANCE;
        TOLERANCE = 1e-3;
        super.testCDFBoundaryConditions();
        TOLERANCE = temp;
    }

    @Override
    public void testCDFKnownValues()
    {

        System.out.println( "CDF Known Values" );
        
        double result;
        double expected;

        TOLERANCE = 1e-3;

        //http://cse.niaes.affrc.go.jp/miwa/probcalc/s-range/srng_tbl.html
        StudentizedRangeDistribution.CDF cdf = new StudentizedRangeDistribution.CDF( 20, 20 );
        expected = 1.0-0.10;
        result = cdf.evaluate(5.205);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 1.0-0.05;
        result = cdf.evaluate(5.714);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 1.0-0.01;
        result = cdf.evaluate(6.823);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );

        cdf = new StudentizedRangeDistribution.CDF( 10, Double.POSITIVE_INFINITY );
        expected = 1.0-0.1;
        result = cdf.evaluate(4.129);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 1.0-0.05;
        result = cdf.evaluate(4.474);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 1.0-0.01;
        result = cdf.evaluate(5.157);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
    }

    public void testCDFInverseKnownValues()
    {
        System.out.println( "CDF Inverse Known Values" );

        double result;
        double expected;

        TOLERANCE = 1e-1;

        StudentizedRangeDistribution.CDF cdf = new StudentizedRangeDistribution.CDF( 2, Double.POSITIVE_INFINITY );

        //http://cse.niaes.affrc.go.jp/miwa/probcalc/s-range/srng_tbl.html
        expected = 1.960;
        result = cdf.inverse(1.0-0.05)/Math.sqrt(2);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 1.645;
        result = cdf.inverse(1.0-0.10)/Math.sqrt(2);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );

        expected = 2.728;
        cdf = new StudentizedRangeDistribution.CDF( 5, Double.POSITIVE_INFINITY );
        result = cdf.inverse(1.0-0.05)/Math.sqrt(2);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 2.459;
        result = cdf.inverse(1.0-0.10)/Math.sqrt(2);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );


        expected = 3.164;
        cdf = new StudentizedRangeDistribution.CDF( 10, Double.POSITIVE_INFINITY );
        result = cdf.inverse(1.0-0.05)/Math.sqrt(2);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );
        expected = 2.920;
        result = cdf.inverse(1.0-0.10)/Math.sqrt(2);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );

        expected = 5.205;
        cdf = new StudentizedRangeDistribution.CDF( 20, 20 );
        result = cdf.inverse(1.0-0.10);
        System.out.println( "Expected: " + expected + ", Result: " + result );
        assertEquals( expected, result, TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convert to vector" );

        double dof = RANDOM.nextDouble() + 1.0;
        int num = RANDOM.nextInt(100) + 2;
        StudentizedRangeDistribution instance =
            new StudentizedRangeDistribution( num, dof );
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( num, p.getElement(0), TOLERANCE );
        assertEquals( dof, p.getElement(1), TOLERANCE );
    }

    @Override
    public void testCDFConvertFromVector()
    {
//        super.testCDFConvertFromVector();
    }

}
