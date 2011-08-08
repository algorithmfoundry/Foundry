/*
 * File:                DefaultDateField.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document;

import java.text.DateFormat;
import java.util.Date;

/**
 * A field for storing a date.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultDateField
    extends AbstractField
{

    /** The date stored in the field. */
    protected Date date;

    /**
     * Creates a new, empty {@code DefaultDateField}.
     */
    public DefaultDateField()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code DefaultDateField}.
     *
     * @param   name
     *      The name of the field.
     * @param   date
     *      The date to store in the field.
     */
    public DefaultDateField(
        final String name,
        final Date date)
    {
        super(name);

        this.setDate(date);
    }

    public String getText()
    {
        if (this.date == null)
        {
            // Return null text for a null date.
            return null;
        }
        else
        {
            // Format the date according to the local format.
            return DateFormat.getDateTimeInstance().format(this.date);
        }
    }

    /**
     * Gets the date stored in the field.
     *
     * @return
     *      The date stored in the field.
     */
    public Date getDate()
    {
        return this.date;
    }

    /**
     * Sets the date stored in the field.
     *
     * @param   date
     *      The date stored in the field.
     */
    public void setDate(
        final Date date)
    {
        this.date = date;
    }

}
