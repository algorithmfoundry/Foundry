/*
 * File:                ConditionalProbability.java
 * Authors:             Tom Brounstein
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apir 2, 2014, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.ComputableDistribution;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A class for finding the conditional probability of two elements, or one element and a collection of other elements.
 * @author trbroun
 * @param <DataType>  The data type of the elements.
 */
@PublicationReference(
    author="Wikipedia",
    title="Conditional Probability",
    type=PublicationType.WebPage,
    year=2014,
    url="http://en.wikipedia.org/wiki/Conditional_probability"
)
public class ConditionalProbability<DataType>
{
    /**
     * Constructor for new Conditional Probability object.
     */
    public ConditionalProbability() {
        
    }
    
    /**
     * Computes the conditional probability between a collection of objects and a new object.
     * @param prior  The collection of objects that's conditioned on.  If conditional probability is thought of as "A given B", this is the B.
     * @param posterior  The new object; finding its probability given the prior.  This is the "A" in the conditional probability definition.
     * @param priorDistribution  The distribution which the prior is over.
     * @param posteriorDistribution  The distribution which the posterior is over.  If the prior contains n elements, this is a distribution over collections of size n+1
     * @return The double representing the conditional probability.
     */
    public double computeConditionalProbability(Collection<DataType> prior, DataType posterior,
        ComputableDistribution<Collection<DataType>> priorDistribution, ComputableDistribution<Collection<DataType>> posteriorDistribution) {
        
        Collection<DataType> realPosterior = new ArrayList<DataType>(prior);
        realPosterior.add(posterior);
        double numerator = posteriorDistribution.getProbabilityFunction().evaluate(realPosterior);
        double denominator = priorDistribution.getProbabilityFunction().evaluate(prior);
        if (denominator == 0) {
            throw new IllegalArgumentException("Prior does not appear in prior distribution.");
        }
        return numerator/denominator;     
    }
    
    /**
     * Computes the conditional probability between two objects.
     * @param prior  The object that's conditioning the posterior.  If conditional probability is thought of as "A given B", this is the B.
     * @param posterior  The object whose probability is based off the prior.  This is the "A" in the conditional probability definition.
     * @param priorDistribution  A distribution for the objects.
     * @param posteriorDistribution  A distribution for all pairs of objects.
     * @return The double representing the conditional probability.
     */
    public double computeConditionalProbability(DataType prior, DataType posterior,
        ComputableDistribution<DataType> priorDistribution, ComputableDistribution<Collection<DataType>> posteriorDistribution) {
        
        Collection<DataType> realPosterior = new ArrayList<DataType>();
        realPosterior.add(prior);
        realPosterior.add(posterior);
        double numerator = posteriorDistribution.getProbabilityFunction().evaluate(
            realPosterior);
        double denominator = priorDistribution.getProbabilityFunction().evaluate(prior);
        if (denominator == 0) {
            throw new IllegalArgumentException("Prior does not appear in prior distribution.");
        }
        return numerator/denominator;
    }
    
    /**
     * Computes the conditional probability between two objects.  This is used when the data type contains the historical data.
     * @param prior  The object that's conditioning the posterior.  If conditional probability is thought of as "A given B", this is the B.
     * @param posterior  The object whose probability is based off the prior.  This is the "A" in the conditional probability definition.
     * @param priorDistribution  A distribution for the objects.
     * @param posteriorDistribution  A distribution for all pairs of objects.
     * @return The double representing the conditional probability.
     */
    public double computeConditionalProbabilityWhenDataTypeHasHistoricalData(DataType prior, DataType posterior,
        ComputableDistribution<DataType> priorDistribution, ComputableDistribution<DataType> posteriorDistribution) {
        
        double numerator = posteriorDistribution.getProbabilityFunction().evaluate(
            posterior);
        double denominator = priorDistribution.getProbabilityFunction().evaluate(prior);
        if (denominator == 0) {
            throw new IllegalArgumentException("Prior does not appear in prior distribution.");
        }
        return numerator/denominator;
    }
}
