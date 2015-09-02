package au.com.billon.stt.resources;

import au.com.billon.stt.db.EndpointDAO;
import au.com.billon.stt.db.EndpointDetailDAO;
import au.com.billon.stt.handlers.HandlerFactory;
import au.com.billon.stt.models.Endpoint;
import au.com.billon.stt.models.EndpointDetail;
import io.dropwizard.db.DataSourceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/endpoints") @Produces({ MediaType.APPLICATION_JSON })
public class EndpointResource {
    private final EndpointDAO dao;
    private final EndpointDetailDAO detailDao;

    public EndpointResource(EndpointDAO dao, EndpointDetailDAO detailDao, DataSourceFactory dsFactory) {
        this.dao = dao;
        this.detailDao = detailDao;

        initSystemData(dsFactory);
    }

    private void initSystemData(DataSourceFactory dsFactory) {
        if (dao.findByName("STTSystemDBEndpoint") == null) {
            Endpoint endpoint = new Endpoint(0, "STTSystemDB", "The database of the Servie Testing Tool (STT)", "DB", null, null);

            List<EndpointDetail> details = new ArrayList<EndpointDetail>();
            details.add(new EndpointDetail(0, 0, "url", dsFactory.getUrl(), null, null));
            details.add(new EndpointDetail(0, 0, "username", dsFactory.getUser(), null, null));
            details.add(new EndpointDetail(0, 0, "password", dsFactory.getPassword(), null, null));

            endpoint.setDetails(details);

            create(endpoint);
        }
    }

    @POST
    public Endpoint create(Endpoint endpoint) {
        long id = dao.insert(endpoint);
        endpoint.setId(id);

        List<EndpointDetail> details = endpoint.getDetails();
        for (EndpointDetail detail : details) {
            detail.setEndpointId(endpoint.getId());
            if (EndpointDetail.PASSWORD_PROPERTY.equals(detail.getName())) {
                detailDao.insertPassword(detail);
            } else {
                detailDao.insert(detail);
            }
        }

        return endpoint;
    }

    @PUT @Path("{endpointId}")
    public Endpoint update(Endpoint endpoint) {
        dao.update(endpoint);

        List<EndpointDetail> details = endpoint.getDetails();
        for (EndpointDetail detail : details) {
            if (EndpointDetail.PASSWORD_PROPERTY.equals(detail.getName())) {
                detailDao.updatePassword(detail);
            } else {
                detailDao.update(detail);
            }
        }

        return findById(endpoint.getId());
    }

    @DELETE @Path("{endpointId}")
    public void delete(@PathParam("endpointId") long endpointId) {
        dao.deleteById(endpointId);
    }

    @GET
    public List<Endpoint> findAll() {
        return dao.findAll();
    }

    @GET @Path("/properties/{endpointType}")
    public List<EndpointDetail> getDetails(@PathParam("endpointType") String handlerName) {
        List<String> propNames = HandlerFactory.getInstance().getHandler(handlerName).getPropNames();
        List<EndpointDetail> details = new ArrayList<EndpointDetail>();

        for (String propName : propNames) {
            EndpointDetail detail = new EndpointDetail();
            detail.setName(propName);

            details.add(detail);
        }

        return details;
    }

    @GET @Path("{endpointId}")
    public Endpoint findById(@PathParam("endpointId") long endpointId) {
        Endpoint endpoint = dao.findById(endpointId);
        endpoint.setDetails(detailDao.findByEndpoint(endpointId));
        return endpoint;
    }
}
