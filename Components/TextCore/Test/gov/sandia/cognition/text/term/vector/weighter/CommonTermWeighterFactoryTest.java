/*
 * File:                CommonTermWeighterFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 30, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.text.term.vector.weighter.global.DominanceGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.EntropyGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.InverseDocumentFrequencyGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.local.LogLocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.local.TermFrequencyLocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.normalize.UnitTermWeightNormalizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class CommonTermWeighterFactory.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class CommonTermWeighterFactoryTest
{
    /**
     * Creates a new test.
     */
    public CommonTermWeighterFactoryTest()
    {
    }

    /**
     * Test of createTFWeighter method, of class CommonTermWeighterFactory.
     */
    @Test
    public void testCreateTFWeighter()
    {
        CompositeLocalGlobalTermWeighter result =
            CommonTermWeighterFactory.createTFWeighter();
        assertNotNull(result);
        assertTrue(result.getLocalWeighter() instanceof TermFrequencyLocalTermWeighter);
        assertNull(result.getGlobalWeighter());
        assertNull(result.getNormalizer());
        assertNotSame(result, CommonTermWeighterFactory.createTFWeighter());

        // Now test the computations.
        double epsilon = 0.0001;
        Vector input = new Vector3(7.0, 0.0, 1.0);
        Vector expected = new Vector3(7.0 / 8.0, 0.0, 1.0 / 8.0);
        Vector actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));


        input = new Vector3(1.0, 0.0, 0.0);
        expected = new Vector3(1.0, 0.0, 0.0);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        
        input = new Vector3(0.25, 0.0, 0.75);
        expected = new Vector3(0.25, 0.0, 0.75);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        input = new Vector3();
        expected = new Vector3();
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));
    }

    /**
     * Test of createTFIDFWeighter method, of class CommonTermWeighterFactory.
     */
    @Test
    public void testCreateTFIDFWeighter()
    {
        CompositeLocalGlobalTermWeighter result =
            CommonTermWeighterFactory.createTFIDFWeighter();
        assertNotNull(result);
        assertTrue(result.getLocalWeighter() instanceof TermFrequencyLocalTermWeighter);
        assertTrue(result.getGlobalWeighter() instanceof InverseDocumentFrequencyGlobalTermWeighter);
        assertNull(result.getNormalizer());
        assertNotSame(result, CommonTermWeighterFactory.createTFIDFWeighter());
        assertNotSame(result.getGlobalWeighter(), CommonTermWeighterFactory.createTFIDFWeighter().getGlobalWeighter());

        // Now test the computations.
        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(4.0, 0.0, 0.0));
        result.getGlobalWeighter().add(new Vector3(9.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.0001;
        Vector input = new Vector3(7.0, 0.0, 1.0);
        Vector expected = new Vector3(0.0, 0.0, 0.0359);
        Vector actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));


        result.getGlobalWeighter().add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.1952, 0.0, 0.0638);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        result.getGlobalWeighter().remove(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.0, 0.0, 0.0359);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));
    }

/**
     * Test of createTFIDFWeighter method, of class CommonTermWeighterFactory.
     */
    @Test
    public void testCreateTFIDFWeighterWithUnitNormalization()
    {
        CompositeLocalGlobalTermWeighter result =
            CommonTermWeighterFactory.createTFIDFWeighterWithUnitNormalization();
        assertNotNull(result);
        assertTrue(result.getLocalWeighter() instanceof TermFrequencyLocalTermWeighter);
        assertTrue(result.getGlobalWeighter() instanceof InverseDocumentFrequencyGlobalTermWeighter);
        assertTrue(result.getNormalizer() instanceof UnitTermWeightNormalizer);
        assertNotSame(result, CommonTermWeighterFactory.createTFIDFWeighterWithUnitNormalization());
        assertNotSame(result.getGlobalWeighter(), CommonTermWeighterFactory.createTFIDFWeighterWithUnitNormalization().getGlobalWeighter());

        // Now test the computations.
        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(4.0, 0.0, 0.0));
        result.getGlobalWeighter().add(new Vector3(9.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.001;
        Vector input = new Vector3(7.0, 0.0, 1.0);
        Vector expected = new Vector3(0.0, 0.0, 0.0359).unitVector();
        Vector actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));


        result.getGlobalWeighter().add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.1952, 0.0, 0.0638).unitVector();
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        result.getGlobalWeighter().remove(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.0, 0.0, 0.0359).unitVector();
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));
    }

    /**
     * Test of createLogEntropyWeighter method, of class CommonTermWeighterFactory.
     */
    @Test
    public void testCreateLogEntropyWeighter()
    {
        CompositeLocalGlobalTermWeighter result =
            CommonTermWeighterFactory.createLogEntropyWeighter();
        assertNotNull(result);
        assertTrue(result.getLocalWeighter() instanceof LogLocalTermWeighter);
        assertTrue(result.getGlobalWeighter() instanceof EntropyGlobalTermWeighter);
        assertNull(result.getNormalizer());
        assertNotSame(result, CommonTermWeighterFactory.createLogEntropyWeighter());
        assertNotSame(result.getGlobalWeighter(), CommonTermWeighterFactory.createLogEntropyWeighter().getGlobalWeighter());


        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(4.0, 0.0, 0.0));
        result.getGlobalWeighter().add(new Vector3(9.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.0001;
        Vector input = new Vector3(7.0, 0.0, 1.0);
        Vector expected = new Vector3(0.1821, 0.0, 0.1732);
        Vector actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        result.getGlobalWeighter().add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.4452, 0.0, 0.2453);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        result.getGlobalWeighter().remove(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(0.1821, 0.0, 0.1732);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));
    }

    /**
     * Test of createLogDominanceWeighter method, of class CommonTermWeighterFactory.
     */
    @Test
    public void testCreateLogDominanceWeighter()
    {
        CompositeLocalGlobalTermWeighter result =
            CommonTermWeighterFactory.createLogDominanceWeighter();
        assertNotNull(result);
        assertTrue(result.getLocalWeighter() instanceof LogLocalTermWeighter);
        assertTrue(result.getGlobalWeighter() instanceof DominanceGlobalTermWeighter);
        assertNull(result.getNormalizer());
        assertNotSame(result, CommonTermWeighterFactory.createLogDominanceWeighter());
        assertNotSame(result.getGlobalWeighter(), CommonTermWeighterFactory.createLogDominanceWeighter().getGlobalWeighter());


        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(4.0, 0.0, 0.0));
        result.getGlobalWeighter().add(new Vector3(9.0, 0.0, 1.0));
        result.getGlobalWeighter().add(new Vector3(3.0, 0.0, 2.0));

        double epsilon = 0.0001;
        Vector input = new Vector3(7.0, 0.0, 1.0);
        Vector expected = new Vector3(1.8417, 0.0, 0.4901);
        Vector actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        result.getGlobalWeighter().add(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(1.4733, 0.0, 0.3921);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));

        result.getGlobalWeighter().remove(new Vector3(0.0, 1.0, 0.0));
        expected = new Vector3(1.8417, 0.0, 0.4901);
        actual = result.evaluate(input);
        assertTrue(expected.equals(actual, epsilon));
    }

}