/*
 * File:                SoftwareReference.java
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
 * Describes a reference to software. The purpose of this annotation is to 
 * provide a reference to a piece of software so that someone would be able
 * to find it on the Internet using a search engine. This is typically used 
 * when a class wraps or uses some third-party library.
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
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SoftwareReference
{

    /**
     * The full name of the software.
     * 
     * @return
     *      The full name of the software.
     */
    String name();

    /**
     * The specific version of the software that is used. For example,
     * {@code "1.6.0"}.
     * 
     * @return
     *      The specific version of the software.
     */
    String version();

    /**
     * An optional date on which the reference was made. Useful for projects 
     * whose versions do not make much sense, or to locate the exact version 
     * used. Defaults to {@code ""}.
     * 
     * @return
     *      The date on which the software reference was made.
     */
    String date() default "";

    /**
     * An optional URL reference to where the software can be found. Typically,
     * the home-page for the software. Defaults to {@code ""}.
     * 
     * @return
     *      A URL where the software can be found.
     */
    String url() default "";

    /**
     * The type of license for the software. If the selection is {@code Other},
     * please specify in the {@code notes} section.
     * 
     * @return
     *      The type of license for the software.
     */
    SoftwareLicenseType license();

    /**
     * An optional version for the license. Defaults to {@code ""}.
     * 
     * @return
     *      The version for the license.
     */
    String licenseVersion() default "";

    /**
     * An optional URL for the software license. Defaults to {@code ""}.
     * 
     * @return
     *      A URL where the software license can be found.
     */
    String licenseURL() default "";

    /**
     * Optional notes regarding this reference.
     * 
     * @return
     *      Optional notes regarding this reference.
     */
    String[] notes() default "";

}
