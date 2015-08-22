package au.com.billon.stt.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zheng on 2/08/2015.
 */
public class EvaluatorFactory {
    private static EvaluatorFactory instance;

    private Map<String, Evaluator> evaluators = new HashMap<String, Evaluator>();

    private EvaluatorFactory() { }

    public static synchronized EvaluatorFactory getInstance() {
        if ( instance == null ) {
            instance = new EvaluatorFactory();
        }
        return instance;
    }

    public Evaluator getEvaluator(String assertionType) {
        Evaluator evaluator = null;
        if (assertionType != null) {
            evaluator = evaluators.get(assertionType);
            if (evaluator == null) {
                try {
                    Class evaluatorClass = Class.forName("au.com.billon.stt.core." + assertionType + "Evaluator");
                    evaluator = (Evaluator) evaluatorClass.newInstance();
                    evaluators.put(assertionType, evaluator);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return evaluator;
    }
}
