/*
 * File:                Categorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Set;

/**
 * The <code>Categorizer</code> interface defines the functionality of an
 * object that can take an input and evaluate what category out of a fixed
 * set of categories it belongs to. The categories are represented as values
 * of type CategoryType.
 * <BR><BR>
 * The term "categorizer" is used here instead of synonymous "classifier" for 
 * two reasons:
 * <BR>
 * <BR>    1) Java uses the term "class" itself to mean something different.
 * <BR>    2) At Sandia "classified" has yet another meaning.
 * <BR><BR>
 * Thus we use "categorizer" because using other words from the same stem is
 * does not have these problems: "category", "categorized", "categorization", 
 * etc.
 *
 * @param  <InputType> The type of the input the categorizer can use.
 * @param  <CategoryType> The type of category output by the categorizer.
 * @author Justin Basilico
 * @since  2.0
 */
public interface Categorizer<InputType, CategoryType>
    extends Evaluator<InputType, CategoryType>,
    CloneableSerializable
{
    /**
     * Gets the list of possible categories that the categorizer can produce.
     * 
     * @return The list of possible categories.
     */
    Set<? extends CategoryType> getCategories();
}
