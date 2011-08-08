/*
 * File:                WeightedPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 12, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 * 
 */

package gov.sandia.cognition.util;

/**
 * The {@code WeightedPair} interface defines an extension of a normal 
 * {@code Pair} that includes an additional weight.
 * 
 * @param   <FirstType> Type of the first object in the pair.
 * @param   <SecondType> Type of the second object in the pair.
 * @author  Justin Basilico
 * @since   2.0
 */
public interface WeightedPair<FirstType, SecondType>
    extends Pair<FirstType, SecondType>, Weighted
{
}
