package com.bsu.avizhen.services.impl;

import com.bsu.avizhen.services.PlagiarismDetector;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Александр on 04.04.2017.
 */
@Service(value = "fingerPrintsDetector")
public class FingerPrintsPlagiarismDetector implements PlagiarismDetector {
    private int k = 7;
    private int t = 8;

    public FingerPrintsPlagiarismDetector() {
    }

    @Override
    public double getPlagiarismCoefficient(String tokenizeSrc, String uniqueTokenizeSrc) {
        Set<Integer> uniqueSrcSet = new HashSet<>(screenHashCodes(getHashCodesByTokenizeStr(uniqueTokenizeSrc)));
        Set<Integer> verifiableSet = new HashSet<>(screenHashCodes(getHashCodesByTokenizeStr(tokenizeSrc)));
        int amountOfCommonHashCodes = 0;
        for (Integer hashCode : verifiableSet) {
            if (uniqueSrcSet.contains(hashCode)) {
                amountOfCommonHashCodes++;
            }
        }
        if ((verifiableSet.size() + uniqueSrcSet.size() == 0)) {
            return 0.0;
        }
        return ((double) amountOfCommonHashCodes) / Math.max(verifiableSet.size(),uniqueSrcSet.size());

    }

    private List<Integer> getHashCodesByTokenizeStr(String tokenizeStr) {
        List<Integer> hashCodes = new ArrayList<>();
        int r = k;
        while (r <= tokenizeStr.length()) {
            hashCodes.add(tokenizeStr.substring(r - k, r).hashCode());
            r++;
        }
        return hashCodes;
    }

    private List<Integer> screenHashCodes(List<Integer> hashCodes) {
        int windowWidth = t - k + 1;
        int r = t - k + 1;
        List<Integer> screenCodes = new ArrayList<>();
        while (r <= hashCodes.size()) {
            int min = Collections.min(hashCodes.subList(r - windowWidth, r));
            if (screenCodes.isEmpty() || screenCodes.get(screenCodes.size() - 1) != min) {
                screenCodes.add(min);
            }
            r++;
        }
        return screenCodes;
    }
}
