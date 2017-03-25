/*
 * File:            DefaultSummaryStatisticsTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.test;

import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;
import static org.junit.Assert.*;

/**
 * Utility class for asserting aspects of bean properties.
 * 
 * @author Justin Basilico
 * @since 4.0.0
 */
public class PropertyChecker
    extends Object
{
    /**
     * Protected to prevent creation of singleton class.
     */
    protected PropertyChecker()
    {
        super();
    }
    
    /**
     * Checks that the getter/setter of a double property works as expected.
     *
     * @param   <T>
     *      The type of the instance.
     * @param   instance
     *      The instance to check the getter/setter on.
     * @param   name
     *      The name of the property to check.
     * @param   getter
     *      The getter for the property.
     * @param   setter
     *      The setter for the property.
     * @param   expectedInitialValue
     *      The expected initial value from the setter.
     * @param   goodValues
     *      The array of good values to check. They should work when calling
     *      the getter/setter.
     * @param   badValues
     *      The array of bad values to check. They should thrown an
     *      {@link IllegalArgumentException} in the setter and the value should
     *      remain unchanged.
     */
    public static <T> void checkGetSetDouble(
        final T instance,
        final String name,
        final ToDoubleFunction<T> getter,
        final ObjDoubleConsumer<T> setter,
        final double expectedInitialValue,
        final double[] goodValues,
        final double[] badValues)
    {
        try
        {
            // Make sure the getter works.
            double value = expectedInitialValue;
            assertEquals(value, getter.applyAsDouble(instance), 0.0);

            // Now make sure the setter works by setting the initial value
            // again.
            setter.accept(instance, expectedInitialValue);
            assertEquals(value, getter.applyAsDouble(instance), 0.0);

            for (final double goodValue : goodValues)
            {
                value = goodValue;
                setter.accept(instance, value);
                assertEquals(value, getter.applyAsDouble(instance), 0.0);
            }

            if (badValues != null)
            {
                for (final double badValue : badValues)
                {
                    boolean exceptionThrown = false;
                    try
                    {
                        setter.accept(instance, badValue);
                    }
                    catch (Exception e)
                    {
                        exceptionThrown = e instanceof IllegalArgumentException
                            || e.getCause() instanceof IllegalArgumentException;
                    }
                    finally
                    {
                        assertTrue("Expected an IllegalArgumentException when "
                            + "calling setter for" + name + " with value " 
                            + badValue + " but got none.",
                            exceptionThrown);
                    }

                    assertEquals(value, getter.applyAsDouble(instance), 0.0);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
