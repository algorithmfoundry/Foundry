/*
 * File:                ConfusionMatrix.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 11, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Set;

/**
 * An interface for a general confusion matrix, which is used to tabulate
 * a set of actual category values against the values predicted for those
 * categories. The entries in the matrix are non-negative counts of the number
 * of occurrences
 *
 * @param   <CategoryType>
 *      The type of the categories the confusion matrix is computed over.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface ConfusionMatrix<CategoryType>
    extends CloneableSerializable
{

    /**
     * Adds a count of one to the matrix entry for the (actual, predicted)
     * pair.
     *
     * @param   actual
     *      The actual category.
     * @param   predicted
     *      The predicted category.
     */
    public void add(
        final CategoryType actual,
        final CategoryType predicted);

    /**
     * Adds a given value to the matrix entry for the (actual, predicted)
     * pair.
     *
     * @param   actual
     *      The actual category.
     * @param   predicted
     *      The predicted category.
     * @param   value
     *      The value to add.
     */
    public void add(
        final CategoryType actual,
        final CategoryType predicted,
        final double value);

    /**
     * Adds all of the values in the given confusion matrix to this confusion
     * matrix.
     *
     * @param   <OtherType>
     *      The type of values in the other matrix, which must extend the type
     *      of value in this matrix. Typically these will be the same.
     * @param   other
     *      The other confusion matrix to add all of its values to this one.
     */
    public <OtherType extends CategoryType> void addAll(
        final ConfusionMatrix<OtherType> other);

    /**
     * Gets the entry in the matrix for the given actual and predicted
     * categories, which is the count of the number of times the predicted
     * category was given for the given actual category.
     *
     * @param   actual
     *      The actual category.
     * @param   predicted
     *      The predicted category.
     * @return
     *      The count for (actual, predicted).
     */
    public double getCount(
        final CategoryType actual,
        final CategoryType predicted);

    /**
     * Gets the total number of entries for the given actual category.
     *
     * @param   actual
     *      The actual category.
     * @return
     *      The total number of instances of the given actual category.
     */
    public double getActualCount(
        final CategoryType actual);

    /**
     * Gets the total number of entries for the given predicted category.
     *
     * @param   predicted
     *      The predicted category.
     * @return
     *      The total number of instances of the given predicted category.
     */
    public double getPredictedCount(
        final CategoryType predicted);

    /**
     * Empties out all the data in this confusion matrix.
     */
    public void clear();

    /**
     * Gets whether or not the matrix is empty. This is equivalent to having a
     * total count of zero.
     *
     * @return
     *      True if the matrix is empty; otherwise, false.
     */
    public boolean isEmpty();

    /**
     * Gets the total number of entries in the confusion matrix. It is the sum
     * of all counts.
     *
     * @return
     *      The total number of entries in the confusion matrix.
     */
    public double getTotalCount();

    /**
     * Gets the total number of correct entries in the confusion matrix. It is
     * the sum of the "diagonal" elements of the matrix. Must be greater than
     * or equal to zero and less than or equal to the total count.
     *
     * @return
     *      The total number of correct predictions.
     */
    public double getTotalCorrectCount();

    /**
     * Gets the total number of incorrect entries in the confusion matrix. It
     * is the sum of all the "non-diagonal" elements of the matrix. Must be
     * greater than or equal to zero and less than or equal to the total
     * count. Equal to {@code getTotalCount() - getTotalCorrectCount()}.
     * 
     * @return
     *      The total number of incorrect predictions.
     */
    public double getTotalIncorrectCount();

    /**
     * The accuracy value of the entire confusion matrix. It is the sum of
     * counts where the actual and predicted value are the same, divided by the
     * total number of entries in the matrix. It is equivalent to:
     * 1 - errorRate.
     *
     * @return
     *      The accuracy value for the confusion matrix, which is between 0.0
     *      and 1.0.
     */
    public double getAccuracy();

    /**
     * The category accuracy value for the confusion matrix. It is the number
     * of predicted values that equal the actual values for the given category
     * divided by the number of values for the category.
     *
     * @param   category
     *      The category to get the accuracy for.
     * @return
     *      The category accuracy, which is between 0.0 and 1.0.
     */
    public double getCategoryAccuracy(
        final CategoryType category);

    /**
     * The average accuracy value for the categories in the confusion
     * matrix. This is different than the accuracy in that each category is
     * given equal weight in the accuracy calculation. Only actual categories
     * that have a count greater than 0 are included.
     *
     * @return
     *      The average category accuracy, which is between 0.0 and 1.0.
     */
    public double getAverageCategoryAccuracy();

    /**
     * The error rate of the entire confusion matrix. It sum of counts where
     * the actual value is not equal to the predicted value, divided by the
     * total number of entries in the matrix. It is equivalent to: 1 - accuracy.
     *
     * @return
     *      The error rate for the confusion matrix, which is between 0.0 and
     *      1.0.
     */
    public double getErrorRate();

    /**
     * The category error rate for the confusion matrix. It is the number
     * of predicted values that equal the actual values for the given category
     * divided by the number of values for the category.
     *
     * @param   category
     *      The category to get the error rate for.
     * @return
     *      The category error rate, which is between 0.0 and 1.0.
     */
    public double getCategoryErrorRate(
        final CategoryType category);

    /**
     * The average error rate for the actual categories in the confusion
     * matrix. This is different than the error rate in that each category is
     * given equal weight in the error rate calculation. Only actual categories
     * that have a count greater than 0 are included.
     *
     * @return
     *      The average category error rate, which is between 0.0 and 1.0.
     */
    public double getAverageCategoryErrorRate();

    /**
     * Gets the set of all categories in the confusion matrix. It is the union
     * of the predicted and actual categories.
     *
     * @return
     *      The set of all categories.
     */
    public Set<CategoryType> getCategories();

    /**
     * Gets the set of all the actual categories.
     *
     * @return
     *      The set of all actual categories.
     */
    public Set<CategoryType> getActualCategories();

    /**
     * Gets the set of all the predicted categories.
     *
     * @return
     *      The set of all predicted categories.
     */
    public Set<CategoryType> getPredictedCategories();

    /**
     * Gets the predicted categories for a given actual category. This can
     * be either a sparse set (non-zero values) or a dense set (zeros allowed).
     * However, the sparse set will usually yield higher performance in most
     * use cases.
     *
     * @param   actual
     *      The actual category to get the set of prediction categories for.
     * @return
     *      The set of predicted categories for the given actual categories.
     */
    public Set<CategoryType> getPredictedCategories(
        final CategoryType actual);

}
