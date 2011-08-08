/*
 * File:                AbstractTemporal.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Date;


/**
 * Partial implementation of Temporal
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractTemporal
    extends AbstractCloneableSerializable
    implements Temporal
{
    
    /**
     * Time associated with the value
     */
    protected Date time;

    /** 
     * Creates a new instance of AbstractTemporal 
     */
    public AbstractTemporal()
    {
        this( null );
    }

    /**
     * Creates a new instance of AbstractTemporal 
     * @param time
     * Time associated with the value
     */
    public AbstractTemporal(
        Date time )
    {
        this.setTime( time );
    }

    @Override
    public AbstractTemporal clone()
    {
        AbstractTemporal clone = (AbstractTemporal) super.clone();
        clone.setTime( ObjectUtil.cloneSmart( this.getTime() ) );
        return clone;
    }

    public int compareTo(
        Temporal o )
    {
        return this.getTime().compareTo( o.getTime() );
    }

    public Date getTime()
    {
        return time;
    }

    /**
     * Sets the time of the Temporal
     * @param time
     * Time associated with the value
     */
    protected void setTime(
        Date time )
    {
        this.time = time;
    }
    
}
