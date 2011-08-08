/*
 * File:                BinaryCategorizerSelector.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.BinaryCategorizer;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The {@code BinaryCategorizerSelector} class implements a "weak learner"
 * meant for use in boosting algorithms that selects the best 
 * {@code BinaryCategorizer} from a pre-set list by picking the one with the
 * best weighted error.
 *
 * @param  <InputType> The type of the input.
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Removed implements Serializable, since BatchLearner already does.",
        "Otherwise, looks fine."
    }
)
public class BinaryCategorizerSelector<InputType>
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<InputType,Boolean,BinaryCategorizer<? super InputType>>
{
    /** The collection of categorizers to evaluate and select from. */
    protected Collection<BinaryCategorizer<? super InputType>> categorizers;
    
    /**
     * Creates a new instance of {@code BinaryCategorizerSelector}.
     */
    public BinaryCategorizerSelector()
    {
        this(new LinkedList<BinaryCategorizer<? super InputType>>());
    }
    
    /**
     * Creates a new instance of {@code BinaryCategorizerSelector}.
     * 
     * @param   categorizers The categorizers to use.
     */
    public BinaryCategorizerSelector(
        final Collection<BinaryCategorizer<? super InputType>> categorizers)
    {
        super();
        
        this.setCategorizers(categorizers);
    }
    
    /**
     * Selects the BinaryCategorizer from its list of categorizers that 
     * minimizes the weighted error on the given set of weighted input-output
     * pairs.
     *
     * @param  data 
     *      The set of weighted input-output pairs to use to select the best
     *      categorizer.
     * @return 
     *      The BinaryCategorizer from its list of categorizers that minimizes
     *      the weighted error on the given data.
     */
    public BinaryCategorizer<? super InputType> learn(
        Collection<? extends InputOutputPair<? extends InputType, Boolean>> data )
    {
        // We need to find the categorizer with the smallest training error.
        double bestWeightedError = Double.MAX_VALUE;
        BinaryCategorizer<? super InputType> best = null;

        // To find the best categorizer we evaluate each categorizer on each
        // input to compute its weighted error.
        for ( BinaryCategorizer<? super InputType> categorizer 
            : this.getCategorizers() )
        {
            // Go through all the examples to get the weighted error.
            double weightedError = 0.0;
            for ( InputOutputPair<? extends InputType, Boolean> example : data )
            {
                final double weight = DatasetUtil.getWeight(example);
                if (weight == 0.0)
                {
                    // No need to evaluate examples with no weight.
                    continue;
                }

                // Compute the output of the categorizer on this input.
                final boolean estimated = 
                    categorizer.evaluate(example.getInput());
                final boolean actual = example.getOutput();

                // Check to see if the output is correct.
                if ( estimated != actual )
                {
                    // This was estimated incorrectly.
                    weightedError += weight;
                }
                // else - It was estimated correctly.
            }
            
            if ( best == null || weightedError < bestWeightedError )
            {
                // This is the best categorizer found so far.
                bestWeightedError = weightedError;
                best = categorizer;
            }
        }

        // Return the best categorizer that we've found.
        return best;
    }

    /**
     * Gets the collection of categorizers that the learner selects from.
     *
     * @return The collection of categorizers that the learner selects from.
     */
    public Collection<BinaryCategorizer<? super InputType>> getCategorizers()
    {
        return this.categorizers;
    }

    /**
     * Gets the collection of categorizers that the learner selects from.
     *
     * @param  categorizers
     *      The collection of categorizers that the learner selects from.
     */
    public void setCategorizers(
        final Collection<BinaryCategorizer<? super InputType>> categorizers)
    {
        this.categorizers = categorizers;
    }

}
