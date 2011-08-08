/*
 * File:                PublicationReference.java
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
 * The {@code PublicationReference} annotation describes a reference to a 
 * publication from a journal, conference, etc. The purpose of this annotation
 * is to provide a reference to a publication so that someone would be able to
 * find it on the Internet using a search engine; not to automatically produce
 * a full bibliography that contains all the data that publishers require.
 * 
 * @author  Kevin R. Dixon
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments="Interface looks fine."
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublicationReference
{

    /**
     * The full names of the author(s) of the publication. For example, 
     * {@code { "Kevin R. Dixon", "Justin D. Basilico" }}.
     * 
     * @return
     *      The full names of the author(s).
     */
    String[] author();

    /**
     * The title of the publication. For example, 
     * {@code "Cognitive Foundry: How did it become perfect?"}.
     * 
     * @return
     *      The title of the publication.
     */
    String title();

    /**
     * The type of the publication. For example, Journal, Conference, Book.
     * 
     * @return
     *      The type of the publication.
     * @see PublicationType
     */
    PublicationType type();

    /**
     * The year of publication.
     * 
     * @return
     *      The year of publication.
     */
    int year();

    /**
     * The title of the larger publication where the article was published.
     * For example, {@code "Learning Kernel Classifiers"} or
     * {@code "Proceedings of the Twenty-First International Conference on Machine Learning (ICML)"}.
     * 
     * @return
     *      The title of the larger publication where the article was published.
     */
    String publication()  default "";

    /**
     * The pages on which the publication can be found. 
     * For example, {@code {10, 100}}.
     * 
     * @return
     *      The range of pages on which the publication can be found.
     */
    int[] pages()  default 0;

    /**
     * An optional URL reference where the publication can be found. Defaults
     * to {@code ""}.
     * 
     * @return
     *      A URL where the reference can be found. 
     */
    String url()  default "";

    /**
     * Optional notes regarding this reference.
     * 
     * @return
     *      Optional notes regarding this reference.
     */
    String[] notes()  default "";

}
