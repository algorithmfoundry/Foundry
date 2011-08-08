/*
 * File:                BlockExperimentComparison.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 2, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.util.Collection;

/**
 * Implements a null-hypothesis multiple-comparison test from a block-design
 * experiment.  In these experiments, there are some number of "subjects"
 * assigned to each "treatment".  Typically, the null-hypothesis of a
 * block-design experiment is that the treatments have no effect on the
 * measurements of the subject.
 * <BR><BR>
 * For example, one treatment may contain 10 subjects who are given nothing
 * but water.  Another treatment may contain 8 subjects who are given pure
 * corn syrup.  A third treatment may contain 12 subjects given liquefied lard.
 * The experimenter then measures the change in the subjects' weight from the
 * beginning to the end of the experiment.  The null-hypothesis may be that
 * the treatments (water, corn syrup, lard) has no effect on the weight gain
 * of the subjects.
 * <BR><BR>
 * Please note that block-experiment comparisons, such as ANOVA and Friedman's
 * test, typically DO NOT indicate which treatment is statistically
 * significantly different from the others, just that a difference exists.
 * You must run a MultipleHypothesisComparison test to determine which
 * treatments are different from the others.
 * 
 * @param <DataType> Type of data measured from each subject.
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Randomized Block Design",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/Randomized_block_design"
)
public interface BlockExperimentComparison<DataType>
    extends NullHypothesisEvaluator<Collection<? extends DataType>>
{

    /**
     * Evaluates the null hypothesis for the given block-design treatments
     * @param treatments
     * Collection of treatments for the block-design experiment, where each
     * treatment contains
     * @return
     *      The confidence for the null hypothesis.
     */
    public ConfidenceStatistic evaluateNullHypothesis(
        Collection<? extends Collection<? extends DataType>> treatments );

}
