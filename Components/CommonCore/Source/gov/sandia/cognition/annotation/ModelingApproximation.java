/*
 * File:                ModelingApproximation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 14, 2008, Sandia Corporation.
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
 * The {@code ModelingApproximation} annotation indicates that an approximation
 * is being used in modeling a component. It contains the person who originally
 * made the approximation in the code, a description of the approximation, and
 * a reference to a publication that provides the basis for the approximation.
 *
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @author  Zachary Benz
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Interface looks fine."
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelingApproximation
{

    /**
     * The full name originator who is making the approximation.
     * For example, {@code "Justin Basilico"}. This is the person to contact
     * with questions about the approximation.
     * 
     * @return
     *      The full name of the originator of the approximation.
     */
    String[] originator();

    /**
     * A description of the approximation.
     * 
     * @return
     *      A description of the approximation.
     */
    String[] description();

    /**
     * A reference to a publication providing a basis for the modeling
     * assumption.
     * 
     * @return
     *      A reference to a publication providing the basis for the modeling
     *      assumption.
     */
    PublicationReference[] reference();

}
