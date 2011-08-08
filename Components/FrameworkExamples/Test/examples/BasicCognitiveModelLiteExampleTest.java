/*
 * File:                BasicCognitiveModelLiteExampleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package examples;

import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     BasicCognitiveModelLiteExample
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class BasicCognitiveModelLiteExampleTest 
    extends TestCase 
{
    /**
     * Creates a new BasicCognitiveModelLiteExampleTest.
     */
    public BasicCognitiveModelLiteExampleTest(
        String testName) 
    {
        super(testName);
    }


    /**
     * Test of main method, of class examples.BasicCognitiveModelLiteExample.
     */
    public void testMain() 
    {
        // This makes sure that the example code runs.
        String[] arguments = new String[0];
        BasicCognitiveModelLiteExample.main(arguments);
    }   
}
