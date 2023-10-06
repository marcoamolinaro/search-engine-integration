package br.com.ovd.kafka.integration.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@ApplicationScoped
public interface SearchEngineIntegration {
    @PUT
    @Path("/{id}")
    Long alterar(@PathParam("id") Long id);

    @POST
    @Path("/{id}")
    Long criar(@PathParam("id") Long id);
}
