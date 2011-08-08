/*
 * File:                SoftwareLicenseType.java
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

/**
 * An enumeration of common types of software licenses. This is not meant to
 * be exhaustive, but instead enumerate common software licenses (particularly
 * open source ones) for use with the {@code SoftwareReference} annotation.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public enum SoftwareLicenseType
{
    /**
     * For public domain software.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Public domain",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Public_domain#No_legal_restriction_on_use",
        notes="The nonspecific license when work is posted for anybody to use without restriction."
    )
    PublicDomain,
    
    /**
     * For software licensed under the Berkeley Software Distribution (BSD)
     * license.
     */
    @PublicationReference(
        author="Regents of the University of California",
        title="The BSD License",
        type=PublicationType.WebPage,
        year=1998,
        url="http://www.opensource.org/licenses/bsd-license.php",
        notes="Open Source Initiative Approved license"
    )
    BSD,
    
    /**
     * For software licensed under the Apache License.
     */
    @PublicationReference(
        author="Apache Software Foundation",
        title="Apache License, Version 2.0",
        type=PublicationType.WebPage,
        year=2004,
        url="http://www.apache.org/licenses/LICENSE-2.0",
        notes="Open Source Initiative Approved license"
    )
    Apache,
    
    /**
     * For software licensed under the Common Development and Distribution 
     * License (CDDL), or similar licenses such as the Mozilla Public License
     * (MPL) or Eclipse License.
     */
    @PublicationReference(
        author="Sun Microsystems",
        title="Common Development and Distribution License (CDDL) Information",
        type=PublicationType.WebPage,
        year=2004,
        url="http://www.sun.com/cddl/",
        notes="Open Source Initiative Approved license"
    )
    CDDL, 
    
    /**
     * For software licensed under the Lesser GNU Public License (LGPL), or
     * similar licenses.
     */
    @PublicationReference(
        author="Free Software Foundation, Inc.",
        title="GNU Lesser General Public License, Version 3",
        type=PublicationType.WebPage,
        year=2007,
        url="http://www.gnu.org/licenses/lgpl.html",
        notes="Open Source Initiative Approved license"
    )
    LGPL,
    
    /**
     * For software licensed under the GNU Public License (GPL).
     */
    @PublicationReference(
        author="Free Software Foundation, Inc.",
        title="GNU General Public License, Version 3",
        type=PublicationType.WebPage,
        year=2007,
        url="http://www.gnu.org/copyleft/gpl.html",
        notes={
            "Open Source Initiative Approved license",
            "DO NOT USE THIS LICENSE IN THE FOUNDRY, AS IT ACTS LIKE A VIRUS!"
        }
    )
    GPL,
    
    /**
     * For software licensed under a commercial license.
     */
    Commercial,
    
    /**
     * For software licensed under a specific tailored license for that
     * software package.
     */
    Custom,
    
    /**
     * For any other software licenses.
     */
    Other
}
