/*
 * File:                BayesianParameter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.DistributionParameter;
import gov.sandia.cognition.statistics.Distribution;
import java.util.Random;

/**
 * A parameter from a Distribution that has an assumed Distribution of
 * values.
 * @param <ParameterType>
 * Type of parameter that changes the behavior of the conditional distribution.
 * @param <ConditionalType>
 * Type of parameterized distribution that generates observations.
 * @param <PriorType>
 * Assumed underlying distribution of parameters of the conditional distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface BayesianParameter<ParameterType,ConditionalType extends Distribution<?>,PriorType extends Distribution<ParameterType>>
    extends DistributionParameter<ParameterType,ConditionalType>
{

    /**
     * Gets the Distribution of values that the parameter is assumed to take.
     * @return
     * Distribution of values that the parameter is assumed to take.
     */
    public PriorType getParameterPrior();

    /**
     * Updates the conditional distribution by sampling from the prior
     * distribution and assigning through the DistributionParameter.
     * @param random
     * Random number generator to use in sampling.
     */
    public void updateConditionalDistribution(
        Random random );

}
