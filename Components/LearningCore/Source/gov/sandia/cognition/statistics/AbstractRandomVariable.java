/*
 * File:                AbstractRandomVariable.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.AbstractRing;
import gov.sandia.cognition.util.Randomized;
import java.util.Random;

/**
 * Partial implementation of RandomVariable.
 * @param <DataType> 
 * Type of data that can be sampled from the Distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractRandomVariable<DataType>
    extends AbstractRing<RandomVariable<DataType>>
    implements RandomVariable<DataType>,
    Randomized
{    
    
    @Override
    public DataType sample(
        final Random random )
    {
        return CollectionUtil.getFirst( this.sample( random, 1 ) );
    }
    
}
