package au.com.billon.stt.core;

/**
 * Created by Zheng on 2/08/2015.
 */
public class EvaluatorFactory {
    public Evaluator createEvaluator(String intfaceType, String assertionType) {
        Evaluator result = null;

        if (intfaceType.equals("DBInterface")) {
            if (assertionType.equals("DSField")) {
                result = new DSFieldEvaluator();
            }
        }

        if (intfaceType.equals("WSDL")) {
            if (assertionType.equals("XPath")) {
                result = new XPathEvaluator();
            }
        }

        return result;
    }
}
