/*
 * File:                KullbackLeiblerDivergence.java
 * Authors:             Tom Brounstein
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 31, 2014, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.util.Collections;
import java.util.Set;

/**
 * A class used for measuring how similar two distributions are using Kullback--Leibler Divergence.
 * @author trbroun
 * @param <DomainType>  The type for the domain of the two distributions which this class is comparing.
 */
@PublicationReference(
    author="Wikipedia",
    title="Kullback--Leibler Divergence",
    type=PublicationType.WebPage,
    year=2014,
    url="http://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence"
)
public class KullbackLeiblerDivergence<DomainType>
{

    private final DiscreteDistribution<DomainType> firstDistribution;
    private final DiscreteDistribution<DomainType> secondDistribution;
    
    /**
     * Basic constructor to find the Kullback--Leibler Divergence between the two supplied distributions.
     * The two distributions must be over the same domain.
     * @param firstDistribution
     * @param secondDistribution 
     */
    @SuppressWarnings("unchecked")
    public KullbackLeiblerDivergence(DiscreteDistribution<DomainType> firstDistribution,
        DiscreteDistribution<DomainType> secondDistribution)
    {
        if (firstDistribution == null) {
            throw new IllegalArgumentException("First distribution is null.");
        }
        if (secondDistribution == null) {
            throw new IllegalArgumentException("Second distribution is null.");
        }
        for (DomainType term : firstDistribution.getDomain()) {
            double temp = firstDistribution.getProbabilityFunction().evaluate(term);
            if (temp != 0 && (!secondDistribution.getDomain().contains(term) ||
                secondDistribution.getProbabilityFunction().evaluate(term)==0)) {
                throw new IllegalArgumentException("Domain mismatch; a non-zero probability in first distribution requires a non-zero probability in second distribution");
            }
        }        
        this.firstDistribution = (DiscreteDistribution<DomainType>) firstDistribution.clone();
        this.secondDistribution = (DiscreteDistribution<DomainType>) secondDistribution.clone();
    }

    /**
     * Gets the domain of the distributions.
     * @return The domain of the distributions.
     */
    public Set<? extends DomainType> getDomain()
    {
        return Collections.unmodifiableSet(secondDistribution.getDomain());
    }

    /**
     * Computes the Kullback--Leibler Divergence.
     * @return
     */
    public double compute()
    {
        double sum = 0.0;
        Set<? extends DomainType> domain = firstDistribution.getDomain();
        ProbabilityMassFunction<DomainType> pmfP =
            firstDistribution.getProbabilityFunction();
        ProbabilityMassFunction<DomainType> pmfQ = 
            secondDistribution.getProbabilityFunction();

        for (DomainType element : domain)
        {
            double PTerm = pmfP.evaluate(element);
            double QTerm = pmfQ.evaluate(element);
            if (QTerm == 0 || PTerm == 0) {
                continue;
            }
            double temp = PTerm/QTerm;
            temp = Math.log(temp);
            double termSolution = temp*PTerm;
            sum += termSolution;
        }
        
        return sum;
    }

}
