/*
 * File:                package-info.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 15, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

/**
 * Provides confidence-weighted categorization algorithms.  These methods are
 * associate uncertainty with the estimated parameters, similar to Bayesian
 * techniques.  However, these techniques are not explicitly Bayesian in
 * their formulation.  In our test batteries, these methods tend to outperform
 * techniques that do not propagate parameter uncertainty in their estimates.
 * @author  Justin Basilico
 * @since   3.0
 */
@gov.sandia.cognition.annotation.Documentation
package gov.sandia.cognition.learning.algorithm.confidence;
