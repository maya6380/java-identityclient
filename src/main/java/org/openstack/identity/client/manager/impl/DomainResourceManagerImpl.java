package org.openstack.identity.client.manager.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.openstack.identity.client.common.constants.IdentityConstants;
import org.openstack.identity.client.common.util.ResourceUtil;
import org.openstack.identity.client.common.wrapper.IdentityResponseWrapper;
import org.openstack.identity.client.domain.Domain;
import org.openstack.identity.client.domain.ObjectFactory;
import org.openstack.identity.client.fault.IdentityFault;
import org.openstack.identity.client.manager.DomainResourceManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.net.URI;
import java.net.URISyntaxException;

public class DomainResourceManagerImpl extends ResponseManagerImpl implements DomainResourceManager {

    @Override
    public ClientResponse createDomain(Client client, String url, String token, String domainId, String domainName, boolean enabled, String description) throws IdentityFault, URISyntaxException {
        ClientResponse response = null;
        try {
            response = post(client, new URI(url + IdentityConstants.RAX_AUTH + IdentityConstants.DOMAINS), token, buildCreateDomainRequestObject(domainId, domainName, enabled, description));
        } catch (UniformInterfaceException ux) {
            throw IdentityResponseWrapper.buildFaultMessage(ux.getResponse());
        } catch (JAXBException e) {
            throw new IdentityFault(e.getMessage(), e.getLinkedException().getLocalizedMessage(), Integer.valueOf(e.getErrorCode()));
        }

        if (!isResponseValid(response)) {
            handleBadResponse(response);
        }

        return response;
    }

    @Override
    public boolean updateDomain(Client client, String url, String token, String domainId, String domainName, String enabled, String description) throws IdentityFault, URISyntaxException {
        ClientResponse response = null;
        try {
            URI blah = new URI(url + IdentityConstants.RAX_AUTH + IdentityConstants.DOMAINS + "/" + domainId);
            Domain domain = getDomain(client, url, token, domainId);
            response = put(client, new URI(url + IdentityConstants.RAX_AUTH + IdentityConstants.DOMAINS + "/" + domainId), token, buildUpdateDomainRequestObject(domain, domainId, domainName, enabled, description));
        } catch (UniformInterfaceException ux) {
            throw IdentityResponseWrapper.buildFaultMessage(ux.getResponse());
        } catch (JAXBException e) {
            throw new IdentityFault(e.getMessage(), e.getLinkedException().getLocalizedMessage(), Integer.valueOf(e.getErrorCode()));
        }

        if (!isResponseValid(response)) {
            handleBadResponse(response);
        }

        return true;
    }

    @Override
    public Domain getDomain(Client client, String url, String token, String domainId) throws IdentityFault, URISyntaxException {
        ClientResponse response = null;
        try {
            response = get(client, new URI(url + IdentityConstants.RAX_AUTH + IdentityConstants.DOMAINS + "/" + domainId), token);
        } catch (UniformInterfaceException ux) {
            throw IdentityResponseWrapper.buildFaultMessage(ux.getResponse());
        }

        if (!isResponseValid(response)) {
            handleBadResponse(response);
        }

        return response.getEntity(Domain.class);
    }

    @Override
    public boolean deleteDomain(Client client, String url, String token, String domainId) throws IdentityFault, URISyntaxException {
        ClientResponse response = null;
        try {
            response = delete(client, new URI(url + IdentityConstants.RAX_AUTH + IdentityConstants.DOMAINS + "/" + domainId), token);
        } catch (UniformInterfaceException ux) {
            throw IdentityResponseWrapper.buildFaultMessage(ux.getResponse());
        }

        if (!isResponseValid(response)) {
            handleBadResponse(response);
        }

        return true;
    }

    private String buildCreateDomainRequestObject(String domainId, String domainName, boolean enabled, String description) throws JAXBException {
        ObjectFactory factory = new ObjectFactory();
        Domain domain = factory.createDomain();
        domain.setId(domainId);
        domain.setName(domainName);
        domain.setEnabled(enabled);
        domain.setDescription(description);
        return ResourceUtil.marshallResource(factory.createDomain(domain),
                JAXBContext.newInstance(Domain.class)).toString();
    }

    private String buildUpdateDomainRequestObject(Domain domain, String domainId, String domainName, String enabled, String description) throws JAXBException {
        ObjectFactory factory = new ObjectFactory();
        Domain domainU = factory.createDomain();
        if (domainId != null) {
            domainU.setId(domainId);
        } else {
            domainU.setId(domain.getId());
        }
        if (domainName != null) {
            domainU.setName(domainName);
        } else {
            domainU.setName(domain.getName());
        }
        domainU.setEnabled(Boolean.valueOf(enabled));
        if (description != null) {
            domainU.setDescription(description);
        } else {
            domainU.setDescription(domain.getDescription());
        }
        return ResourceUtil.marshallResource(factory.createDomain(domainU),
                JAXBContext.newInstance(Domain.class)).toString();
    }
}