/*
 * File:                AbstractAnytimeAlgorithm.java
 * Authors:             Kevin R. Dixon and Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * A partial implementation of the common functionality of an 
 * {@code AnytimeAlgorithm}.
 * 
 * @param   <ResultType> The type of object produced by the algorithm.
 * @author  Kevin R. Dixon
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2008-07-17",
    changesNeeded=false,
    comments="Abstract class looks fine."
)
public abstract class AbstractAnytimeAlgorithm<ResultType>
    extends AbstractIterativeAlgorithm
    implements AnytimeAlgorithm<ResultType>
{

    /**
     * Maximum number of iterations before stopping
     */
    protected int maxIterations;
    
    /** 
     * Creates a new instance of {@code AbstractAnytimeAlgorithm}.
     * 
     * @param   maxIterations 
     *      Maximum number of iterations before stopping
     */
    public AbstractAnytimeAlgorithm(
        final int maxIterations)
    {
        super();
        
        this.setMaxIterations(maxIterations);
    }

    public boolean isResultValid()
    {
        return this.getResult() != null;
    }

    public int getMaxIterations()
    {
        return this.maxIterations;
    }

    public void setMaxIterations(
        final int maxIterations)
    {
        if (maxIterations <= 0)
        {
            throw new IllegalArgumentException(
                "maxIterations must be greater than zero.");
        }
        
        this.maxIterations = maxIterations;
    }

}
