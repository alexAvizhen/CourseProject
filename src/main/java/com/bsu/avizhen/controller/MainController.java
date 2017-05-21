package com.bsu.avizhen.controller;

import com.bsu.avizhen.params.UploadForm;
import com.bsu.avizhen.services.PlagiarismDetector;
import com.bsu.avizhen.services.Tokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Александр on 08.12.2016.
 */
@Controller
@SessionAttributes({"filesForm"})
public class MainController {
    @Autowired
    private Tokenizer tokenizer;

    @Autowired
    @Qualifier("fingerPrintsDetector")
    private PlagiarismDetector plagiarismDetector;

    @Autowired
    @Qualifier("simpleDetector")
    private PlagiarismDetector simplePlagiarismDetector;

    private String plagiarismSelectionStyle = "color:red;";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcomePage(ModelMap model) {
        return "index";
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public String checkSourceCode(ModelMap model, @RequestParam(name = "verifiableSrc") String verifiableSrc,
                                  @RequestParam(name = "uniqueSrc") String uniqueSrc,
                                  @ModelAttribute UploadForm form,
                                  @RequestParam(name = "minPlagiarismCoef") Double minPlagiarismCoef,
                                  RedirectAttributes redirectAttributes) throws IOException {
        if (minPlagiarismCoef == null) {
            minPlagiarismCoef = 0.3;
        }
        if (form.isFileUploaded()) {
            model.addAttribute("filesForm", form);
            StringBuilder resultStr = new StringBuilder();
            for (MultipartFile verifiableFile : form.getVerifiableFiles()) {
                for (MultipartFile uniqueFile : form.getUniqueFiles()) {
                    String uniqueTokenizeSrc = tokenizer.getTokenizeSourceCode(getFileText(uniqueFile));
                    String verifiableTokenizeSrc = tokenizer.getTokenizeSourceCode(getFileText(verifiableFile));
                    double plagiarismCoeff = plagiarismDetector.getPlagiarismCoefficient(verifiableTokenizeSrc, uniqueTokenizeSrc);
                    if (plagiarismCoeff >= minPlagiarismCoef) {
                        String link = " <a href='/show?verifiableFileName=" + verifiableFile.getOriginalFilename() +
                                "&uniqueFileName=" + uniqueFile.getOriginalFilename() + "'>Show codes</a>";
                        resultStr.append(verifiableFile.getOriginalFilename() + " to " + uniqueFile.getOriginalFilename() + ": "
                                + plagiarismCoeff + link + "<br>");
                    }
                }
            }
            model.addAttribute("result", resultStr.toString());
        } else {
            String verifiableTokenizeSrc = tokenizer.getTokenizeSourceCode(verifiableSrc);
            String uniqueTokenizeSrc = tokenizer.getTokenizeSourceCode(uniqueSrc);
            double plagiarismCoeff = plagiarismDetector.getPlagiarismCoefficient(verifiableTokenizeSrc,
                    uniqueTokenizeSrc);
            model.addAttribute("result", "For source code from text areas: " + plagiarismCoeff);
        }

        return "result";
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String showCodes(Model model,
                            @ModelAttribute(name = "filesForm")UploadForm form,
                            @RequestParam(name = "verifiableFileName") String verifiableFileName,
                            @RequestParam(name = "uniqueFileName") String uniqueFileName) throws IOException {
        MultipartFile verifiableFile = getFileByNameFromArray(verifiableFileName, form.getVerifiableFiles());
        MultipartFile uniqueFile = getFileByNameFromArray(uniqueFileName, form.getUniqueFiles());
        if (verifiableFile != null && uniqueFile != null) {
            Map<String, String> resultHtmlMap = selectPlagiarismCode(getFileText(verifiableFile), getFileText(uniqueFile));
            String resultVerifiableSourceCodeInHtml = resultHtmlMap.get("verifiableCode");
            String resultUniqueSourceCodeInHtml = resultHtmlMap.get("uniqueCode");
            model.addAttribute("verifiableCode", "<pre>" + resultVerifiableSourceCodeInHtml + "</pre>");
            model.addAttribute("verifiableFileName", verifiableFile.getOriginalFilename());
            model.addAttribute("uniqueCode", "<pre>" + resultUniqueSourceCodeInHtml + "</pre>");
            model.addAttribute("uniqueFileName", uniqueFile.getOriginalFilename());
        }
        return "codes";
    }

    private Map<String, String> selectPlagiarismCode(String verifiableSourceCode, String uniqueSourceCode) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("verifiableCode", verifiableSourceCode);
        resultMap.put("uniqueCode", uniqueSourceCode);
        Map<String, String> tokenToVerifiableSourceCodeMap = generateTokenToSourceCodeMap(verifiableSourceCode);
        Map<String, String> tokenToUniqueSourceCodeMap = generateTokenToSourceCodeMap(uniqueSourceCode);
        String tokenizeVerifiableSourceCode = tokenizer.getTokenizeSourceCode(verifiableSourceCode);
        String tokenizeUniqueSourceCode = tokenizer.getTokenizeSourceCode(uniqueSourceCode);
        int commonTokens = tokenizeVerifiableSourceCode.length() / 2;
        int startIndex = 0;
        int index = 0;
        while (commonTokens > 5) {
            while (startIndex + commonTokens <= tokenizeVerifiableSourceCode.length()) {
                String tempToken = tokenizeVerifiableSourceCode.substring(startIndex, startIndex + commonTokens);
                if (tokenizeUniqueSourceCode.indexOf(tempToken) != -1) {
                    if (tokenToVerifiableSourceCodeMap.containsKey(tempToken)) {
                        String tempVerifiableSourceCode = tokenToVerifiableSourceCodeMap.get(tempToken);
                        index = resultMap.get("verifiableCode").indexOf(tempVerifiableSourceCode, index);
                        if (index != -1) {
                            String s = resultMap.get("verifiableCode");
                            String plagiarism = s.substring(index, index + tempVerifiableSourceCode.length());
                            if (!isCodeSelectedAsPlagiarism(s, plagiarism)) {
                                plagiarism = "</pre><pre class='plagtext'>" + plagiarism + "</pre><pre>";
                            }
                            resultMap.put("verifiableCode", s.substring(0, index) + plagiarism + s.substring(index + tempVerifiableSourceCode.length()));
                        }
                        if (tokenToUniqueSourceCodeMap.containsKey(tempToken)) {
                            String tempUniqueSourceCode = tokenToUniqueSourceCodeMap.get(tempToken);
                            index = resultMap.get("uniqueCode").indexOf(tempUniqueSourceCode, index);
                            if (index != -1) {
                                String s = resultMap.get("uniqueCode");
                                String plagiarism = s.substring(index, index + tempUniqueSourceCode.length());
                                if (!isCodeSelectedAsPlagiarism(s, plagiarism)) {
                                    plagiarism = "</pre><pre class='plagtext'>" + plagiarism + "</pre><pre>";
                                }
                                resultMap.put("uniqueCode", s.substring(0, index) + plagiarism + s.substring(index + tempUniqueSourceCode.length()));
                            }
                        }
                    }
                }
                startIndex ++;
            }
            commonTokens--;
            startIndex = 0;
        }
        return resultMap;
    }

    private boolean isCodeSelectedAsPlagiarism(String sourceCode, String plagSubcode) {
        int indexOfSubcode = sourceCode.indexOf(plagSubcode);
        int start = sourceCode.substring(0, indexOfSubcode).lastIndexOf("<pre class='plagtext'>");
        int end = sourceCode.indexOf("</pre>", indexOfSubcode + plagSubcode.length());
        if (start == -1 || end == -1) {
            return false;
        }
        if (start <= indexOfSubcode && end >= indexOfSubcode) {
            return true;
        }
        return false;
    }

    private Map<String, String> generateTokenToSourceCodeMap(String sourceCode) {
        Map<String, String> resultMap = new HashMap<>();
        int k = 2;
        int tempLength = sourceCode.length() / k;
        int startIndex = 0;
        int endIndex = tempLength;
        String tempSourceCode;
        String tokenizeTempSourceCode;
        do {
            if (endIndex > sourceCode.length()) {
                tempSourceCode = sourceCode.substring(startIndex);
            } else {
                tempSourceCode = sourceCode.substring(startIndex, endIndex);
            }
            tokenizeTempSourceCode = tokenizer.getTokenizeSourceCode(tempSourceCode);
            resultMap.put(tokenizeTempSourceCode, tempSourceCode);
            startIndex = endIndex;
            endIndex += tempLength;
            if (startIndex > sourceCode.length()) {
                k++;
                startIndex = 0;
                tempLength = sourceCode.length() / k;
                endIndex = tempLength;
            }
        } while (tempLength > 4);
        return resultMap;
    }

    private MultipartFile getFileByNameFromArray(String verifiableFileName, MultipartFile[] verifiableFiles) {
        for (MultipartFile verifiableFile : verifiableFiles) {
            if (verifiableFile.getOriginalFilename().equals(verifiableFileName)) {
                return verifiableFile;
            }
        }
        return null;
    }

    private String getFileText(MultipartFile file) throws IOException {
        StringBuilder str = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String currentStr = bufferedReader.readLine();
        while (currentStr != null) {
            str.append(currentStr).append("\n");
            currentStr = bufferedReader.readLine();
        }
        return str.toString();
    }

}
