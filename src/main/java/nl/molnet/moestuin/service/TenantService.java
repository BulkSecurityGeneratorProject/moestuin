package nl.molnet.moestuin.service;

import nl.molnet.moestuin.domain.Tenant;
import nl.molnet.moestuin.repository.TenantRepository;
import nl.molnet.moestuin.repository.search.TenantSearchRepository;
import nl.molnet.moestuin.service.dto.TenantDTO;
import nl.molnet.moestuin.service.mapper.TenantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Tenant.
 */
@Service
@Transactional
public class TenantService {

    private final Logger log = LoggerFactory.getLogger(TenantService.class);
    
    @Inject
    private TenantRepository tenantRepository;

    @Inject
    private TenantMapper tenantMapper;

    @Inject
    private TenantSearchRepository tenantSearchRepository;

    /**
     * Save a tenant.
     *
     * @param tenantDTO the entity to save
     * @return the persisted entity
     */
    public TenantDTO save(TenantDTO tenantDTO) {
        log.debug("Request to save Tenant : {}", tenantDTO);
        Tenant tenant = tenantMapper.tenantDTOToTenant(tenantDTO);
        tenant = tenantRepository.save(tenant);
        TenantDTO result = tenantMapper.tenantToTenantDTO(tenant);
        tenantSearchRepository.save(tenant);
        return result;
    }

    /**
     *  Get all the tenants.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<TenantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tenants");
        Page<Tenant> result = tenantRepository.findAll(pageable);
        return result.map(tenant -> tenantMapper.tenantToTenantDTO(tenant));
    }

    /**
     *  Get one tenant by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public TenantDTO findOne(Long id) {
        log.debug("Request to get Tenant : {}", id);
        Tenant tenant = tenantRepository.findOne(id);
        TenantDTO tenantDTO = tenantMapper.tenantToTenantDTO(tenant);
        return tenantDTO;
    }

    /**
     *  Delete the  tenant by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Tenant : {}", id);
        tenantRepository.delete(id);
        tenantSearchRepository.delete(id);
    }

    /**
     * Search for the tenant corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TenantDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tenants for query {}", query);
        Page<Tenant> result = tenantSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(tenant -> tenantMapper.tenantToTenantDTO(tenant));
    }
}
