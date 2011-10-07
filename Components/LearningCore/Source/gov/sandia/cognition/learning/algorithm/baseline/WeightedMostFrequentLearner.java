/*
 * File:                WeightedMostFrequentLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * The {@code WeightedMostFrequentLearner} class implements a baseline learning
 * algorithm that finds the most frequent output of a given dataset based on
 * the weights of the examples.
 * 
 * @param   <OutputType> The output type of the data.
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
public class WeightedMostFrequentLearner<OutputType>
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Object,OutputType,ConstantEvaluator<OutputType>>
{
    /**
     * Creates a new {@code MostFrequentLearner}.
     */
    public WeightedMostFrequentLearner()
    {
        super();
    }

    /**
     * Creates a constant evaluator based on the most frequent output in a given
     * collection of input-output pairs, taking the weight into account.
     * 
     * @param   data {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public ConstantEvaluator<OutputType> learn(
        final Collection<? extends InputOutputPair<? extends Object, OutputType>> data )
    {
        // We are going to sum up the weight associated with each output value.
        final DefaultDataDistribution<OutputType> weightDistribution =
            new DefaultDataDistribution<OutputType>();
        
        // Go through all the examples and increment the weight sum for each
        // output value.
        for (InputOutputPair<?, ? extends OutputType> example : data)
        {
            final double weight = DatasetUtil.getWeight(example);
            final OutputType output = example.getOutput();
            weightDistribution.increment(output, weight);
        }
        
        // Figure out the output with the highest weight.
        final ConstantEvaluator<OutputType> result =
            new ConstantEvaluator<OutputType>();
        if (weightDistribution.getTotal() > 0.0)
        {
            result.setValue(weightDistribution.getMaxValueKey());
        }
        
        // Create the resulting evaluator.
        return result;
    }
    
}
