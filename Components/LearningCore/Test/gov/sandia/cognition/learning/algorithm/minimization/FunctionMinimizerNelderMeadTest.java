/*
 * File:                FunctionMinimizerNelderMeadTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 9, 2007, Sandia Corporation.  Under the terms of Contract
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
 * JUnit tests for class FunctionMinimizerNelderMeadTest
 * @author Kevin R. Dixon
 */
public class FunctionMinimizerNelderMeadTest
    extends FunctionMinimizerTestHarness<Evaluator<? super Vector,Double>>    
{

    /**
     * Entry point for JUnit tests for class FunctionMinimizerNelderMeadTest
     * @param testName name of this test
     */
    public FunctionMinimizerNelderMeadTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public FunctionMinimizerNelderMead createInstance()
    {
        FunctionMinimizerNelderMead mini = new FunctionMinimizerNelderMead();
        mini.setTolerance( 1e-5 );
        return mini;
    }

}
