package com.bsu.avizhen.services.impl;

import com.bsu.avizhen.services.Tokenizer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Александр on 18.12.2016.
 */
@Service
public class TokenizerImpl implements Tokenizer {
    private List<String> javaKeyWords = new ArrayList<>();
    private List<String> javaOperatorWords = new ArrayList<>();
    private String operatorString = "=,==,+,-,*,/,%,{,},>,<,!,~,^,|,<=,>=,!=,";


    public TokenizerImpl() {
        String fileWithKeyWordPath = "C:\\Users\\Александр\\Documents\\IDEA projects\\CourseProject\\src\\main\\resources\\javaKeyWords.txt";
        javaOperatorWords.addAll(Arrays.asList(operatorString.split(",")));
        try {
            Scanner scanner = new Scanner(new File(fileWithKeyWordPath));
            while (scanner.hasNext()) {
                javaKeyWords.add(scanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTokenizeSourceCode(String sourceCode) {
        StringBuilder tokenizeSourceCode = new StringBuilder();
        sourceCode = sourceCode.replaceAll("\\s+", " ");
        String wordArray[] = sourceCode.split(" ");
        int k = 0;
        while(k < wordArray.length) {
            if (Pattern.matches("\\d+;?", wordArray[k])) {
                tokenizeSourceCode.append("N");
            } else if (wordArray[k].equals("=")) {
                tokenizeSourceCode.append("O");
                k++;
                boolean isLastWorldInLine = false;
                while (k < wordArray.length && !isLastWorldInLine) {
                    if (wordArray[k].endsWith(";")) {
                        isLastWorldInLine = true;
                    }
                    if (Pattern.matches("\\d+;?", wordArray[k])) {
                        tokenizeSourceCode.append("N");
                    } else if (Pattern.matches(".*\\(.+\\);", wordArray[k])) {
                        tokenizeSourceCode.append("KV");
                    } else if (javaOperatorWords.contains(wordArray[k])) {
                        tokenizeSourceCode.append("O");
                    } else {
                        tokenizeSourceCode.append("K");
                    }
                    k++;
                }
                k--;
            } else if (javaOperatorWords.contains(wordArray[k])) {
                tokenizeSourceCode.append("O");
            } else if (javaKeyWords.contains(wordArray[k])) {
                tokenizeSourceCode.append("K");
            }else {
                tokenizeSourceCode.append("I");
            }
            k++;
        }
        return tokenizeSourceCode.toString();
    }
}
