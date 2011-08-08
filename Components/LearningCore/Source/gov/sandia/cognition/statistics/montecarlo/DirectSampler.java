/*
 * File:                DirectSampler.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 11, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Sampler that generates samples directly from a target distribution.
 * @param <DataType> Type of data to sample.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class DirectSampler<DataType>
    extends AbstractCloneableSerializable
    implements MonteCarloSampler<DataType,DataType,ProbabilityFunction<DataType>>
{

    /** 
     * Creates a new instance of DirectSampler 
     */
    public DirectSampler()
    {
    }

    public ArrayList<? extends DataType> sample(
        ProbabilityFunction<DataType> targetFunction,
        Random random,
        int numSamples)
    {
        return targetFunction.sample(random, numSamples);
    }
    
}
