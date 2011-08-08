/*
 * File:                ComputableDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 24, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * A type of Distribution that has an associated distribution function,
 * either a PDF or PMF.
 * @param <DomainType> Domain type of the Distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ComputableDistribution<DomainType>
    extends Distribution<DomainType>
{

    /**
     * Gets the distribution function associated with this Distribution,
     * either the PDF or PMF.
     * @return
     * Distribution function associated with this Distribution.
     */
    public ProbabilityFunction<DomainType> getProbabilityFunction();
    
}
