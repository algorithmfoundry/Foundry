/*
 * File:                DeciderLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Collection;

/**
 * The {@code DeciderLearner} interface defines the functionality of a learner
 * that can be used to learn a decision function inside a decision tree.
 *
 * @param   <InputType>
 *      The input type to learn a decision for.
 * @param   <OutputType>
 *      The output type to learn a decision for.
 * @param   <CategoryType>
 *      The category type that the decider will output.
 * @param   <DeciderType>
 *      The type of the decision function being learned.
 * @author  Justin Basilico
 * @since   2.0
 */
public interface DeciderLearner<InputType, OutputType, CategoryType, DeciderType extends Categorizer<? super InputType, ? extends CategoryType>>
    extends BatchLearner
        <Collection<? extends InputOutputPair<? extends InputType, OutputType>>,
         DeciderType>
{
}
