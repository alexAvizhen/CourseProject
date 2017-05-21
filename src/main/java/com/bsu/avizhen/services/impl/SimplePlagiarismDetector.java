package com.bsu.avizhen.services.impl;

import com.bsu.avizhen.services.PlagiarismDetector;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Александр on 19.12.2016.
 */
@Service(value = "simpleDetector")
public class SimplePlagiarismDetector implements PlagiarismDetector {

    public SimplePlagiarismDetector() {
    }

    @Override
    public double getPlagiarismCoefficient(String tokenizeSrc, String uniqueTokenizeSrc) {
        int numberOfCommonToken = 4;
        Set<String> verifiableSet = new TreeSet<>();
        Set<String> uniqueSrcSet = new TreeSet<>();
        for (int i = 0; i < tokenizeSrc.length() - 4; i++) {
            verifiableSet.add(tokenizeSrc.substring(i, i + 4));
        }
        for (int i = 0; i < uniqueTokenizeSrc.length() - 4; i++) {
            uniqueSrcSet.add(uniqueTokenizeSrc.substring(i, i + 4));
        }
        int commonGramms = 0;
        for (String s : verifiableSet) {
            if (uniqueSrcSet.contains(s)) {
                commonGramms++;
            }
        }
        if ((verifiableSet.size() + uniqueSrcSet.size() == 0)) {
            return 0.0;
        }
        return ((double) commonGramms) / ((verifiableSet.size() - commonGramms) + uniqueSrcSet.size());

    }
}
