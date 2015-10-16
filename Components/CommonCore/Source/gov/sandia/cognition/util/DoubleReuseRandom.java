/*
 * File:                DoubleReuseRandom.java
 * Authors:             Sean Crosby
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 20, 2014, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Random;

/**
 * An extension of java.util.Random that builds a list of random doubles
 * and then serves up the random values from the array, returning to the beginning
 * when the end is reached.  This class 
 * 
 * Only nextDouble() will return these repeated sequences of random numbers.  All
 * other types should work according to the java.util.Random implementation.
 * 
 * This class can be helpful when a program is experiencing slowdown from 
 * frequently calling Random.nextDouble().  This class decreases the mathematical
 * purity of the random number sequence (as the sequence is reused).
 * 
 * Recommendations:
 * 1) make sure the length of the array is different from the number of iterations
 *    of other loops in the program.  This will help avoid situations where the same
 *    numbers are used in the same context of the loop.
 * 2) make the length of the array large enough to guarantee sufficient coverage
 *    of the random number sequence (an array of length 10 would likely have gaps
 *    in the range of numbers)
 * 
 * @author  Sean Crosby
 * @since   3.4.2
 */
public class DoubleReuseRandom extends Random
{
    /** an array of pre-generated random numbers */
    double[] randArray;
    
    /** the current index randArray */
    int randArrayIndex = 0;
    
    /** whether or not the randArray has been filled with random numbers */
    boolean initialized = false;
    
    /**
     * Creates DoubleReuseRandom with the given parameters
     * 
     * @param arrayLen the number of doubles to generate and cycle over
     */
    public DoubleReuseRandom(int arrayLen) {
        super();
        if(arrayLen < 1) {
            throw new RuntimeException("The array must have a length greater than zero");
        }

        // Initialize the array
        randArray =  new double[arrayLen];

        // Set the index high to force an array populate
        randArrayIndex = arrayLen;
    }
    
    /**
     * Creates DoubleReuseRandom with the given parameters
     * 
     * @param arrayLen the number of doubles to generate and cycle over
     * @param seed the seed passed to the underlying Random class
     */
    public DoubleReuseRandom(int arrayLen, long seed) {
        super(seed);
        if(arrayLen < 1) {
            throw new RuntimeException("The array must have a length greater than zero");
        }

        // Initialize the array
        randArray =  new double[arrayLen];

        // Set the index high to force an array populate
        randArrayIndex = arrayLen;
    }
    
    /**
     * Generates the random doubles and puts them in randArray
     */
    private void initialize() {

        for(int i = 0; i < randArray.length; i++) {
            randArray[i] = super.nextDouble();
        }
    }
    
    /**
     * Gets the length of the array of doubles
     * 
     * @return length of the array of doubles
     */
    public int getArrayLen() {
        if(randArray != null) {
            return randArray.length;
        }
        else {
            return 0;
        }
    }
    
    /**
     * Returns the next double from the array
     * 
     * @return random double
     */
    @Override
    public double nextDouble() {
       randArrayIndex++; 
       if(randArrayIndex >=randArray.length) {
           randArrayIndex = 0;
           if(initialized == false) {
               initialize();
               initialized = true;
           }
       }

       return randArray[randArrayIndex];
    }
}
