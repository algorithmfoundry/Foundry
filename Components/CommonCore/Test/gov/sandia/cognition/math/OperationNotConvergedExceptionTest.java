/*
 * File:            OperationNotConvergedExceptionTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OperationNotConvergedException.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class OperationNotConvergedExceptionTest
{

    /**
     * Creates a new test.
     */
    public OperationNotConvergedExceptionTest()
    {
        super();
    }

    /**
     * Test of constructors of class OperationNotConvergedException.
     */
    @Test
    public void testConstructors()
    {
        String message = "this is an error message that I made up";
        Throwable cause = null;
        OperationNotConvergedException instance = new OperationNotConvergedException(
            message);
        assertSame(message, instance.getMessage());
        assertSame(cause, instance.getCause());

        cause = new Exception();
        instance = new OperationNotConvergedException(message, cause);
        assertSame(message, instance.getMessage());
        assertSame(cause, instance.getCause());

        message = null;
        cause = new Exception("I caused it");
        instance = new OperationNotConvergedException(message, cause);
        assertSame(message, instance.getMessage());
        assertSame(cause, instance.getCause());
    }

}
