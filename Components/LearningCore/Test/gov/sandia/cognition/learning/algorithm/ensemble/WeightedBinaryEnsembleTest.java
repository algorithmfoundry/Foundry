/*
 * File:                WeightedBinaryEnsembleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.BinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for WeightedBinaryEnsembleTest
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class WeightedBinaryEnsembleTest
    extends TestCase
{

    public WeightedBinaryEnsembleTest(
        String testName )
    {
        super( testName );
    }

    public void testConstants()
    {
        assertEquals( 1.0, WeightedBinaryEnsemble.DEFAULT_WEIGHT );
    }

    public void testConstructors()
    {
        WeightedBinaryEnsemble<Vector> instance = new WeightedBinaryEnsemble<Vector>();
        assertNotNull( instance.getMembers() );
        assertTrue( instance.getMembers().isEmpty() );

        ArrayList<WeightedValue<? extends Evaluator<? super Vector,? extends Boolean>>> members =
            new ArrayList<WeightedValue<? extends Evaluator<? super Vector,? extends Boolean>>>();
        members.add( new DefaultWeightedValue<KernelBinaryCategorizer<Vector>>(
            new KernelBinaryCategorizer<Vector>( new PolynomialKernel( 2 ) ),
            Math.random() ) );

        instance = new WeightedBinaryEnsemble<Vector>( members );
        assertSame( members, instance.getMembers() );
    }

    /**
     * Test of add method, of class gov.sandia.cognition.learning.ensemble.WeightedBinaryEnsemble.
     */
    public void testAdd()
    {
        WeightedBinaryEnsemble<Vector> instance = new WeightedBinaryEnsemble<Vector>();
        KernelBinaryCategorizer<Vector> member =
            new KernelBinaryCategorizer<Vector>( new PolynomialKernel( 2 ) );
        instance.add( member );
        assertEquals( 1, instance.getMembers().size() );
        assertSame( member, instance.getMembers().get( 0 ).getValue() );
        assertEquals( WeightedBinaryEnsemble.DEFAULT_WEIGHT,
            instance.getMembers().get( 0 ).getWeight() );

        member =
            new KernelBinaryCategorizer<Vector>( new PolynomialKernel( 4 ) );
        double weight = Math.random();
        instance.add( member, weight );
        assertEquals( 2, instance.getMembers().size() );
        assertSame( member, instance.getMembers().get( 1 ).getValue() );
        assertEquals( weight, instance.getMembers().get( 1 ).getWeight() );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.ensemble.WeightedBinaryEnsemble.
     */
    public void testEvaluate()
    {
        WeightedBinaryEnsemble<Vector> instance = new WeightedBinaryEnsemble<Vector>();

        assertTrue( instance.evaluate( new Vector3( Math.random(), Math.random(), Math.random() ) ) );

        LinearBinaryCategorizer categorizer = new LinearBinaryCategorizer(
            new Vector3( Math.random(), Math.random(), Math.random() ), Math.random() );
        instance.add( categorizer );

        int count = 10;

        for (int i = 0; i < count; i++)
        {
            Vector input = new Vector3( Math.random(), Math.random(), Math.random() );
            Boolean expected = categorizer.evaluate( input );

            assertEquals( expected, instance.evaluate( input ) );
        }

        instance.add( categorizer, -3.0 );
        for (int i = 0; i < count; i++)
        {
            Vector input = new Vector3( Math.random(), Math.random(), Math.random() );
            Boolean expected = !categorizer.evaluate( input );

            assertEquals( expected, instance.evaluate( input ) );
        }
    }

    /**
     * Test of evaluateAsDouble method, of class gov.sandia.cognition.learning.ensemble.WeightedBinaryEnsemble.
     */
    public void testEvaluateAsDouble()
    {
        WeightedBinaryEnsemble<Vector> instance = new WeightedBinaryEnsemble<Vector>();


        assertEquals( 0.0, instance.evaluateAsDouble( new Vector3( Math.random(), Math.random(), Math.random() ) ) );

        LinearBinaryCategorizer categorizer = new LinearBinaryCategorizer(
            new Vector3( Math.random(), Math.random(), Math.random() ), Math.random() );
        instance.add( categorizer );

        int count = 10;

        for (int i = 0; i < count; i++)
        {
            Vector input = new Vector3( Math.random(), Math.random(), Math.random() );
            double expected = categorizer.evaluate( input ) ? +1.0 : -1.0;

            assertEquals( expected, instance.evaluateAsDouble( input ) );
        }

        instance.add( categorizer, -3.0 );
        for (int i = 0; i < count; i++)
        {
            Vector input = new Vector3( Math.random(), Math.random(), Math.random() );
            double expected = !categorizer.evaluate( input ) ? +0.5 : -0.5;

            assertEquals( expected, instance.evaluateAsDouble( input ) );
        }
    }

    /**
     * Test of getMembers method, of class gov.sandia.cognition.learning.ensemble.WeightedBinaryEnsemble.
     */
    public void testGetMembers()
    {
        this.testSetMembers();
    }

    /**
     * Test of setMembers method, of class gov.sandia.cognition.learning.ensemble.WeightedBinaryEnsemble.
     */
    public void testSetMembers()
    {
        WeightedBinaryEnsemble<Vector> instance = new WeightedBinaryEnsemble<Vector>();

        ArrayList<WeightedValue<? extends Evaluator<? super Vector,? extends Boolean>>> members =
            new ArrayList<WeightedValue<? extends Evaluator<? super Vector,? extends Boolean>>>();
        members.add( new DefaultWeightedValue<BinaryCategorizer<Vector>>(
            new KernelBinaryCategorizer<Vector>( new PolynomialKernel( 2 ) ),
            Math.random() ) );
        instance.setMembers( members );
        assertSame( members, instance.getMembers() );

        instance.setMembers( null );
        assertNull( instance.getMembers() );
    }

}
