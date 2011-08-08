/*
 * File:                DocumentExtractionExceptionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document.extractor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DocumentExtractionException.
 *
 * @author Justin Basilico
 */
public class DocumentExtractionExceptionTest
{

    public DocumentExtractionExceptionTest()
    {
    }

    /**
     * Test of constructors of class DocumentExtractionException.
     */
    @Test
    public void testConstructors()
    {
        String message = null;
        Throwable cause = null;
        DocumentExtractionException instance = new DocumentExtractionException();
        assertEquals(message, instance.getMessage());
        assertSame(cause, instance.getCause());

        message = "this is a test message";
        instance = new DocumentExtractionException(message);
        assertEquals(message, instance.getMessage());
        assertSame(cause, instance.getCause());

        cause = new Exception(message);
        instance = new DocumentExtractionException(cause);
        assertSame(cause, instance.getCause());

        instance = new DocumentExtractionException(message, cause);
        assertEquals(message, instance.getMessage());
        assertSame(cause, instance.getCause());
    }

}