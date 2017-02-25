package nl.molnet.moestuin.repository;

import nl.molnet.moestuin.domain.Tenant;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tenant entity.
 */
@SuppressWarnings("unused")
public interface TenantRepository extends JpaRepository<Tenant,Long> {

}
