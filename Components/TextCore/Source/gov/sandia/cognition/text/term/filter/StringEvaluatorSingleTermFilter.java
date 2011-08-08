/*
 * File:                StringEvaluatorSingleTermFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermOccurrence;

/**
 * Adapts an evaluator from string to string to be a term filter on individual
 * terms.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class StringEvaluatorSingleTermFilter
    extends AbstractSingleTermFilter
{

    /** The evaluator to adapt. */
    protected Evaluator<String, String> evaluator;

    /**
     * Creates a new {@code StringEvaluatorSingleTermFilter} with a null
     * evaluator.
     */
    public StringEvaluatorSingleTermFilter()
    {
        this(null);
    }

    /**
     * Creates a new {@code StringEvaluatorSingleTermFilter} with a given
     * evaluator.
     *
     * @param   evaluator
     *      The evaluator to use.
     */
    public StringEvaluatorSingleTermFilter(
        final Evaluator<String, String> evaluator)
    {
        super();

        this.setEvaluator(evaluator);
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurrence)
    {
        // Get the input string.
        final String input = occurrence.getTerm().getName();

        // Evaluate to get the output.
        final String output = this.getEvaluator().evaluate(input);

        if (output == null)
        {
            // Bad output.
            return null;
        }
        else
        {
            // Create the new term.
            final Term term = new DefaultTerm(output);
            return new DefaultTermOccurrence(term,
                occurrence.getStart(), occurrence.getLength());
        }
    }

    /**
     * Gets the evaluator being used as a filter.
     *
     * @return
     *      The evaluator.
     */
    public Evaluator<String, String> getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * Sets the evaluator being used as a filter.
     *
     * @param   evaluator
     *      The evaluator.
     */
    public void setEvaluator(
        final Evaluator<String, String> evaluator)
    {
        this.evaluator = evaluator;
    }

}
