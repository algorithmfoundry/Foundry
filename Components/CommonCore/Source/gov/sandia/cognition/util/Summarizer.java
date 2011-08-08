/*
 * File:                Summarizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import java.util.Collection;

/**
 * The {@code Summarizer} interface defines the functionality of an object that
 * can take a collection of some data and return a summary of that data. Note
 * that this interface does not require that an implementing class also
 * implements {@code CloneableSerializable}, but most implementing classes
 * should also implement that interface.
 *
 * @param <DataType> Data from which to build the summary
 * @param <SummaryType> Output class that summarizes the data
 * @author Justin Basilico
 * @since  2.0
 */
public interface Summarizer<DataType, SummaryType>
{

    /**
     * Creates a summary of the given collection of data.
     *
     * @param  data The collection of data to summarize.
     * @return The summary of the data.
     */
    SummaryType summarize(
        Collection<? extends DataType> data );

}
