/*
 * File:            FieldTestHarness.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2013, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

/**
 * Implements a test harness for the {@link Field} interface.
 *
 * @param   <FieldType>
 *      The type of field to test.
 * @author  Justin Basilico
 * @version 3.3.3
 */
public abstract class FieldTestHarness<FieldType extends Field<FieldType>>
    extends EuclideanRingTestHarness<FieldType>
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public FieldTestHarness(
        final String testName)
    {
        super(testName);
    }
    
    public void testInverseEquals()
    {
        FieldType f1 = this.createRandom();
        FieldType f1Clone = f1.clone();

        assertEquals(f1, f1Clone);
        f1.inverseEquals();
        assertFalse(f1.equals(f1Clone, TOLERANCE));

        // x = x * x * 1 / x
        assertTrue(f1Clone.equals(f1Clone.times(f1).times(f1Clone), TOLERANCE));

        // x = 1 / (1 / x)
        f1.inverseEquals();
        assertTrue(f1Clone.equals(f1, TOLERANCE));
    }

    public void testInverse()
    {
        FieldType f1 = this.createRandom();
        FieldType f1Clone = f1.clone();

        assertEquals(f1, f1Clone);
        f1.inverse();
        assertEquals(f1, f1Clone);

        assertFalse(f1.inverse().equals(f1, TOLERANCE));

        // x = x * x * 1 / x
        assertTrue(f1.equals(f1.times(f1.inverse()).times(f1), TOLERANCE));

        // x = 1 / (1 / x)
        assertTrue(f1.equals(f1.inverse().inverse(), TOLERANCE));
    }

}
