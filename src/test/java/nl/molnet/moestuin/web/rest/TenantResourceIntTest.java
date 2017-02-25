package nl.molnet.moestuin.web.rest;

import nl.molnet.moestuin.MoestuinApp;

import nl.molnet.moestuin.domain.Tenant;
import nl.molnet.moestuin.repository.TenantRepository;
import nl.molnet.moestuin.service.TenantService;
import nl.molnet.moestuin.repository.search.TenantSearchRepository;
import nl.molnet.moestuin.service.dto.TenantDTO;
import nl.molnet.moestuin.service.mapper.TenantMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TenantResource REST controller.
 *
 * @see TenantResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MoestuinApp.class)
public class TenantResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_API_KEY = "AAAAA";
    private static final String UPDATED_API_KEY = "BBBBB";

    private static final String DEFAULT_STREET_ADDRESS = "AAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBB";

    private static final String DEFAULT_CITY = "AAAAA";
    private static final String UPDATED_CITY = "BBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAA";
    private static final String UPDATED_COUNTRY = "BBBBB";

    @Inject
    private TenantRepository tenantRepository;

    @Inject
    private TenantMapper tenantMapper;

    @Inject
    private TenantService tenantService;

    @Inject
    private TenantSearchRepository tenantSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTenantMockMvc;

    private Tenant tenant;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TenantResource tenantResource = new TenantResource();
        ReflectionTestUtils.setField(tenantResource, "tenantService", tenantService);
        this.restTenantMockMvc = MockMvcBuilders.standaloneSetup(tenantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenant createEntity(EntityManager em) {
        Tenant tenant = new Tenant()
                .name(DEFAULT_NAME)
                .apiKey(DEFAULT_API_KEY)
                .streetAddress(DEFAULT_STREET_ADDRESS)
                .postalCode(DEFAULT_POSTAL_CODE)
                .city(DEFAULT_CITY)
                .country(DEFAULT_COUNTRY);
        return tenant;
    }

    @Before
    public void initTest() {
        tenantSearchRepository.deleteAll();
        tenant = createEntity(em);
    }

    @Test
    @Transactional
    public void createTenant() throws Exception {
        int databaseSizeBeforeCreate = tenantRepository.findAll().size();

        // Create the Tenant
        TenantDTO tenantDTO = tenantMapper.tenantToTenantDTO(tenant);

        restTenantMockMvc.perform(post("/api/tenants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenantDTO)))
                .andExpect(status().isCreated());

        // Validate the Tenant in the database
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeCreate + 1);
        Tenant testTenant = tenants.get(tenants.size() - 1);
        assertThat(testTenant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTenant.getApiKey()).isEqualTo(DEFAULT_API_KEY);
        assertThat(testTenant.getStreetAddress()).isEqualTo(DEFAULT_STREET_ADDRESS);
        assertThat(testTenant.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testTenant.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testTenant.getCountry()).isEqualTo(DEFAULT_COUNTRY);

        // Validate the Tenant in ElasticSearch
        Tenant tenantEs = tenantSearchRepository.findOne(testTenant.getId());
        assertThat(tenantEs).isEqualToComparingFieldByField(testTenant);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setName(null);

        // Create the Tenant, which fails.
        TenantDTO tenantDTO = tenantMapper.tenantToTenantDTO(tenant);

        restTenantMockMvc.perform(post("/api/tenants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenantDTO)))
                .andExpect(status().isBadRequest());

        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkApiKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setApiKey(null);

        // Create the Tenant, which fails.
        TenantDTO tenantDTO = tenantMapper.tenantToTenantDTO(tenant);

        restTenantMockMvc.perform(post("/api/tenants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenantDTO)))
                .andExpect(status().isBadRequest());

        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTenants() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        // Get all the tenants
        restTenantMockMvc.perform(get("/api/tenants?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].apiKey").value(hasItem(DEFAULT_API_KEY.toString())))
                .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE.toString())))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
                .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }

    @Test
    @Transactional
    public void getTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        // Get the tenant
        restTenantMockMvc.perform(get("/api/tenants/{id}", tenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tenant.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.apiKey").value(DEFAULT_API_KEY.toString()))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS.toString()))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTenant() throws Exception {
        // Get the tenant
        restTenantMockMvc.perform(get("/api/tenants/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);
        tenantSearchRepository.save(tenant);
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();

        // Update the tenant
        Tenant updatedTenant = tenantRepository.findOne(tenant.getId());
        updatedTenant
                .name(UPDATED_NAME)
                .apiKey(UPDATED_API_KEY)
                .streetAddress(UPDATED_STREET_ADDRESS)
                .postalCode(UPDATED_POSTAL_CODE)
                .city(UPDATED_CITY)
                .country(UPDATED_COUNTRY);
        TenantDTO tenantDTO = tenantMapper.tenantToTenantDTO(updatedTenant);

        restTenantMockMvc.perform(put("/api/tenants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenantDTO)))
                .andExpect(status().isOk());

        // Validate the Tenant in the database
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeUpdate);
        Tenant testTenant = tenants.get(tenants.size() - 1);
        assertThat(testTenant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTenant.getApiKey()).isEqualTo(UPDATED_API_KEY);
        assertThat(testTenant.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testTenant.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testTenant.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testTenant.getCountry()).isEqualTo(UPDATED_COUNTRY);

        // Validate the Tenant in ElasticSearch
        Tenant tenantEs = tenantSearchRepository.findOne(testTenant.getId());
        assertThat(tenantEs).isEqualToComparingFieldByField(testTenant);
    }

    @Test
    @Transactional
    public void deleteTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);
        tenantSearchRepository.save(tenant);
        int databaseSizeBeforeDelete = tenantRepository.findAll().size();

        // Get the tenant
        restTenantMockMvc.perform(delete("/api/tenants/{id}", tenant.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean tenantExistsInEs = tenantSearchRepository.exists(tenant.getId());
        assertThat(tenantExistsInEs).isFalse();

        // Validate the database is empty
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);
        tenantSearchRepository.save(tenant);

        // Search the tenant
        restTenantMockMvc.perform(get("/api/_search/tenants?query=id:" + tenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].apiKey").value(hasItem(DEFAULT_API_KEY.toString())))
            .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }
}
