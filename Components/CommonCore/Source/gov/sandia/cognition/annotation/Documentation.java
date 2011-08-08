/*
 * File:                Documentation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 14, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code Documentation} annotation marks that something is just 
 * documentation.
 * 
 * Why does this exist?
 * 
 * This annotation was created to deal with an issue regarding the 
 * "package-info.java" files, which is the preferred place for package level
 * documentation according to the Javadoc 1.5 specification. The problem is
 * that somewhere in our build system (Javac, Ant, NetBeans), it determines
 * that the "package-info.java" file needs to be compiled every time. This is
 * usually due to the fact that by default a "package-info.java" file generates
 * no "package-info.class" file. However, if there is an annotation on the
 * package, which is specified in the "package-info.java" file, then it must
 * create a corresponding "package-info.class". Thus, if you add this annotation
 * to the package, it will remove the compilation problems.
 *
 * @author  Justin Basilico
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Looks fine. (Nothing much to review on this one.)"
)
@Retention(RetentionPolicy.RUNTIME)
public @interface Documentation
{
}
