/*
 * File:                BinaryCategorizerSelectorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.function.categorization.BinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import junit.framework.*;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     BinaryCategorizerSelector
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class BinaryCategorizerSelectorTest
    extends TestCase
{
    /**
     * Creates a new BinaryCategorizerSelectorTest.
     *
     * @param  testName The test name.
     */
    public BinaryCategorizerSelectorTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the constructors of BinaryCategorizerSelector.
     */
    public void testConstructors()
    {
        BinaryCategorizerSelector<Vector> instance = 
            new BinaryCategorizerSelector<Vector>();
        assertNotNull(instance.getCategorizers());
        assertEquals(0, instance.getCategorizers().size());
        
        LinkedList<BinaryCategorizer<? super Vector>> categorizers = 
            new LinkedList<BinaryCategorizer<? super Vector>>();
        categorizers.add(new LinearBinaryCategorizer());
        
        instance = new BinaryCategorizerSelector<Vector>(categorizers);
        assertSame(categorizers, instance.getCategorizers());
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.ensemble.BinaryCategorizerSelector.
     */
    public void testLearn()
    {
        Vector2[] positives = new Vector2[]
        {
            new Vector2(1.00,  1.00),
            new Vector2(1.00,  3.00),
            new Vector2(0.25,  4.00),
            new Vector2(2.00,  1.00),
            new Vector2(5.00, -3.00)
        };
        
        Vector2[] negatives = new Vector2[]
        {
            new Vector2(2.00,  3.00),
            new Vector2(2.00,  4.00),
            new Vector2(3.00,  2.00),
            new Vector2(4.25,  3.75),
            new Vector2(4.00,  7.00),
            new Vector2(7.00,  4.00)
        };
        
        ArrayList<DefaultWeightedInputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<DefaultWeightedInputOutputPair<Vector2, Boolean>>();
        for ( Vector2 example : positives )
        {
            examples.add(new DefaultWeightedInputOutputPair<Vector2, Boolean>(example, true, 1.0));
        }
        
        for ( Vector2 example : negatives )
        {
            examples.add(new DefaultWeightedInputOutputPair<Vector2, Boolean>(example, false, 1.0));
        }
        
        LinkedList<BinaryCategorizer<? super Vector>> categorizers = 
            new LinkedList<BinaryCategorizer<? super Vector>>();
        
        LinearBinaryCategorizer categorizer1 = 
            new LinearBinaryCategorizer(new Vector2(-1.0, 0.0), 1.5);
        LinearBinaryCategorizer categorizer2 = 
            new LinearBinaryCategorizer(new Vector2(-1.0, 0.0), 2.5);
        
        
        categorizers.add(categorizer1);
        categorizers.add(categorizer2);
        BinaryCategorizerSelector<Vector> instance = 
             new BinaryCategorizerSelector<Vector>(categorizers);
        
        BinaryCategorizer<? super Vector> learned = instance.learn(examples);
        assertSame(categorizer1, learned);
        
        // Categorizer 1 gets the third example wrong but categorizer 2 gets it
        // right. Lets give it a lot of weight and make sure that categorizer 2
        // is returned.
        examples.get(3).setWeight(100.0);
        learned = instance.learn(examples);
        assertSame(categorizer2, learned);
    }

    /**
     * Test of getCategorizers method, of class gov.sandia.cognition.learning.ensemble.BinaryCategorizerSelector.
     */
    public void testGetCategorizers()
    {
        this.testSetCategorizers();
    }

    /**
     * Test of setCategorizers method, of class gov.sandia.cognition.learning.ensemble.BinaryCategorizerSelector.
     */
    public void testSetCategorizers()
    {
        BinaryCategorizerSelector<Vector> instance = new BinaryCategorizerSelector<Vector>();
        assertNotNull(instance.getCategorizers());
        assertEquals(0, instance.getCategorizers().size());
        
        LinkedList<BinaryCategorizer<? super Vector>> categorizers = 
            new LinkedList<BinaryCategorizer<? super Vector>>();
        categorizers.add(new LinearBinaryCategorizer());
        instance.setCategorizers(categorizers);
        assertSame(categorizers, instance.getCategorizers());
        
        instance.setCategorizers(null);
        assertNull(instance.getCategorizers());
    }
}
