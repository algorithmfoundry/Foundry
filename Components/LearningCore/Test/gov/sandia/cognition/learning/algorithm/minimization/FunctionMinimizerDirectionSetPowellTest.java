/*
 * File:                FunctionMinimizerDirectionSetPowellTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright November 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class FunctionMinimizerDirectionSetPowellTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerDirectionSetPowellTest
    extends FunctionMinimizerTestHarness<Evaluator<? super Vector,Double>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerDirectionSetPowellTest
     * @param testName name of this test
     */
    public FunctionMinimizerDirectionSetPowellTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public FunctionMinimizerDirectionSetPowell createInstance()
    {
        return new FunctionMinimizerDirectionSetPowell();
    }
    
}
