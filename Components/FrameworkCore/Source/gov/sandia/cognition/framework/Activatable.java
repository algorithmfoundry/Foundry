/*
 * File:                Activatable.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The Activatable interface provides a general definition for objects that can
 * be isActivated.
 * 
 * 
 * @author Jonathan McClain
 * @since 1.0
 */
public interface Activatable
{
    /**
     * Returns whether or not the object is isActivated.
     * 
     * 
     * @return True if isActivated, false otherwise.
     */
    public boolean isActivated();
}
