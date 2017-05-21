package com.bsu.avizhen.services;

/**
 * Created by Александр on 19.12.2016.
 */
public interface PlagiarismDetector {
    double getPlagiarismCoefficient(String tokenizeSrc, String uniqueTokenizeSrc);
}
