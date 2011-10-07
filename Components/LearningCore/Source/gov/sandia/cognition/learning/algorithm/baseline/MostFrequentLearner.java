/*
 * File:                MostFrequentLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * The {@code MostFrequentLearner} class implements a baseline learner that
 * computes the most frequent output value.
 * 
 * @param   <OutputType> The type of the output.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Fixed a few typos in javadoc.",
        "Removed implements Serializeable, as BatchLearner already does that.",
        "I don't particularly like this class... I just don't think it's useful.",
        "However, the code looks fine."
    }
)
public class MostFrequentLearner<OutputType>
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Object,OutputType,ConstantEvaluator<OutputType>>
{
    /**
     * Creates a new {@code MostFrequentLearner}.
     */
    public MostFrequentLearner()
    {
        super();
    }

    /**
     * Creates a constant evaluator that for the most frequent output value of
     * the given dataset.
     * 
     * @param   data The dataset of input-output pairs to use.
     * @return  A constant evaluator for the most frequent output value.
     */
    @Override
    public ConstantEvaluator<OutputType> learn(
        final Collection<? extends InputOutputPair<? extends Object, OutputType>> data )
    {
        // We are going to count up how many times each value occurs.
        final DefaultDataDistribution<OutputType> counts =
            new DefaultDataDistribution<OutputType>();
        
        for (InputOutputPair<?, ? extends OutputType> example : data)
        {
            counts.increment(example.getOutput());
        }
        
        // Create the resulting evaluator.
        final ConstantEvaluator<OutputType> result = 
            new ConstantEvaluator<OutputType>();
        if (counts.getTotal() > 0)
        {
            result.setValue(counts.getMaxValueKey());
        }
        // else - There is no most frequent output value.
        return result;
    }
    
}
