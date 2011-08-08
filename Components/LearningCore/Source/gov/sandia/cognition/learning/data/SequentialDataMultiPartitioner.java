/*
 * File:                SequentialDataMultiPartitioner.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 22, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This partitioner splits a Collection of data into a pre-defined number of
 * approximately equal sequential partitions, with the nonzero remainder
 * elements going into the final partition.  For example, if we have ten
 * data points in a Collection, and we want to split the Collection into
 * three partitions, then the method will return three Collections
 * <OL>
 * <LI>: 1, 2, 3
 * <LI>: 4, 5, 6
 * <LI>: 7, 8, 9, 10
 * </OL>
 * <BR>
 * This is class was primarily intended for splitting datasets for
 * parallelization.
 * 
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class SequentialDataMultiPartitioner
{

    /**
     * Creates a partition of the given data into "numPartition" roughly equal
     * sets, preserving their pre-existing sequential ordering, with the
     * nonzero remainder elements going into the final partition.
     * 
     * @param <DataType> Type of data to partition.
     * @param data Collection of data to partition
     * @param numPartitions Number of partitions to create.
     * @return
     * List of Lists of size data.size()/numPartitions, with the remainder of
     * data elements going into the final partition.
     */
    public static <DataType> ArrayList<ArrayList<DataType>> create(
        Collection<? extends DataType> data,
        int numPartitions )
    {
        
        int numData = data.size();
        int numEach = data.size() / numPartitions;
        ArrayList<ArrayList<DataType>> retval = 
            new ArrayList<ArrayList<DataType>>( numPartitions );
        
        int index = 0;
        Iterator<? extends DataType> iterator = data.iterator();
        for( int n = 0; n < numPartitions; n++ )
        {
            // The remainder goes into the final partition
            int numThis = (n < (numPartitions-1)) ? numEach : (numData-index);
            ArrayList<DataType> partition = new ArrayList<DataType>( numThis );
            
            for( int i = 0; i < numThis; i++ )
            {
                partition.add( iterator.next() );
                index++;
            }
            
            retval.add( partition );
            
        }
        
        return retval;
        
    }
        

}
