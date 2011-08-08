/*
 * File:                PublicationType.java
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

/**
 * The {@code PublicationType} enumeration lists off the possible types of 
 * publications for a {@code PublicationReference} annotation.
 * 
 * @author  Kevin R. Dixon
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Looks fine."
)
public enum PublicationType
{

    /**
     * A publication published as a book.
     * For example, {@code "War and Peace"} or 
     * {@code "Artificial Intelligence: An Awesome Approach"}.
     */
    Book,
    
    /**
     * A publication published in a scholarly journal.
     * For example, {@code "Nature"} or {@code "IEEE Transactions on Stuff"}.
     */
    Journal,
    
    /**
     * A publication published as a technical report, typically by a research
     * organization. For example, a Sandia SAND Report or the CMU Computer
     * Science series.
     */
    TechnicalReport,
    
    /**
     * A publication published as a thesis by a graduate student.
     */
    Thesis,
    
    /**
     * A publication in the proceedings of a conference.
     */
    Conference,
    
    /**
     * A publication published as a chapter in a book. Usually an edited 
     * volume.
     */
    BookChapter,
    
    /**
     * A publication published only as a web page.
     */
    WebPage,
    
    /**
     * A publication published in a way that does not fit into the other 
     * reference types.
     */
    Misc

}
