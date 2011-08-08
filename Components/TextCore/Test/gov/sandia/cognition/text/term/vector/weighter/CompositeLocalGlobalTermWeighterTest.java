/*
 * File:                CompositeLocalGlobalTermWeighterTest.java
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
import gov.sandia.cognition.text.term.vector.weighter.global.EntropyGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.GlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.InverseDocumentFrequencyGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.local.BinaryLocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.local.LocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.local.TermFrequencyLocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.normalize.TermWeightNormalizer;
import gov.sandia.cognition.text.term.vector.weighter.normalize.UnitTermWeightNormalizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class CompositeLocalGlobalTermWeighter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class CompositeLocalGlobalTermWeighterTest
{
    /**
     * Creates a new test.
     */
    public CompositeLocalGlobalTermWeighterTest()
    {
    }

    /**
     * Test of constructors of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testConstructors()
    {
        LocalTermWeighter localWeighter = null;
        GlobalTermWeighter globalWeighter = null;
        TermWeightNormalizer normalizer = null;
        CompositeLocalGlobalTermWeighter instance =
            new CompositeLocalGlobalTermWeighter();
        assertSame(localWeighter, instance.getLocalWeighter());
        assertSame(globalWeighter, instance.getGlobalWeighter());
        assertSame(normalizer, instance.getNormalizer());

        localWeighter = new TermFrequencyLocalTermWeighter();
        globalWeighter = new InverseDocumentFrequencyGlobalTermWeighter();
        normalizer = new UnitTermWeightNormalizer();
        instance = new CompositeLocalGlobalTermWeighter(localWeighter,
            globalWeighter, normalizer);
        assertSame(localWeighter, instance.getLocalWeighter());
        assertSame(globalWeighter, instance.getGlobalWeighter());
        assertSame(normalizer, instance.getNormalizer());

    }

    /**
     * Test of evaluate method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testEvaluate()
    {
        LocalTermWeighter localWeighter = new TermFrequencyLocalTermWeighter();
        GlobalTermWeighter globalWeighter = new InverseDocumentFrequencyGlobalTermWeighter();
        TermWeightNormalizer normalizer = new UnitTermWeightNormalizer();
        CompositeLocalGlobalTermWeighter instance =
            new CompositeLocalGlobalTermWeighter();

        // Start with a totally empty composite weighter. The output should
        Vector input = new Vector3(1.0, 2.0, 0.0);
        Vector inputClone = input.clone();
        Vector expected = input.clone();
        Vector result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Add a local weighter.
        instance.setLocalWeighter(localWeighter);

        // Test local.
        expected = localWeighter.computeLocalWeights(input);
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Add the global weighter.
        instance.setGlobalWeighter(globalWeighter);

        // Test a global weighter that has no global weight yet.
        result = instance.evaluate(result);
        expected = localWeighter.computeLocalWeights(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Now see what happens if there is actually a valid global weighter.
        // Test local-global.
        globalWeighter.add(new Vector3(7.0, 0.0, 2.0));
        globalWeighter.add(new Vector3(0.0, 2.0, 2.0));
        expected = localWeighter.computeLocalWeights(input).dotTimes(globalWeighter.getGlobalWeights());
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);
        
        // Add the normalizer.
        instance.setNormalizer(normalizer);

        // Test local-global-normalize.
        expected = localWeighter.computeLocalWeights(input).dotTimes(globalWeighter.getGlobalWeights()).unitVector();
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Remove the local weighter.
        instance.setLocalWeighter(null);

        // Test global-normalize.
        expected = input.dotTimes(globalWeighter.getGlobalWeights()).unitVector();
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Remove the normalizer.
        instance.setNormalizer(null);

        // Test global.
        expected = input.dotTimes(globalWeighter.getGlobalWeights());
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Remove the global and add the normalizer.
        instance.setGlobalWeighter(null);
        instance.setNormalizer(normalizer);

        // Test normalize.
        expected = input.unitVector();
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);

        // Add the local weighter.
        instance.setLocalWeighter(localWeighter);

        // Test local-normalize.
        expected = localWeighter.computeLocalWeights(input).unitVector();
        result = instance.evaluate(input);
        assertEquals(expected, result);
        assertNotSame(input, result);
        assertEquals(inputClone, input);
    }

    /**
     * Test of getLocalWeighter method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testGetLocalWeighter()
    {
        this.testSetLocalWeighter();
    }

    /**
     * Test of setLocalWeighter method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testSetLocalWeighter()
    {
        LocalTermWeighter localWeighter = null;
        CompositeLocalGlobalTermWeighter instance =
            new CompositeLocalGlobalTermWeighter();
        assertSame(localWeighter, instance.getLocalWeighter());

        localWeighter = new TermFrequencyLocalTermWeighter();
        instance.setLocalWeighter(localWeighter);
        assertSame(localWeighter, instance.getLocalWeighter());

        localWeighter = new BinaryLocalTermWeighter();
        instance.setLocalWeighter(localWeighter);
        assertSame(localWeighter, instance.getLocalWeighter());

        localWeighter = null;
        instance.setLocalWeighter(localWeighter);
        assertSame(localWeighter, instance.getLocalWeighter());

        localWeighter = new TermFrequencyLocalTermWeighter();
        instance.setLocalWeighter(localWeighter);
        assertSame(localWeighter, instance.getLocalWeighter());
    }

    /**
     * Test of getGlobalWeighter method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testGetGlobalWeighter()
    {
        this.testSetGlobalWeighter();
    }

    /**
     * Test of setGlobalWeighter method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testSetGlobalWeighter()
    {
        GlobalTermWeighter globalWeighter = null;
        CompositeLocalGlobalTermWeighter instance =
            new CompositeLocalGlobalTermWeighter();
        assertSame(globalWeighter, instance.getGlobalWeighter());

        globalWeighter = new InverseDocumentFrequencyGlobalTermWeighter();
        instance.setGlobalWeighter(globalWeighter);
        assertSame(globalWeighter, instance.getGlobalWeighter());

        globalWeighter = new EntropyGlobalTermWeighter();
        instance.setGlobalWeighter(globalWeighter);
        assertSame(globalWeighter, instance.getGlobalWeighter());

        globalWeighter = null;
        instance.setGlobalWeighter(globalWeighter);
        assertSame(globalWeighter, instance.getGlobalWeighter());

        globalWeighter = new InverseDocumentFrequencyGlobalTermWeighter();
        instance.setGlobalWeighter(globalWeighter);
        assertSame(globalWeighter, instance.getGlobalWeighter());
    }

    /**
     * Test of getNormalizer method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testGetNormalizer()
    {
        this.testSetNormalizer();
    }

    /**
     * Test of setNormalizer method, of class CompositeLocalGlobalTermWeighter.
     */
    @Test
    public void testSetNormalizer()
    {
        TermWeightNormalizer normalizer = null;
        CompositeLocalGlobalTermWeighter instance =
            new CompositeLocalGlobalTermWeighter();
        assertSame(normalizer, instance.getNormalizer());

        normalizer = new UnitTermWeightNormalizer();
        instance.setNormalizer(normalizer);
        assertSame(normalizer, instance.getNormalizer());

        normalizer = new UnitTermWeightNormalizer();
        instance.setNormalizer(normalizer);
        assertSame(normalizer, instance.getNormalizer());

        normalizer = null;
        instance.setNormalizer(normalizer);
        assertSame(normalizer, instance.getNormalizer());

        normalizer = new UnitTermWeightNormalizer();
        instance.setNormalizer(normalizer);
        assertSame(normalizer, instance.getNormalizer());
    }

}