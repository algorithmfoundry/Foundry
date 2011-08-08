/*
 * File:                ConfidenceTestAssumptions.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 25, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes the assumptions and other information of a statistical confidence
 * test.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface ConfidenceTestAssumptions
{

    /**
     * Most-common name of the confidence test.
     * @return
     * Most-common name of the confidence test.
     */
    String name();

    /**
     * High-level description of the confidence test.
     * @return
     * High-level description of the confidence test.
     */
    String[] description();

    /**
     * Other names that the test may be known as.
     * @return
     * Other names that the test may be known as.
     */
    String[] alsoKnownAs() default "";

    /**
     * Description of the null hypothesis.
     * @return
     * Description of the null hypothesis.
     */
    String nullHypothesis();

    /**
     * Distribution used to evaluate the null hypothesis probability.
     * @return
     * Distribution used to evaluate the null hypothesis probability.
     */
    Class<? extends ClosedFormDistribution<?>> distribution();

    /**
     * List of assumptions of the confidence test.
     * @return
     * List of assumptions of the confidence test.
     */
    String[] assumptions();

    /**
     * Flag if the data must be the same size.
     * @return
     * Flag if the data must be the same size.
     */
    boolean dataSameSize();

    /**
     * Flag is the data are paired together during the test.
     * @return
     * Flag if the data are paired together during the test.
     */
    boolean dataPaired();

    /**
     * A reference to a publication providing a basis for the confidence test.
     *
     * @return
     * A reference to a publication providing a basis for the confidence test.
     */
    PublicationReference[] reference();

}
