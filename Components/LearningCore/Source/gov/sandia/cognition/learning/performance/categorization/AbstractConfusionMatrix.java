/*
 * File:                AbstractConfusionMatrix.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 17, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code ConfusionMatrix} interface. Takes
 * care of a lot of the support functions in the interface so that
 * implementations can focus on the core data structures.
 *
 * @param   <CategoryType>
 *      The type of category that the confusion matrix is under.
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractConfusionMatrix<CategoryType>
    extends AbstractCloneableSerializable
    implements ConfusionMatrix<CategoryType>
{

    /**
     * Creates a new {@code AbstractConfusionMatrix}.
     */
    public AbstractConfusionMatrix()
    {
        super();
    }

    @Override
    public void add(
        final CategoryType target,
        final CategoryType estimate)
    {
        this.add(target, estimate, 1.0);
    }

    @Override
    public <OtherType extends CategoryType> void addAll(
        final ConfusionMatrix<OtherType> other)
    {
        for (OtherType target : other.getActualCategories())
        {
            for (OtherType estimate : other.getPredictedCategories(target))
            {
                this.add(target, estimate, other.getCount(target, estimate));
            }
        }
    }
    
    @Override
    public boolean isEmpty()
    {
        return this.getTotalCount() <= 0.0;
    }

    @Override
    public double getTotalCount()
    {
        double result = 0.0;
        for (CategoryType target : this.getActualCategories())
        {
            result += this.getActualCount(target);
        }
        return result;
    }

    @Override
    public double getTotalCorrectCount()
    {
        double correct = 0.0;
        for (CategoryType category : this.getActualCategories())
        {
            correct += this.getCount(category, category);
        }
        return correct;
    }

    @Override
    public double getTotalIncorrectCount()
    {
        return this.getTotalCount() - this.getTotalCorrectCount();
    }

    @Override
    public double getActualCount(
        final CategoryType target)
    {
        double result = 0.0;
        for (CategoryType estimate : this.getPredictedCategories(target))
        {
            result += this.getCount(target, estimate);
        }
        return result;
    }

    @Override
    public double getPredictedCount(
        final CategoryType predicted)
    {
        double result = 0.0;
        for (CategoryType actual : this.getActualCategories())
        {
            result += this.getCount(actual, predicted);
        }
        return result;
    }
    
    @Override
    public double getAccuracy()
    {
        return this.getTotalCorrectCount() / this.getTotalCount();
    }

    @Override
    public double getCategoryAccuracy(
        final CategoryType category)
    {
        return this.getCount(category, category) / this.getActualCount(category);
    }

    @Override
    public double getAverageCategoryAccuracy()
    {
        double sum = 0.0;
        int categoryCount = 0;
        for (CategoryType actual : this.getActualCategories())
        {
            if (this.getActualCount(actual) > 0)
            {
                sum += this.getCategoryAccuracy(actual);
                categoryCount++;
            }
        }
        return sum / categoryCount;
    }

    @Override
    public double getErrorRate()
    {
        return 1.0 - this.getAccuracy();
    }

    @Override
    public double getCategoryErrorRate(
        final CategoryType category)
    {
        return 1.0 - this.getCategoryAccuracy(category);
    }

    @Override
    public double getAverageCategoryErrorRate()
    {
        double sum = 0.0;
        int categoryCount = 0;
        for (CategoryType actual : this.getActualCategories())
        {
            if (this.getActualCount(actual) > 0)
            {
                sum += this.getCategoryErrorRate(actual);
                categoryCount++;
            }
        }
        return sum / categoryCount;
    }

}
