/*
 * File:                AbstractBinaryConfusionMatrix.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 18, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.function.categorization.AbstractBinaryCategorizer;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Set;

/**
 * An abstract implementation of the {@code BinaryConfusionMatrix} interface.
 * Takes care of a lot of the general operations from the interface so that
 * extending classes only need to focus on the data structure implementation.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractBinaryConfusionMatrix
    extends AbstractConfusionMatrix<Boolean>
    implements BinaryConfusionMatrix
{

    /**
     * Creates a new {@code AbstractBinaryConfusionMatrix}.
     */
    public AbstractBinaryConfusionMatrix()
    {
        super();
    }

    @Override
    public Set<Boolean> getCategories()
    {
        return AbstractBinaryCategorizer.BINARY_CATEGORIES;
    }
    
    @Override
    public Set<Boolean> getActualCategories()
    {
        return AbstractBinaryCategorizer.BINARY_CATEGORIES;
    }

    @Override
    public Set<Boolean> getPredictedCategories()
    {
        return AbstractBinaryCategorizer.BINARY_CATEGORIES;
    }

    @Override
    public Set<Boolean> getPredictedCategories(
        final Boolean target)
    {
        return AbstractBinaryCategorizer.BINARY_CATEGORIES;
    }

    @Override
    public double getCount(
        final Boolean target,
        final Boolean estimate)
    {
        if (target)
        {
            if (estimate)
            {
                return this.getTruePositivesCount();
            }
            else
            {
                return this.getFalseNegativesCount();
            }
        }
        else
        {
            if (estimate)
            {
                return this.getFalsePositivesCount();
            }
            else
            {
                return this.getTrueNegativesCount();
            }
        }
    }

    @Override
    public double getActualCount(
        final Boolean target)
    {
        if (target)
        {
            return this.getTruePositivesCount() + this.getFalseNegativesCount();
        }
        else
        {
            return this.getFalsePositivesCount() + this.getTrueNegativesCount();
        }
    }


    @Override
    public double getTruePositivesFraction()
    {
        return this.getTruePositivesCount() / this.getTotalCount();
    }

    @Override
    public double getFalsePositivesFraction()
    {
        return this.getFalsePositivesCount() / this.getTotalCount();
    }

    @Override
    public double getTrueNegativesFraction()
    {
        return this.getTrueNegativesCount() / this.getTotalCount();
    }

    @Override
    public double getFalseNegativesFraction()
    {
        return this.getFalseNegativesCount() / this.getTotalCount();
    }

    @Override
    public double getSensitivity()
    {
        final double truePositivesCount = this.getTruePositivesCount();
        return truePositivesCount
            / (truePositivesCount + this.getFalseNegativesCount());
    }

    @Override
    public double getSpecificity()
    {
        final double trueNegativesCount = this.getTrueNegativesCount();
        return trueNegativesCount
            / (trueNegativesCount + this.getFalsePositivesCount());
    }

    @Override
    public double getPrecision()
    {
        return this.getTruePositivesCount()
            / (this.getTruePositivesCount() + this.getFalsePositivesCount());
    }

    @Override
    public double getRecall()
    {
        return this.getTruePositivesCount()
            / (this.getTruePositivesCount() + this.getFalseNegativesCount());
    }

    @Override
    public double getFScore()
    {
        return this.getFScore(1.0);
    }

    @Override
    public double getFScore(
        final double beta)
    {
        ArgumentChecker.assertIsNonNegative("beta", beta);
        final double betaSquared = beta * beta;
        final double precision = this.getPrecision();
        final double recall = this.getRecall();

        return (1.0 + betaSquared) * (precision * recall)
            / (betaSquared * precision + recall);
    }
    
}
