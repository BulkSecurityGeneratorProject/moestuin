package nl.molnet.moestuin.service.mapper;

import nl.molnet.moestuin.domain.*;
import nl.molnet.moestuin.service.dto.TenantDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Tenant and its DTO TenantDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TenantMapper {

    TenantDTO tenantToTenantDTO(Tenant tenant);

    List<TenantDTO> tenantsToTenantDTOs(List<Tenant> tenants);

    @Mapping(target = "events", ignore = true)
    Tenant tenantDTOToTenant(TenantDTO tenantDTO);

    List<Tenant> tenantDTOsToTenants(List<TenantDTO> tenantDTOs);
}
