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
        List<Testcase> testcases = testrun.getTestcases();
        // Run multiple test cases
        if (testcases != null) {
            runTestcases(testcases, testrun.getEnvironmentId());
        } else {
            Testcase testcase = testrun.getTestcase();
            // Run one test case
            if (testcase != null) {
                runTestCase(testcase, false, testrun.getEnvironmentId());
            } else {
                String request = testrun.getRequest();
                // Invoke a service only
                if (request != null) {
                    TestResponse response = invokeService(request, testrun.getEndpointId(), testrun.getEndpointProps());
                    testrun.setResponse(response);
                } else {
                    TestResponse response = testrun.getResponse();
                    // Evaluate test response
                    if (response != null) {
                        List<Assertion> assertions = testrun.getAssertions();
                        if (assertions != null) {
                            evaluateAssertions(response, assertions);
                        }
                    }
                }
            }
        }

        return testrun;
    }

    private void runTestcases(List<Testcase> testcases, long environmentId) throws Exception {
        for (Testcase testcase: testcases) {
            runTestCase(testcase, true, environmentId);
        }
    }

    private void runTestCase(Testcase testcase, boolean isDBTeststeps, long environmentId) throws Exception {
        TestResult result = new TestResult();
        result.setPassed(true);

        List<Teststep> teststeps = null;
        if (isDBTeststeps) {
            teststeps = teststepDao.findByTestcaseId(testcase.getId());
            testcase.setTeststeps(teststeps);
        } else {
            teststeps = testcase.getTeststeps();
        }

        Environment environment = environmentDao.findById(environmentId);
        Map<Long, EnvEntry> enventryMap = enventryList2Map(enventryDao.findByEnv(environmentId));

        for (Teststep teststep : teststeps) {
            EnvEntry enventry = enventryMap.get(teststep.getIntfaceId());
            if (enventry == null) {
                throw new Exception("The interface of the test step " + teststep.getName() + " is not added to the environment " + environment.getName());
            } else {
                long endpointId = enventry.getEndpointId();
                runTestStep(teststep, endpointId);
                if (! teststep.getResult().getPassed()) {
                    result.setPassed(false);
                    break;
                };
            }
        }

        testcase.setResult(result);
    }

    private void runTestStep(Teststep teststep, long endpointId) throws Exception {
        TestResult result = new TestResult();
        result.setPassed(true);

        TestResponse response = invokeService(teststep.getRequest(), endpointId, null);

        List<Assertion> assertions = assertionDao.findByTeststepId(teststep.getId());
        evaluateAssertions(response, assertions);

        for (Assertion assertion : assertions) {
            if (!assertion.getResult().getPassed()) {
                result.setPassed(false);
                break;
            }
        }

        teststep.setResult(result);
    }

    private TestResponse invokeService(String request, long endpointId, Map<String, String> endpointProps) throws Exception {
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
            System.out.println(response);
        }

        return response;
    }

    private void evaluateAssertions(TestResponse response, List<Assertion> assertions) {
        for (Assertion assertion : assertions) {
            TestResult result = EvaluatorFactory.getInstance().getEvaluator(assertion.getType()).evaluate(response, assertion.getProperties());
            assertion.setResult(result);
        }
    }

    private Map<Long, EnvEntry> enventryList2Map(List<EnvEntry> enventries) {
        Map<Long, EnvEntry> enventryMap = new HashMap<Long, EnvEntry>();
        for (EnvEntry enventry : enventries) {
            enventryMap.put(enventry.getIntfaceId(), enventry);
        }

        return enventryMap;
    }

    private Map<String, String> getEndpointProps(long endpointId) {
        Map<String, String> properties = details2Properties(endpointdtlDao.findByEndpoint(endpointId));

        // get decrypted password
        EndpointDetail detailPassword = endpointdtlDao.findByEndpointPassword(endpointId);
        if (detailPassword != null) {
            properties.put(detailPassword.getName(), detailPassword.getValue());
        }

        return properties;
    }

    private Map<String, String> details2Properties(List<EndpointDetail> details) {
        Map<String, String> properties = new HashMap<String, String>();
        for (EndpointDetail detail : details) {
            properties.put(detail.getName(), detail.getValue());
        }

        return properties;
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
