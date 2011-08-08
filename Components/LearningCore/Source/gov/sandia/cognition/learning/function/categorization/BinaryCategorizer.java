/*
 * File:                BinaryCategorizer.java
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

/**
 * The {@code BinaryCategorizer} extends the {@code Categorizer}
 * interface by enforcing that the output categories are boolean values, which
 * means that the categorizer is performing binary categorization.
 *
 * @param  <InputType> The input type for the categorizer.
 * @author Justin Basilico
 * @since  2.0
 */
public interface BinaryCategorizer<InputType>
    extends Categorizer<InputType, Boolean>
{
}
