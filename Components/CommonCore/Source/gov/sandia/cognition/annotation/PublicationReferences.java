/*
 * File:                PublicationReferences.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 11, 2008, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code PublicationReferences} annotation defines a container for one or
 * more references to a publication. This container annotation is needed because 
 * Java allows only one instance of an annotations per element.
 * 
 * @author  Kevin R. Dixon
 * @author  Justin D. Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Interface looks fine."
)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicationReferences
{

    /**
     * One or more {@code PublicationReference} annotations.
     * 
     * @return
     *      One or more {@code PublicationReference} annotations.
     */
    PublicationReference[] references();

}
