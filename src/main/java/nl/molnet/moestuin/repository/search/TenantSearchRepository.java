package nl.molnet.moestuin.repository.search;

import nl.molnet.moestuin.domain.Tenant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Tenant entity.
 */
public interface TenantSearchRepository extends ElasticsearchRepository<Tenant, Long> {
}
