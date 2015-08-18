package au.com.billon.stt.resources;

import au.com.billon.stt.db.AssertionDAO;
import au.com.billon.stt.db.TeststepDAO;
import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.Properties;
import au.com.billon.stt.models.SOAPTeststepProperties;
import au.com.billon.stt.models.Teststep;
import au.com.billon.stt.parsers.ParserFactory;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private final TeststepDAO dao;
    private final AssertionDAO assertionDAO;

    public TeststepResource(TeststepDAO dao, AssertionDAO assertionDAO) {
        this.dao = dao;
        this.assertionDAO = assertionDAO;
    }

    @POST
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        Properties properties = teststep.getProperties();

        String parserName = "DBInterface";
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            parserName = "WSDL";
            String adhocAddress = ParserFactory.getInstance().getParser(parserName).getAdhocAddress(properties);
            ((SOAPTeststepProperties) properties).setSoapAddress(adhocAddress);
        }

        String sampleRequest = ParserFactory.getInstance().getParser(parserName).getSampleRequest(properties);
        teststep.setRequest(sampleRequest);

        long id = dao.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point
        return teststep;
    }

    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        Teststep teststep = dao.findById(teststepId);
        List<Assertion> assertions = assertionDAO.findByTeststepId(teststepId);
        teststep.setAssertions(assertions);
        return teststep;
    }

    @PUT @Path("{teststepId}")
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        dao.update(teststep);

        long teststepId = teststep.getId();
        List<Assertion> assertions = teststep.getAssertions();
        Set<Long> assertionIds = new HashSet<Long>();

        for (Assertion assertion : assertions) {
            Long assertionId = assertion.getId();
            if (assertionId > 0) {
                // update the existing assertion
                assertionDAO.update(assertion);
            } else {
                // create a new assertion
                assertion.setTeststepId(teststepId);
                assertionId = assertionDAO.insert(assertion);
            }

            assertionIds.add(assertionId);
        }

        List<Assertion> dbAssertions = assertionDAO.findByTeststepId(teststepId);
        for (Assertion dbAssertion : dbAssertions) {
            Long dbAssertionId = dbAssertion.getId();
            if (! assertionIds.contains(dbAssertionId)) {
                // delete the existing entry
                assertionDAO.deleteById(dbAssertionId);
            }
        }

        return findById(teststepId);
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        dao.deleteById(teststepId);
    }
}
