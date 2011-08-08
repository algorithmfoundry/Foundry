/*
 * File:                LinearMixtureModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright November 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * A linear mixture of RandomVariables, with a prior probability distribution.
 * The posterior pdf is:
 * p(x|this) = \sum_{y\in this} p(x|y,this)P(y|this),
 * where p(x|y,this) is the pdf of the underlying RandomVariable, and
 * P(y|this) is the prior probability of RandomVariable y in this.
 *
 * @param <DataType> 
 *      Type of data in this mixture model
 * @param <InternalDistributionType>
 *      The type of the internal distributions inside the mixture.
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Mixture Model",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Mixture_model"
)
public abstract class LinearMixtureModel<DataType, InternalDistributionType extends Distribution<DataType>>
    extends AbstractDistribution<DataType>
{
    
    /**
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input
     */
    private ArrayList<? extends InternalDistributionType> randomVariables;
    
    /**
     * The prior probability distribution of the MultivariateGaussians, must
     * have 1-norm ~1.0 and dimensionality equal to the number of 
     * RandomVariables
     */
    private Vector priorProbabilities;
    
    /**
     * Creates a new instance of LinearMixtureModel
     * @param randomVariables 
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input
     */
    public LinearMixtureModel(
        Collection<? extends InternalDistributionType> randomVariables )
    {
        this( new ArrayList<InternalDistributionType>( randomVariables ),
            VectorFactory.getDefault().createVector( randomVariables.size() ) );
    }
    
    /**
     * Creates a new instance of LinearMixtureModel
     * @param randomVariables 
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input
     * @param priorProbabilities 
     * The prior probability distribution of the MultivariateGaussians must
     * have dimensionality equal to the number of RandomVariables.  If this
     * parameter doesn't have a 1-norm == 1.0, then this method will normalize
     * the distribution so that its 1-norm == 1.0.  If the prior distribution
     * is uniform 0.0, then this method will assume uniform with 1-norm
     * == 1.0.
     */
    public LinearMixtureModel(
        ArrayList<? extends InternalDistributionType> randomVariables,
        Vector priorProbabilities )
    {
        
        if( randomVariables.size() != priorProbabilities.getDimensionality() )
        {
            throw new IllegalArgumentException(
                "Number of RandomVariables must equal number of prior probabilities!" );
        }
        
        int M = randomVariables.size();
        this.setRandomVariables( randomVariables );        
        
        double norm1 = priorProbabilities.norm1();
        if( priorProbabilities.norm1() == 0.0 )
        {
            for( int i = 0; i < M; i++ )
            {
                priorProbabilities.setElement( i, 1.0/M );
            }
        }
        else if( norm1 != 1.0 )
        {
            priorProbabilities.scaleEquals( 1.0 / norm1 );
        }
        
        this.setPriorProbabilities( priorProbabilities );

        
    }

    @Override
    public LinearMixtureModel<DataType, InternalDistributionType> clone()
    {
        @SuppressWarnings("unchecked")
        LinearMixtureModel<DataType, InternalDistributionType> clone =
            (LinearMixtureModel<DataType, InternalDistributionType>) super.clone();
        clone.setRandomVariables( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getRandomVariables() ) );
        clone.setPriorProbabilities(
            ObjectUtil.cloneSafe( this.getPriorProbabilities() ) );
        return clone;
        
    }
    
    /**
     * Returns the number of random variables in the LinearMixtureModel
     * @return number of random variables in the LinearMixtureModel
     */
    public int getNumRandomVariables()
    {
        return this.getRandomVariables().size();
    }
    
    /**
     * Getter for randomVariables
     * @return 
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input
     */
    public ArrayList<? extends InternalDistributionType> getRandomVariables()
    {
        return this.randomVariables;
    }

    /**
     * Setter for randomVariables
     * @param randomVariables 
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input
     */
    protected void setRandomVariables(
        ArrayList<? extends InternalDistributionType> randomVariables)
    {
        this.randomVariables = randomVariables;
    }

    /**
     * Getter for priorProbabilities
     * @return 
     * The prior probability distribution of the MultivariateGaussians, must
     * have 1-norm ~1.0 and dimensionality equal to the number of 
     * RandomVariables
     */
    public Vector getPriorProbabilities()
    {
        return this.priorProbabilities;
    }

    /**
     * Setter for priorProbabilities
     * @param priorProbabilities 
     * The prior probability distribution of the MultivariateGaussians, must
     * have 1-norm ~1.0 and dimensionality equal to the number of 
     * RandomVariables
     */
    public void setPriorProbabilities(
        Vector priorProbabilities)
    {
        this.priorProbabilities = priorProbabilities;
    }

    public ArrayList<DataType> sample(
        Random random,
        int numDraws)
    {
        ArrayList<DataType> randomVectors =
            new ArrayList<DataType>( numDraws );
        for( int n = 0; n < numDraws; n++ )
        {
            // Make a single draw from the RandomVariable indicated by
            // the prior probability
            double prior = random.nextDouble();
            int index = 0;
            for( int i = 0; i < this.getNumRandomVariables(); i++ )
            {
                prior -= this.getPriorProbabilities().getElement( i );
                if( prior <= 0.0 )
                {
                    index = i;
                    break;
                }
            }
            randomVectors.add( this.getRandomVariables().get( index ).sample( random ) );            
        }
        
        return randomVectors;
        
    }

    public abstract DataType getMean();
    
    @Override
    public String toString()
    {
        StringBuilder retval = new StringBuilder();
        retval.append("LinearMixtureModel has " + this.getNumRandomVariables() + " RandomVariables:\n");
        for( int i = 0; i < this.getNumRandomVariables(); i++ )
        {
            Distribution rv = this.getRandomVariables().get(i);
            retval.append("RV " + i + ": Prior Probability = " + this.getPriorProbabilities().getElement(i));
            retval.append("\n");
            retval.append("\t" + rv);
        }
        return retval.toString();
    }
    
}
