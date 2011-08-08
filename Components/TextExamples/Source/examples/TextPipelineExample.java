/*
 * File:                TextPipelineExample.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 02, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package examples;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.text.Textual;
import gov.sandia.cognition.text.convert.CommonDocumentTextualConverterFactory;
import gov.sandia.cognition.text.convert.DocumentSingleFieldConverter;
import gov.sandia.cognition.text.document.Document;
import gov.sandia.cognition.text.document.Field;
import gov.sandia.cognition.text.document.extractor.TextDocumentExtractor;
import gov.sandia.cognition.text.term.DefaultTermIndex;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.filter.TermFilter;
import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.term.filter.LowerCaseTermFilter;
import gov.sandia.cognition.text.term.vector.BagOfWordsTransform;
import gov.sandia.cognition.text.term.vector.weighter.CommonTermWeighterFactory;
import gov.sandia.cognition.text.term.vector.weighter.CompositeLocalGlobalTermWeighter;
import gov.sandia.cognition.text.token.LetterNumberTokenizer;
import gov.sandia.cognition.text.token.Token;
import gov.sandia.cognition.text.token.Tokenizer;
import java.net.URI;
import java.net.URL;

/**
 * An example of a typical text processing pipeline. It loads a document,
 * pulls out the body field,  tokenizes it, filters terms, converts it to
 * counts (bag-of-words).
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class TextPipelineExample
    extends Object
{
    
    /**
     * Runs the example.
     * 
     * @param   args Ignored. The example has no arguments.
     * @throws  Exception If there is an error.
     */
    public static void main(
        final String[] args)
        throws Exception
    {
        // To start out with we get the example file as a URI.
        final String exampleFileName = "examples/example.txt";
        final URL exampleFileURL = ClassLoader.getSystemResource(exampleFileName);
        final URI exampleFileURI = exampleFileURL.toURI();

        // Now we extract the document from the URI.
        final TextDocumentExtractor extractor = new TextDocumentExtractor();
        final Document document = extractor.extractDocument(exampleFileURI);

        // Here we can inspect all of the fields that were extracted.
        System.out.println("Fields:");
        for (Field field : document.getFields())
        {
            System.out.println("    Field: " + field.getName());
            System.out.println("    Content: " + field.getText());
        }

        // Next we convert the document to a textual representation by 
        // extracting the text field.
        final DocumentSingleFieldConverter converter =
            CommonDocumentTextualConverterFactory.createBodyConverter();
        final Textual text = converter.convert(document);

        // Next we tokenize the text.
        final Tokenizer tokenizer = new LetterNumberTokenizer();
        final Iterable<Token> tokens = tokenizer.tokenize(text);

        // Here we can look at all the tokens.
        System.out.println("Tokens:");
        for (Token token : tokens)
        {
            System.out.println("    " + token.getText());
        }

        // Next we filter the tokens. Note that tokens are an extension of term
        // occurences.
        Iterable<? extends TermOccurrence> terms = tokens;
        final TermFilter[] filters =
        {
            new LowerCaseTermFilter()
        };

        for (TermFilter filter : filters)
        {
            terms = filter.filterTerms(terms);
        }
        
        System.out.println("Term Occurences:");
        for (TermOccurrence term : terms)
        {
            System.out.println("    " + term.getTerm());
        }
        
        // Next we index the terms.
        final DefaultTermIndex termIndex = new DefaultTermIndex();
        termIndex.addAll(terms);

        // Next we transform the terms to a bag of words.
        final BagOfWordsTransform bagOfWords = new BagOfWordsTransform(termIndex);
        final Vector counts = bagOfWords.convertToVector(terms);

        System.out.println("Term Counts:");
        for (int i = 0; i < termIndex.getTermCount(); i++)
        {
            final Term term = termIndex.getTerm(i);
            final double count = counts.getElement(i);
            System.out.println("    " + i + " (" + term + "): " + count);
        }

        // Next we do a TF-IDF transform.
        final CompositeLocalGlobalTermWeighter tfidf = CommonTermWeighterFactory.createTFIDFWeighter();
        tfidf.getGlobalWeighter().add(counts);
        
        // This just makes the global weights non-zero.
        Vector zeroDocument = counts.clone();
        zeroDocument.zero();
        tfidf.getGlobalWeighter().add(zeroDocument);
        
        final Vector weighted = tfidf.evaluate(counts);
        System.out.println("Term Weights:");
        for (int i = 0; i < termIndex.getTermCount(); i++)
        {
            final Term term = termIndex.getTerm(i);
            final double count = weighted.getElement(i);
            System.out.println("    " + i + " (" + term + "): " + count);
        }
    }

}
