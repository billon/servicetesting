package au.com.billon.stt.resources;

import au.com.billon.stt.core.Evaluator;
import au.com.billon.stt.core.EvaluatorFactory;
import au.com.billon.stt.db.*;
import au.com.billon.stt.handlers.HandlerFactory;
import au.com.billon.stt.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestrunResource {
    private final EndpointDAO endpointDao;
    private final EndpointDetailDAO endpointdtlDao;
    private final TestcaseDAO testcaseDao;
    private final TeststepDAO teststepDao;
    private final EnvironmentDAO environmentDao;
    private final EnvEntryDAO enventryDao;
    private final IntfaceDAO intfaceDao;
    private final AssertionDAO assertionDao;

    public TestrunResource(EndpointDAO endpointDao, EndpointDetailDAO endpointdtlDao, TestcaseDAO testcaseDao,
                           TeststepDAO teststepDao, EnvironmentDAO environmentDao, EnvEntryDAO enventryDao,
                           IntfaceDAO intfaceDao, AssertionDAO assertionDao) {
        this.endpointDao = endpointDao;
        this.endpointdtlDao = endpointdtlDao;
        this.testcaseDao = testcaseDao;
        this.teststepDao = teststepDao;
        this.environmentDao = environmentDao;
        this.enventryDao = enventryDao;
        this.intfaceDao = intfaceDao;
        this.assertionDao = assertionDao;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        List<Long> testcaseIds = testrun.getTestcaseIds();
        // Run multiple test cases
        if (testcaseIds != null) {
        } else {
            long testcaseId = testrun.getTestcaseId();
            // Run one test case
            if (testcaseId > 0) {
                Testcase testcase = testcaseDao.findById(testcaseId);
                List<Teststep> teststeps = teststepDao.findByTestcaseId(testcaseId);

                long environmentId = testrun.getEnvironmentId();
                Environment environment = environmentDao.findById(environmentId);
                Map<Long, EnvEntry> enventryMap = getEnvEntryMap(enventryDao.findByEnv(environmentId));

                for (Teststep teststep : teststeps) {
                    long intfaceId = teststep.getIntfaceId();
                    EnvEntry enventry = enventryMap.get(intfaceId);
                    if (enventry == null) {
                        throw new Exception("No interface entry for the test step " + teststep.getName() + " in the environment " + environment.getName());
                    } else {
                        long endpointId = enventry.getEndpointId();
                        Endpoint endpoint = endpointDao.findById(endpointId);

                        Map<String, String> endpointProps = getEndpointProps(endpointId);

                        TestResponse response = HandlerFactory.getInstance().getHandler(endpoint.getHandler()).invoke(teststep.getRequest(), endpointProps);

                        System.out.println(response);

                        Intface intface = intfaceDao.findById(intfaceId);
                        List<Assertion> assertions = assertionDao.findByTeststepId(teststep.getId());

                        TestResult result = new TestResult();
                        for (Assertion assertion : assertions) {
                            Evaluator evaluator = EvaluatorFactory.getInstance().getEvaluator(assertion.getType());
                            result = evaluator.evaluate(response, assertion.getProperties());
                            if (result.getError().equals("true")) {
                                break;
                            }
                        }

                        teststep.setResult(result);
                    }
                }

                testrun.setEnvironment(environment);
                testcase.setTeststeps(teststeps);
                testrun.setTestcase(testcase);
            } else {
                String request = testrun.getRequest();
                // Invoke a service only
                if (request != null) {
                    TestResponse response = invoke(request, testrun.getEndpointId(), testrun.getEndpointProps());
                    testrun.setResponse(response);
                } else {
                    TestResponse response = testrun.getResponse();
                    // Evaluate test response
                    if (response != null) {
                        List<Assertion> assertions = testrun.getAssertions();
                        if (assertions != null) {
                            evaluate(response, assertions);
                        }
                    }
                }
            }
        }

        return testrun;
    }

    private void run(Testcase testcase) {

    }

    private void run(Teststep teststep) {

    }

    private TestResponse invoke(String request, long endpointId, Map<String, String> endpointProps) throws Exception {
        TestResponse response = null;

        String handler = null;

        if (endpointId > 0) {
            endpointProps = getEndpointProps(endpointId);
            Endpoint endpoint = endpointDao.findById(endpointId);
            handler = endpoint.getHandler();
        } else {
            if (endpointProps != null) {
                endpointProps.put("url", endpointProps.get("soapAddress"));
                handler = "SOAPHandler";
            }
        }

        if (endpointProps != null) {
            response = HandlerFactory.getInstance().getHandler(handler).invoke(request, endpointProps);
        }

        return response;
    }

    private void evaluate(TestResponse response, List<Assertion> assertions) {
        for (Assertion assertion : assertions) {
            TestResult result = EvaluatorFactory.getInstance().getEvaluator(assertion.getType()).evaluate(response, assertion.getProperties());
            assertion.setResult(result);
        }
    }

    private Map<Long, EnvEntry> getEnvEntryMap(List<EnvEntry> enventries) {
        Map<Long, EnvEntry> enventryMap = new HashMap<Long, EnvEntry>();
        for (EnvEntry enventry : enventries) {
            enventryMap.put(enventry.getIntfaceId(), enventry);
        }

        return enventryMap;
    }

    private Map<String, String> getEndpointProps(long endpointId) {
        Map<String, String> details = list2Props(endpointdtlDao.findByEndpoint(endpointId));
        EndpointDetail detailPassword = endpointdtlDao.findByEndpointPassword(endpointId);
        if (detailPassword != null) {
            details.put(detailPassword.getName(), detailPassword.getValue());
        }

        return details;
    }

    private Map<String, String> list2Props(List<EndpointDetail> detailsArray) {
        Map<String, String> details = new HashMap<String, String>();
        for (EndpointDetail detail : detailsArray) {
            details.put(detail.getName(), detail.getValue());
        }

        return details;
    }

    @DELETE @Path("{testrunId}")
    public void delete(@PathParam("testrunId") long testrunId) {
    }

    @GET
    public List<Testrun> findAll() {
        return null;
    }

    @GET @Path("{testrunId}")
    public Testrun findById(@PathParam("testrunId") long testrunId) {
        return null;
    }
}
