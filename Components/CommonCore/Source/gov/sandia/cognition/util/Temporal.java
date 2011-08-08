/*
 * File:                Temporal.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 28, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Date;

/**
 * The {@code Temporal} interface defines the functionality of an object that
 * has a time associated with it.
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public interface Temporal
    extends Comparable<Temporal>
{
    /**
     * The time associated with this object.
     * 
     * @return The time associated with this object.
     */
    public Date getTime();
}
