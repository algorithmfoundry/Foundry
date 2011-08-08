/*
 * File:                DefaultConfusionMatrixTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.data.DefaultTargetEstimatePair;
import java.util.LinkedList;
import java.util.Random;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.util.Pair;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultConfusionMatrix.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultConfusionMatrixTest
{

    /** Random number generator. */
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public DefaultConfusionMatrixTest()
    {
    }

    protected DefaultConfusionMatrix<String> createEmptyInstance()
    {
        return new DefaultConfusionMatrix<String>();
    }

    protected DefaultConfusionMatrix<String> createPopulatedInstance()
    {
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();
        instance.add("a", "a", random.nextDouble() * 10.0);
        instance.add("b", "b", random.nextDouble() * 10.0);
        instance.add("b", "a", random.nextDouble() * 10.0);
        instance.add("a", "b", random.nextDouble() * 10.0);
        return instance;
    }

    protected DefaultConfusionMatrix<String> createExampleInstance()
    {
        // Based on the example here: http://www.colorado.edu/geography/gcraft/notes/manerror/html/kappa.html
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();
        instance.add("a", "a", 2);
        instance.add("a", "c", 2);
        instance.add("b", "b", 1);
        instance.add("c", "a", 1);
        instance.add("c", "c", 1);
        instance.add("d", "d", 2);
        instance.add("d", "e", 1);
        instance.add("e", "e", 6);
        return instance;
    }

    /**
     * Test of constructors of class DefaultConfusionMatrix.
     */
    @Test
    public void testConstructors()
    {
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertTrue(instance.getCategories().isEmpty());

        DefaultConfusionMatrix<String> other = new DefaultConfusionMatrix<String>();
        other.add("a", "a");
        other.add("b", "a");
        instance = new DefaultConfusionMatrix<String>(other);
        assertEquals(other.getTotalCount(), instance.getTotalCount(), 0.0);

        other.add("c", "c");
        assertTrue(other.getCategories().contains("c"));
        assertFalse(instance.getCategories().contains("c"));
    }

    /**
     * Test of clone method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testClone()
    {
        DefaultConfusionMatrix<String> instance = this.createEmptyInstance();
        DefaultConfusionMatrix<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());

        instance.add("a", "a");
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, clone.getTotalCount(), 0.0);

        clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(1.0, clone.getTotalCount(), 0.0);

        instance.add("a", "b");
        assertEquals(2.0, instance.getTotalCount(), 0.0);
        assertEquals(1.0, clone.getTotalCount(), 0.0);
    }

    /**
     * Test of add method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testAdd()
    {
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();

        assertEquals(0, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(0, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(0, instance.getCount("b", "b"), 0.0);

        instance.add("a", "b");
        assertEquals(1, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(0, instance.getCount("b", "b"), 0.0);

        instance.add("b", "b");
        assertEquals(2, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(1, instance.getCount("b", "b"), 0.0);

        instance.add("b", "a", 3);
        assertEquals(5, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(3, instance.getCount("b", "a"), 0.0);
        assertEquals(1, instance.getCount("b", "b"), 0.0);
        instance.add("b", "b", 4);

        assertEquals(9, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(3, instance.getCount("b", "a"), 0.0);
        assertEquals(5, instance.getCount("b", "b"), 0.0);

        instance.clear();
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getCount("a", "b"), 0.0);
        assertEquals(0.0, instance.getCount("c", "c"), 0.0);

        instance.add("a", "b", 1.0);
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(1.0, instance.getCount("a", "b"), 0.0);
        assertEquals(0.0, instance.getCount("c", "c"), 0.0);

        instance.add("c", "c", 3.4);
        assertEquals(4.4, instance.getTotalCount(), 0.0);
        assertEquals(1.0, instance.getCount("a", "b"), 0.0);
        assertEquals(3.4, instance.getCount("c", "c"), 0.0);

        instance.add("a", null, 2.6);
        assertEquals(7.0, instance.getTotalCount(), 0.0);
        assertEquals(1.0, instance.getCount("a", "b"), 0.0);
        assertEquals(3.4, instance.getCount("c", "c"), 0.0);
        assertEquals(2.6, instance.getCount("a", null), 0.0);

        instance.add(null, "a", 0.2);
        assertEquals(7.2, instance.getTotalCount(), 0.0);
        assertEquals(1.0, instance.getCount("a", "b"), 0.0);
        assertEquals(3.4, instance.getCount("c", "c"), 0.0);
        assertEquals(2.6, instance.getCount("a", null), 0.0);
        assertEquals(0.2, instance.getCount(null, "a"), 0.0);

        instance.add(null, null, 0.2);
        assertEquals(7.4, instance.getTotalCount(), 0.0);
        assertEquals(1.0, instance.getCount("a", "b"), 0.0);
        assertEquals(3.4, instance.getCount("c", "c"), 0.0);
        assertEquals(2.6, instance.getCount("a", null), 0.0);
        assertEquals(0.2, instance.getCount(null, "a"), 0.0);
        assertEquals(0.2, instance.getCount(null, null), 0.0);
    }

    /**
     * Test of getCount method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testGetCount()
    {
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();
        assertEquals(0.0, instance.getCount("a", "a"), 0.0);
        assertEquals(0.0, instance.getCount("a", "b"), 0.0);
        assertEquals(0.0, instance.getCount("b", "a"), 0.0);
        assertEquals(0.0, instance.getCount("b", "b"), 0.0);
        assertEquals(0.0, instance.getCount("a", "c"), 0.0);
        assertEquals(0.0, instance.getCount("b", "c"), 0.0);
        assertEquals(0.0, instance.getCount("c", "a"), 0.0);
        assertEquals(0.0, instance.getCount("c", "a"), 0.0);
        assertEquals(0.0, instance.getCount("c", "c"), 0.0);
        assertEquals(0.0, instance.getCount("a", null), 0.0);
        assertEquals(0.0, instance.getCount(null, "a"), 0.0);
        assertEquals(0.0, instance.getCount(null, null), 0.0);

        instance.add("a", "a");
        assertEquals(1.0, instance.getCount("a", "a"), 0.0);
        assertEquals(0.0, instance.getCount("a", "b"), 0.0);
        assertEquals(0.0, instance.getCount("b", "a"), 0.0);
        assertEquals(0.0, instance.getCount("b", "b"), 0.0);
        assertEquals(0.0, instance.getCount("a", "c"), 0.0);
        assertEquals(0.0, instance.getCount("b", "c"), 0.0);
        assertEquals(0.0, instance.getCount("c", "a"), 0.0);
        assertEquals(0.0, instance.getCount("c", "a"), 0.0);
        assertEquals(0.0, instance.getCount("c", "c"), 0.0);
        assertEquals(0.0, instance.getCount("a", null), 0.0);
        assertEquals(0.0, instance.getCount(null, "a"), 0.0);
        assertEquals(0.0, instance.getCount(null, null), 0.0);

        instance.add("b", "a", 2.3);
        assertEquals(1.0, instance.getCount("a", "a"), 0.0);
        assertEquals(0.0, instance.getCount("a", "b"), 0.0);
        assertEquals(2.3, instance.getCount("b", "a"), 0.0);
        assertEquals(0.0, instance.getCount("b", "b"), 0.0);
        assertEquals(0.0, instance.getCount("a", "c"), 0.0);
        assertEquals(0.0, instance.getCount("b", "c"), 0.0);
        assertEquals(0.0, instance.getCount("c", "a"), 0.0);
        assertEquals(0.0, instance.getCount("c", "a"), 0.0);
        assertEquals(0.0, instance.getCount("c", "c"), 0.0);
        assertEquals(0.0, instance.getCount("a", null), 0.0);
        assertEquals(0.0, instance.getCount(null, "a"), 0.0);
        assertEquals(0.0, instance.getCount(null, null), 0.0);

        instance = this.createExampleInstance();
        assertEquals(2, instance.getCount("a", "a"), 0.0);
        assertEquals(0, instance.getCount("a", "b"), 0.0);
        assertEquals(2, instance.getCount("a", "c"), 0.0);
        assertEquals(0, instance.getCount("a", "d"), 0.0);
        assertEquals(0, instance.getCount("a", "e"), 0.0);
        assertEquals(0, instance.getCount("a", "f"), 0.0);

        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(1, instance.getCount("b", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "c"), 0.0);
        assertEquals(0, instance.getCount("b", "d"), 0.0);
        assertEquals(0, instance.getCount("b", "e"), 0.0);
        assertEquals(0, instance.getCount("b", "f"), 0.0);

        assertEquals(1, instance.getCount("c", "a"), 0.0);
        assertEquals(0, instance.getCount("c", "b"), 0.0);
        assertEquals(1, instance.getCount("c", "c"), 0.0);
        assertEquals(0, instance.getCount("c", "d"), 0.0);
        assertEquals(0, instance.getCount("c", "e"), 0.0);
        assertEquals(0, instance.getCount("c", "f"), 0.0);

        assertEquals(0, instance.getCount("d", "a"), 0.0);
        assertEquals(0, instance.getCount("d", "b"), 0.0);
        assertEquals(0, instance.getCount("d", "c"), 0.0);
        assertEquals(2, instance.getCount("d", "d"), 0.0);
        assertEquals(1, instance.getCount("d", "e"), 0.0);
        assertEquals(0, instance.getCount("d", "f"), 0.0);

        assertEquals(0, instance.getCount("e", "a"), 0.0);
        assertEquals(0, instance.getCount("e", "b"), 0.0);
        assertEquals(0, instance.getCount("e", "c"), 0.0);
        assertEquals(0, instance.getCount("e", "d"), 0.0);
        assertEquals(6, instance.getCount("e", "e"), 0.0);
        assertEquals(0, instance.getCount("e", "f"), 0.0);

        assertEquals(0, instance.getCount("f", "a"), 0.0);
        assertEquals(0, instance.getCount("f", "b"), 0.0);
        assertEquals(0, instance.getCount("f", "c"), 0.0);
        assertEquals(0, instance.getCount("f", "d"), 0.0);
        assertEquals(0, instance.getCount("f", "e"), 0.0);
        assertEquals(0, instance.getCount("f", "f"), 0.0);
    }

    /**
     * Test of getActualCount method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testGetActualCount()
    {
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();
        assertEquals(0.0, instance.getActualCount("a"), 0.0);
        assertEquals(0.0, instance.getActualCount("b"), 0.0);
        assertEquals(0.0, instance.getActualCount("c"), 0.0);
        assertEquals(0.0, instance.getActualCount(null), 0.0);

        instance.add("b", "a");
        assertEquals(0.0, instance.getActualCount("a"), 0.0);
        assertEquals(1.0, instance.getActualCount("b"), 0.0);
        assertEquals(0.0, instance.getActualCount("c"), 0.0);
        assertEquals(0.0, instance.getActualCount(null), 0.0);

        instance.add("a", "a", 2.3);
        assertEquals(2.3, instance.getActualCount("a"), 0.0);
        assertEquals(1.0, instance.getActualCount("b"), 0.0);
        assertEquals(0.0, instance.getActualCount("c"), 0.0);
        assertEquals(0.0, instance.getActualCount(null), 0.0);

        instance.add("b", "a", 2.3);
        assertEquals(2.3, instance.getActualCount("a"), 0.0);
        assertEquals(3.3, instance.getActualCount("b"), 0.0);
        assertEquals(0.0, instance.getActualCount("c"), 0.0);
        assertEquals(0.0, instance.getActualCount(null), 0.0);

        instance = this.createExampleInstance();
        assertEquals(4, instance.getActualCount("a"), 0.0);
        assertEquals(1, instance.getActualCount("b"), 0.0);
        assertEquals(2, instance.getActualCount("c"), 0.0);
        assertEquals(3, instance.getActualCount("d"), 0.0);
        assertEquals(6, instance.getActualCount("e"), 0.0);
        assertEquals(0, instance.getActualCount("f"), 0.0);
    }

    /**
     * Test of clear method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testClear()
    {
        DefaultConfusionMatrix<String> instance = new DefaultConfusionMatrix<String>();
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        instance.clear();
        assertEquals(0.0, instance.getTotalCount(), 0.0);

        instance.add("b", "a");
        instance.clear();
        assertEquals(0.0, instance.getTotalCount(), 0.0);

        instance = this.createExampleInstance();
        instance.clear();
        assertEquals(0.0, instance.getTotalCount(), 0.0);
    }

    /**
     * Test of getCategories method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testGetCategories()
    {
        DefaultConfusionMatrix<String> instance =
            new DefaultConfusionMatrix<String>();
        assertTrue(instance.getCategories().isEmpty());

        instance = this.createExampleInstance();
        assertEquals(5, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));
        assertTrue(instance.getCategories().contains("b"));
        assertTrue(instance.getCategories().contains("c"));
        assertTrue(instance.getCategories().contains("d"));
        assertTrue(instance.getCategories().contains("e"));

        instance.add("a", "f");
        assertEquals(6, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));
        assertTrue(instance.getCategories().contains("b"));
        assertTrue(instance.getCategories().contains("c"));
        assertTrue(instance.getCategories().contains("d"));
        assertTrue(instance.getCategories().contains("e"));
        assertTrue(instance.getCategories().contains("f"));

        instance.add("f", null);
        assertEquals(7, instance.getCategories().size());
        assertTrue(instance.getCategories().contains("a"));
        assertTrue(instance.getCategories().contains("b"));
        assertTrue(instance.getCategories().contains("c"));
        assertTrue(instance.getCategories().contains("d"));
        assertTrue(instance.getCategories().contains("e"));
        assertTrue(instance.getCategories().contains("f"));
        assertTrue(instance.getCategories().contains(null));
    }

    /**
     * Test of getActualCategories method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testGetActualCategories()
    {
        DefaultConfusionMatrix<String> instance =
            new DefaultConfusionMatrix<String>();
        assertTrue(instance.getActualCategories().isEmpty());

        instance = this.createExampleInstance();
        assertEquals(5, instance.getActualCategories().size());
        assertTrue(instance.getActualCategories().contains("a"));
        assertTrue(instance.getActualCategories().contains("b"));
        assertTrue(instance.getActualCategories().contains("c"));
        assertTrue(instance.getActualCategories().contains("d"));
        assertTrue(instance.getActualCategories().contains("e"));

        instance.add("a", "f");
        assertEquals(5, instance.getActualCategories().size());
        assertTrue(instance.getActualCategories().contains("a"));
        assertTrue(instance.getActualCategories().contains("b"));
        assertTrue(instance.getActualCategories().contains("c"));
        assertTrue(instance.getActualCategories().contains("d"));
        assertTrue(instance.getActualCategories().contains("e"));

        instance.add(null, "f");
        assertEquals(6, instance.getActualCategories().size());
        assertTrue(instance.getActualCategories().contains("a"));
        assertTrue(instance.getActualCategories().contains("b"));
        assertTrue(instance.getActualCategories().contains("c"));
        assertTrue(instance.getActualCategories().contains("d"));
        assertTrue(instance.getActualCategories().contains("e"));
        assertTrue(instance.getActualCategories().contains(null));
    }

    /**
     * Test of getPredictedCategories method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testGetPredictedCategories()
    {
        DefaultConfusionMatrix<String> instance =
            new DefaultConfusionMatrix<String>();
        assertTrue(instance.getPredictedCategories().isEmpty());

        instance = this.createExampleInstance();
        assertEquals(5, instance.getPredictedCategories().size());
        assertTrue(instance.getPredictedCategories().contains("a"));
        assertTrue(instance.getPredictedCategories().contains("b"));
        assertTrue(instance.getPredictedCategories().contains("c"));
        assertTrue(instance.getPredictedCategories().contains("d"));
        assertTrue(instance.getPredictedCategories().contains("e"));

        instance.add("f", "a");
        assertEquals(5, instance.getPredictedCategories().size());
        assertTrue(instance.getPredictedCategories().contains("a"));
        assertTrue(instance.getPredictedCategories().contains("b"));
        assertTrue(instance.getPredictedCategories().contains("c"));
        assertTrue(instance.getPredictedCategories().contains("d"));
        assertTrue(instance.getPredictedCategories().contains("e"));

        instance.add("f", null);
        assertEquals(6, instance.getPredictedCategories().size());
        assertTrue(instance.getPredictedCategories().contains("a"));
        assertTrue(instance.getPredictedCategories().contains("b"));
        assertTrue(instance.getPredictedCategories().contains("c"));
        assertTrue(instance.getPredictedCategories().contains("d"));
        assertTrue(instance.getPredictedCategories().contains("e"));
        assertTrue(instance.getPredictedCategories().contains(null));

        instance = new DefaultConfusionMatrix<String>();
        assertTrue(instance.getPredictedCategories("a").isEmpty());
        assertTrue(instance.getPredictedCategories("b").isEmpty());
        assertTrue(instance.getPredictedCategories(null).isEmpty());

        instance.add("a", "b");
        assertEquals(1, instance.getPredictedCategories("a").size());
        assertTrue(instance.getPredictedCategories("a").contains("b"));
        assertEquals(0, instance.getPredictedCategories("b").size());
        assertEquals(0, instance.getPredictedCategories(null).size());
        
        instance.add("a", "a");
        assertEquals(2, instance.getPredictedCategories("a").size());
        assertTrue(instance.getPredictedCategories("a").contains("a"));
        assertTrue(instance.getPredictedCategories("a").contains("b"));
        assertEquals(0, instance.getPredictedCategories("b").size());
        assertEquals(0, instance.getPredictedCategories(null).size());

        instance.add("b", "b");
        assertEquals(2, instance.getPredictedCategories("a").size());
        assertTrue(instance.getPredictedCategories("a").contains("a"));
        assertTrue(instance.getPredictedCategories("a").contains("b"));
        assertEquals(1, instance.getPredictedCategories("b").size());
        assertTrue(instance.getPredictedCategories("b").contains("b"));
        assertEquals(0, instance.getPredictedCategories(null).size());
        
        instance.add("b", null);
        assertEquals(2, instance.getPredictedCategories("a").size());
        assertTrue(instance.getPredictedCategories("a").contains("a"));
        assertTrue(instance.getPredictedCategories("a").contains("b"));
        assertEquals(2, instance.getPredictedCategories("b").size());
        assertTrue(instance.getPredictedCategories("b").contains("b"));
        assertTrue(instance.getPredictedCategories("b").contains(null));
        assertEquals(0, instance.getPredictedCategories(null).size());

        instance.add(null, "a");
        assertEquals(2, instance.getPredictedCategories("a").size());
        assertTrue(instance.getPredictedCategories("a").contains("a"));
        assertTrue(instance.getPredictedCategories("a").contains("b"));
        assertEquals(2, instance.getPredictedCategories("b").size());
        assertTrue(instance.getPredictedCategories("b").contains("b"));
        assertTrue(instance.getPredictedCategories("b").contains(null));
        assertEquals(1, instance.getPredictedCategories(null).size());
        assertTrue(instance.getPredictedCategories(null).contains("a"));

        instance.add(null, null);
        assertEquals(2, instance.getPredictedCategories("a").size());
        assertTrue(instance.getPredictedCategories("a").contains("a"));
        assertTrue(instance.getPredictedCategories("a").contains("b"));
        assertEquals(2, instance.getPredictedCategories("b").size());
        assertTrue(instance.getPredictedCategories("b").contains("b"));
        assertTrue(instance.getPredictedCategories("b").contains(null));
        assertEquals(2, instance.getPredictedCategories(null).size());
        assertTrue(instance.getPredictedCategories(null).contains("a"));
        assertTrue(instance.getPredictedCategories(null).contains(null));
    }

    /**
     * Test of toString method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        DefaultConfusionMatrix<String> instance = this.createEmptyInstance();
        assertNotNull(instance.toString());

        instance = this.createPopulatedInstance();
        assertNotNull(instance.toString());

        instance.add(null, null);
        assertNotNull(instance.toString());
    }

    /**
     * Test of createUnweighted method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testCreateUnweighted()
    {
        Collection<TargetEstimatePair<Boolean, Boolean>> data = new LinkedList<TargetEstimatePair<Boolean, Boolean>>();
        DefaultConfusionMatrix<Boolean> instance = DefaultConfusionMatrix.createUnweighted(data);
        assertTrue(instance.isEmpty());
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getCount(true, true), 0.0);
        assertEquals(0.0, instance.getCount(false, false), 0.0);
        assertEquals(0.0, instance.getCount(false, true), 0.0);
        assertEquals(0.0, instance.getCount(true, false), 0.0);

        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        instance = DefaultConfusionMatrix.createUnweighted(data);
        assertFalse(instance.isEmpty());
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getCount(true, true), 0.0);
        assertEquals(0.0, instance.getCount(false, false), 0.0);
        assertEquals(1.0, instance.getCount(false, true), 0.0);
        assertEquals(0.0, instance.getCount(true, false), 0.0);


        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, false));
        instance = DefaultConfusionMatrix.createUnweighted(data);
        assertFalse(instance.isEmpty());
        assertEquals(10.0, instance.getTotalCount(), 0.0);
        assertEquals(4.0, instance.getCount(true, true), 0.0);
        assertEquals(3.0, instance.getCount(false, false), 0.0);
        assertEquals(2.0, instance.getCount(false, true), 0.0);
        assertEquals(1.0, instance.getCount(true, false), 0.0);
    }

    /**
     * Test of createFromActualPredictedPairs method, of class DefaultConfusionMatrix.
     */
    @Test
    public void testCreateFromActualPredictedPairs()
    {
        Collection<Pair<Boolean, Boolean>> data = new LinkedList<Pair<Boolean, Boolean>>();
        DefaultConfusionMatrix<Boolean> instance = DefaultConfusionMatrix.createFromActualPredictedPairs(data);
        assertTrue(instance.isEmpty());
        assertEquals(0.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getCount(true, true), 0.0);
        assertEquals(0.0, instance.getCount(false, false), 0.0);
        assertEquals(0.0, instance.getCount(false, true), 0.0);
        assertEquals(0.0, instance.getCount(true, false), 0.0);

        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        instance = DefaultConfusionMatrix.createFromActualPredictedPairs(data);
        assertFalse(instance.isEmpty());
        assertEquals(1.0, instance.getTotalCount(), 0.0);
        assertEquals(0.0, instance.getCount(true, true), 0.0);
        assertEquals(0.0, instance.getCount(false, false), 0.0);
        assertEquals(1.0, instance.getCount(false, true), 0.0);
        assertEquals(0.0, instance.getCount(true, false), 0.0);


        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, true));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(false, false));
        data.add(new DefaultTargetEstimatePair<Boolean, Boolean>(true, false));
        instance = DefaultConfusionMatrix.createFromActualPredictedPairs(data);
        assertFalse(instance.isEmpty());
        assertEquals(10.0, instance.getTotalCount(), 0.0);
        assertEquals(4.0, instance.getCount(true, true), 0.0);
        assertEquals(3.0, instance.getCount(false, false), 0.0);
        assertEquals(2.0, instance.getCount(false, true), 0.0);
        assertEquals(1.0, instance.getCount(true, false), 0.0);
    }

}