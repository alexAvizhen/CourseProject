import com.bsu.avizhen.services.PlagiarismDetector;
import com.bsu.avizhen.services.impl.TokenizerImpl;
import com.bsu.avizhen.services.impl.FingerPrintsPlagiarismDetector;

/**
 * Created by Александр on 18.12.2016.
 */
public class Test {
    public static void main(String[] args) {
        TokenizerImpl tokenizer = new TokenizerImpl();
        String s1 = "A";
        String s2 = "B";
        PlagiarismDetector d = new FingerPrintsPlagiarismDetector();
        System.out.println(d.getPlagiarismCoefficient(s1, s2));


    }
}
