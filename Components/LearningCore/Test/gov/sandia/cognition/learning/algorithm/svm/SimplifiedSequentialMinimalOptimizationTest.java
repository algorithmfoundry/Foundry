/*
 * File:                SimplifiedSequentialMinimalOptimizationTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 *
 * Copyright July 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class SimplifiedSequentialMinimalOptimizationTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public SimplifiedSequentialMinimalOptimizationTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Test of learn method, of class SimplifiedSequentialMinimalOptimization.
     */
    public void testLearn()
    {
        final Random random = new Random();
        int d = 300;
        int pointsToGenerate = 100;
        final ArrayList<InputOutputPair<Vector, Boolean>> data =
            new ArrayList<InputOutputPair<Vector, Boolean>>(pointsToGenerate);

        final VectorFactory<?> factory = VectorFactory.getDenseDefault();

        Vector target = VectorFactory.getDenseDefault().createUniformRandom(d, -1.0, 1.0, random);
        while (data.size() < pointsToGenerate)
        {
            Vector input = VectorFactory.getSparseDefault().createVector(d, 0.0);

            for (int i = 0; i < d / 10; i++)
            {
                int index = random.nextInt(d);
                input.setElement(index, 1.0);
            }

            double dotProduct = input.dotProduct(target);
            if (dotProduct < -1.0)
            {
                data.add(DefaultInputOutputPair.create(input, false));
            }
            else if (dotProduct > +1.0)
            {
                data.add(DefaultInputOutputPair.create(input, true));
            }
            // else - The dot product wsa between -1.0 and +1.0, try again.
        }

        Vector2[] positives = new Vector2[]{

//            new Vector2( 1.00, 1.00 ),
//            new Vector2( 1.50, 0.00 ),
//            new Vector2( 1.00, 2.00 ),

            new Vector2( 1.00, 1.00 ),
            new Vector2( 1.00, 3.00 ),
            new Vector2( 0.25, 4.00 ),
            new Vector2( 2.00, 1.00 ),
            new Vector2( 5.00, -3.00 )

        };

        Vector2[] negatives = new Vector2[]{

//            new Vector2( -1.00, -1.00 ),
//            new Vector2( -2.00, -2.00 ),

            new Vector2( 2.00, 3.00 ),
            new Vector2( 2.00, 4.00 ),
            new Vector2( 3.00, 2.00 ),
            new Vector2( 4.25, 3.75 ),
            new Vector2( 4.00, 7.00 ),
            new Vector2( 7.00, 4.00 )

        };

        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        for (Vector2 example : positives)
        {
            examples.add( new DefaultInputOutputPair<Vector2, Boolean>( example, true ) );
        }

        for (Vector2 example : negatives)
        {
            examples.add( new DefaultInputOutputPair<Vector2, Boolean>( example, false ) );
        }

        SimplifiedSequentialMinimalOptimization<Vector> instance =
            new SimplifiedSequentialMinimalOptimization<Vector>();
        instance.setKernel(new LinearKernel());
        instance.setRandom(random);
        instance.setMaxIterations(1000);
        instance.setMaxPenalty(100.0);


        final KernelBinaryCategorizer<Vector> result = instance.learn(data);
        assertSame(result, instance.getResult());

        System.out.println("Result " + result);
        for (WeightedValue<?> support : result.getExamples())
        {
            System.out.println("    " + support.getWeight() + " " + support.getValue());
        }
        System.out.println("Bias: " + result.getBias());


//        for (Vector2 example : positives)
//        {
//            assertTrue( result.evaluate( example ) );
//        }
//
//        for (Vector2 example : negatives)
//        {
//            assertFalse( result.evaluate( example ) );
//        }


        for (InputOutputPair<Vector, Boolean> example : data)
        {
//            System.out.println("" + example.getInput() + " -> " + example.getOutput());
            assertEquals( example.getOutput(), result.evaluate( example.getInput() ) );
        }
    }

    /**
     * Test of getResult method, of class SimplifiedSequentialMinimalOptimization.
     */
    public void testGetResult()
    {
        // Tested by testLearn.
    }

    /**
     * Test of getRandom method, of class SimplifiedSequentialMinimalOptimization.
     */
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class SimplifiedSequentialMinimalOptimization.
     */
    public void testSetRandom()
    {
        SimplifiedSequentialMinimalOptimization<String> instance =
            new SimplifiedSequentialMinimalOptimization<String>();
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

}
