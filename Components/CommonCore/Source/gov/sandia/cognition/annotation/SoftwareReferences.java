/*
 * File:                SoftwareReferences.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 23, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code SoftwareReferences} annotation defines a container for one or
 * more references to a publication. This container annotation is needed because 
 * Java allows only one instance of an annotations per element.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments="Looks good."
)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SoftwareReferences
{

    /**
     * One or more {@code SoftwareReferences} annotations.
     * 
     * @return
     *      One or more {@code SoftwareReferences} annotations.
     */
    SoftwareReference[] references();

}