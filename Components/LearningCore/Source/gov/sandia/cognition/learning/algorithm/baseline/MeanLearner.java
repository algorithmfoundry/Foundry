/*
 * File:                MeanLearner.java
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
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * The {@code MeanLearner} class implements a baseline learner that computes
 * the mean of a given set of values.
 * 
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
public class MeanLearner
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Object, Double, ConstantEvaluator<Double>>
{
    /**
     * Creates a new {@code MeanLearner}.
     */
    public MeanLearner()
    {
        super();
    }

    /**
     * Creates a constant evaluator that returns the mean output value of the 
     * given dataset.
     * 
     * @param   data The dataset of input-output pairs to use.
     * @return  A constant evaluator for the mean output value.
     */
    @Override
    public ConstantEvaluator<Double> learn(
        final Collection<? extends InputOutputPair<?, Double>> data)
    {
        // Compute the mean.
        final double mean = DatasetUtil.computeOutputMean(data);
        return new ConstantEvaluator<Double>(mean);
    }
}
