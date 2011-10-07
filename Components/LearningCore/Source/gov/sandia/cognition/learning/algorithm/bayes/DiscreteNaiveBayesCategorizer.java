/*
 * File:                DiscreteNaiveBayesCategorizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.bayes;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.DiscriminantCategorizer;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Naive Bayes Classifier for Discrete Data.  That is,
 * the categorizer takes a Collection of input attributes and infers the
 * most-likely category with the assumption that each input attribute is
 * independent of all others given the category.  In other words,
 * <BR>
 * Cml = arg max(c) P(C=c | X=inputs )
 * <BR>
 * = arg max(c) P(X=inputs AND C=c) / P(X=inputs)
 * <BR>
 * = arg max(c) P(X=inputs AND C=c) (since P(X=inputs) doesn't depend on the category).
 * <BR>
 * P(X=inputs AND C=c) = P(X=inputs|C=c) * P(C=c)
 * <BR>
 * (Naive Bayes assumption:) = P(X1=x1|C=c) * P(X2=x2|C=c) * ... * P(Xn=xn|C=c) * P(C=c).
 * <BR><BR>
 * While the DiscreteNaiveBayesCategorizer class assumes that all inputs have
 * the same dimensionality, it handles missing (unknown) data by inserting
 * a "null" into the given input Collection.  Furthermore,
 * the DiscreteNaiveBayesCategorizer class can also compute the probabilities
 * of various quantities.
 *
 * @param <InputType> Type of inputs to the categorizer.
 * @param <CategoryType> Type of the categories of the categorizer.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "Richard O. Duda",
                "Peter E. Hart",
                "David G. Stork"
            },
            title="Pattern Classification: Second Edition",
            type=PublicationType.Book,
            year=2001,
            pages={56,62}
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Naive Bayes classifier",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Naive_bayes"
        )
    }

)
public class DiscreteNaiveBayesCategorizer<InputType,CategoryType>
    extends AbstractCloneableSerializable
    implements DiscriminantCategorizer<Collection<InputType>,CategoryType,Double>
{


    /**
     * Class conditional probability table.
     */
    private Map<CategoryType,List<DefaultDataDistribution<InputType>>> conditionalProbabilities;

    /**
     * Table of category priors.
     */
    private DefaultDataDistribution<CategoryType> priorProbabilities;

    /**
     * Assumed dimensionality of the inputs.
     */
    private int inputDimensionality;

    /** 
     * Creates a new instance of DiscreteNaiveBayesCategorizer
     */
    public DiscreteNaiveBayesCategorizer()
    {
        this( 0 );
    }

    /**
     * Creates a new instance of DiscreteNaiveBayesCategorizer.
     * @param inputDimensionality
     * Assumed dimensionality of the inputs.
     */
    public DiscreteNaiveBayesCategorizer(
        final int inputDimensionality )
    {
        this.setInputDimensionality(inputDimensionality);
    }

    /**
     * Creates a new instance of DiscreteNaiveBayesCategorizer.
     * @param inputDimensionality
     * Assumed dimensionality of the inputs.
     * @param priorProbabilities
     * Table of category priors.
     * @param conditionalProbabilities
     * Class conditional probability table.
     */
    protected DiscreteNaiveBayesCategorizer(
        final int inputDimensionality,
        final DefaultDataDistribution<CategoryType> priorProbabilities,
        final Map<CategoryType,List<DefaultDataDistribution<InputType>>> conditionalProbabilities )
    {
        this.setInputDimensionality(inputDimensionality);
        this.priorProbabilities = priorProbabilities;
        this.conditionalProbabilities = conditionalProbabilities;
    }

    @Override
    public DiscreteNaiveBayesCategorizer<InputType,CategoryType> clone()
    {
        @SuppressWarnings("unchecked")
        DiscreteNaiveBayesCategorizer<InputType,CategoryType> clone =
            (DiscreteNaiveBayesCategorizer<InputType,CategoryType>) super.clone();
        clone.conditionalProbabilities =
            new LinkedHashMap<CategoryType,List<DefaultDataDistribution<InputType>>>();
        for( CategoryType category : this.getCategories() )
        {
            clone.conditionalProbabilities.put( category,
                ObjectUtil.cloneSmartElementsAsArrayList( this.conditionalProbabilities.get(category) ) );
        }
        
        clone.priorProbabilities =
            ObjectUtil.cloneSafe( this.priorProbabilities );
        return clone;
    }

    @Override
    public Set<CategoryType> getCategories()
    {
        return this.priorProbabilities.getDomain();
    }

    /**
     * Computes the probability of the given inputs. In other words,
     * P(X=inputs) = sum over all C=c ( P(X=inputs|C=c)* P(C=c) ).
     * @param inputs
     * Inputs for which to compute the probability.
     * @return
     * Probability of the inputs, P(X=inputs).
     */
    public double computeEvidenceProbabilty(
        final Collection<InputType> inputs )
    {

        double prob = 0.0;
        for( CategoryType category : this.getCategories() )
        {
            prob += this.computeConjuctiveProbability(inputs, category);
        }

        return prob;

    }


    /**
     * Computes the posterior probability of the inputs for the given category.
     * This is quite expensive as the denominator of Bayes rule will be
     * computed by computing the numerator probabilities for each category,
     * then summing them up.  If you're interested in the most likely class,
     * then I would STRONGLY suggest using computeConjuctiveProbability,
     * which is much cheaper.  In other words,
     * P(C=category|X=inputs) = P(X=inputs|C=category)*P(C=category)/P(X=inputs).
     * @param inputs
     * Inputs to compute the posterior.
     * @param category
     * Category to compute the posterior.
     * @return
     * Posterior probability, P(C=category|X=inputs).
     */
    public double computePosterior(
        final Collection<InputType> inputs,
        final CategoryType category )
    {

        // This is REALLY expensive, as it computes the proporationate
        // posterior for all categories and then sums them up.
        double evidenceProbability = this.computeEvidenceProbabilty(inputs);

        if( evidenceProbability > 0.0 )
        {
            double numerator = this.computeConjuctiveProbability(inputs,category);
            return numerator / evidenceProbability;
        }
        else
        {
            return 0.0;
        }

    }

    /**
     * Computes the class conditional for the given inputs at the given
     * category assuming that each input feature is conditionally independent
     * of all other features.  In other words,
     * P(X=inputs|C=category) = P(X0=x0|C=category) * P(X1=x1|C=category) * ... * P(Xn=xn|C=category).
     * @param inputs
     * Inputs to compute the class conditional.
     * @param category
     * Category to compute the class conditional.
     * @return
     * Class conditional probability, P(X=inputs|C=category)
     */
    public double computeConditionalProbability(
        final Collection<InputType> inputs,
        final CategoryType category )
    {

        if( inputs.size() != this.getInputDimensionality() )
        {
            throw new IllegalArgumentException(
                "Input dimensionality doesn't match " + this.getInputDimensionality() );
        }

        Iterator<DefaultDataDistribution<InputType>> conditionalPMFIterator =
            this.conditionalProbabilities.get( category ).iterator();

        double conditionalProbability = 1.0;
        for( InputType input : inputs )
        {
            DefaultDataDistribution<InputType> conditionalPMF =
                conditionalPMFIterator.next();
            if( input != null )
            {
                conditionalProbability *= conditionalPMF.getFraction(input);
            }
            if( conditionalProbability <= 0.0 )
            {
                break;
            }
        }

        return conditionalProbability;

    }

    /**
     * Updates the probability tables from observing the sample inputs and
     * category.  If the tables are empty, then this observation sets the
     * assumed input dimensionality.
     * @param inputs
     * Inputs to update.
     * @param category
     * Category to update.
     */
    public void update(
        final Collection<InputType> inputs,
        final CategoryType category )
    {

        // If we have no expected input dimensionality, then assume that
        // all future inputs will be the same dimension as this one.
        if( this.getInputDimensionality() <= 0 )
        {
            this.setInputDimensionality( inputs.size() );
        }


        if( inputs.size() != this.getInputDimensionality() )
        {
            throw new IllegalArgumentException(
                "Input dimensionality doesn't match " + this.getInputDimensionality() );
        }


        // Need to make a new conditional probability table for this category
        if( !this.getCategories().contains( category ) )
        {
            ArrayList<DefaultDataDistribution<InputType>> conditional =
                new ArrayList<DefaultDataDistribution<InputType>>( this.getInputDimensionality() );
            for( int i = 0; i < this.getInputDimensionality(); i++ )
            {
                conditional.add( new DefaultDataDistribution<InputType>() );
            }
            this.conditionalProbabilities.put( category, conditional );
        }

        this.priorProbabilities.increment( category );
        Iterator<DefaultDataDistribution<InputType>> conditionalPMFIterator =
            this.conditionalProbabilities.get(category).iterator();
        for( InputType input : inputs )
        {
            DefaultDataDistribution<InputType> conditionalPMF =
                conditionalPMFIterator.next();
            if( input != null )
            {
                conditionalPMF.increment( input );
            }
        }

    }


    /**
     * Computes the conjunctive probability of the inputs and the category.
     * This is the numerator of Bayes rule.
     * In other words,
     * <BR>
     * P( X=inputs AND C=category ) = P(X=inputs|C=category) * P(C=category).
     * <BR><BR>
     * Under the Naive Bayes assumption, the input features are assumed to be
     * independent of all others given the category.  So, we compute the above
     * probability as
     * <BR>
     * P(X=inputs|C=c) = P(X1=x1|C=c) * P(X2=x2|C=c) * ... * P(Xn=xn|C=c).
     * <BR><BR>
     * If we're just interested in finding the most-likely category, then
     * the conjunctive probability is sufficient.
     * @param inputs
     * Inputs for which to compute the conjunctive probability.
     * @param category
     * Category for which to compute the conjunctive probability.
     * @return
     * The conjunctive probability, which is the numerator of Bayes rule.
     */
    public double computeConjuctiveProbability(
        final Collection<InputType> inputs,
        final CategoryType category )
    {

        double categoryPrior = this.getPriorProbability(category);
        if( categoryPrior > 0.0 )
        {
            double conditionalProbability =
                this.computeConditionalProbability(inputs, category);
            return conditionalProbability * categoryPrior;
        }
        else
        {
            return 0.0;
        }

    }

    @Override
    public CategoryType evaluate(
        final Collection<InputType> inputs )
    {
        return this.evaluateWithDiscriminant(inputs).getValue();
    }

    @Override
    public DefaultWeightedValueDiscriminant<CategoryType> evaluateWithDiscriminant(
        final Collection<InputType> input)
    {
        // compute the product of the class conditionals
        double maxPosterior = -1.0;
        CategoryType maxCategory = null;
        for (CategoryType category : this.getCategories())
        {
            // Actually, this is only proportionate to the posterior
            // We would need to divide by the unconditional probability
            // of the inputs to compute the accordinng-to-Hoyle posterior.
            double posterior =
                this.computeConjuctiveProbability(input, category);
            if (maxPosterior < posterior)
            {
                maxPosterior = posterior;
                maxCategory = category;
            }
        }

        return DefaultWeightedValueDiscriminant.create(maxCategory, maxPosterior);
    }

    /**
     * Gets the conditional probability for the given input and category.  In
     * other words,
     * <BR>
     * P(Xindex=input|C=category).
     * @param index
     * Index to compute.
     * @param input
     * Input value to assume.
     * @param category
     * Category value to assume.
     * @return
     * Class conditional probability of the given input and category.
     */
    public double getConditionalProbability(
        final int index,
        final InputType input,
        final CategoryType category )
    {
        return this.conditionalProbabilities.get(category).get(index).getFraction(input);
    }

    /**
     * Returns the prior probability of the given category.  In other words,
     * <BR>
     * P(C=category).
     * @param category
     * Category to return the prior probability of.
     * @return
     * Prior probability of the given category.
     */
    public double getPriorProbability(
        final CategoryType category )
    {
        return this.priorProbabilities.getFraction(category);
    }

    /**
     * Getter for inputDimensionality.
     * @return
     * Assumed dimensionality of the inputs.
     */
    public int getInputDimensionality()
    {
        return this.inputDimensionality;
    }

    /**
     * Setter for inputDimensionality.  Also resets the probability tables.
     * @param inputDimensionality
     * Assumed dimensionality of the inputs.
     */
    public void setInputDimensionality(
        final int inputDimensionality)
    {
        this.conditionalProbabilities =
            new LinkedHashMap<CategoryType, List<DefaultDataDistribution<InputType>>>();
        this.priorProbabilities = new DefaultDataDistribution<CategoryType>();

        this.inputDimensionality = inputDimensionality;
    }


    /**
     * Learner for a DiscreteNaiveBayesCategorizer.
     * @param <InputType> Type of inputs to the categorizer.
     * @param <CategoryType> Type of the categories of the categorizer.
     */
    public static class Learner<InputType,CategoryType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<Collection<InputType>,CategoryType,DiscreteNaiveBayesCategorizer<InputType,CategoryType>>
    {

        /**
         * Default constructor.
         */
        public Learner()
        {
        }

        @Override
        public DiscreteNaiveBayesCategorizer<InputType, CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends Collection<InputType>, CategoryType>> data)
        {
            DiscreteNaiveBayesCategorizer<InputType,CategoryType> nbc =
                new DiscreteNaiveBayesCategorizer<InputType, CategoryType>();
            for( InputOutputPair<? extends Collection<InputType>,CategoryType> sample : data )
            {
                nbc.update(sample.getInput(), sample.getOutput());
            }
            return nbc;
        }
        
    }

}
