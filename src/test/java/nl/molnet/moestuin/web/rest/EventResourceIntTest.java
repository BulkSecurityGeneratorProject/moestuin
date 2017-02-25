package nl.molnet.moestuin.web.rest;

import nl.molnet.moestuin.MoestuinApp;

import nl.molnet.moestuin.domain.Event;
import nl.molnet.moestuin.repository.EventRepository;
import nl.molnet.moestuin.service.EventService;
import nl.molnet.moestuin.repository.search.EventSearchRepository;
import nl.molnet.moestuin.service.dto.EventDTO;
import nl.molnet.moestuin.service.mapper.EventMapper;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EventResource REST controller.
 *
 * @see EventResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MoestuinApp.class)
public class EventResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final ZonedDateTime DEFAULT_EVENT_OPEN_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_EVENT_OPEN_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_EVENT_OPEN_TIME_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_EVENT_OPEN_TIME);

    private static final ZonedDateTime DEFAULT_EVENT_CLOSE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_EVENT_CLOSE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_EVENT_CLOSE_TIME_STR = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(DEFAULT_EVENT_CLOSE_TIME);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Integer DEFAULT_THROUGHPUT_PER_MINUTE = 1;
    private static final Integer UPDATED_THROUGHPUT_PER_MINUTE = 2;

    private static final Integer DEFAULT_MAX_PASSAGES = 1;
    private static final Integer UPDATED_MAX_PASSAGES = 2;

    private static final String DEFAULT_REDIRECT_URL = "AAAAA";
    private static final String UPDATED_REDIRECT_URL = "BBBBB";

    @Inject
    private EventRepository eventRepository;

    @Inject
    private EventMapper eventMapper;

    @Inject
    private EventService eventService;

    @Inject
    private EventSearchRepository eventSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restEventMockMvc;

    private Event event;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EventResource eventResource = new EventResource();
        ReflectionTestUtils.setField(eventResource, "eventService", eventService);
        this.restEventMockMvc = MockMvcBuilders.standaloneSetup(eventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Event createEntity(EntityManager em) {
        Event event = new Event()
                .name(DEFAULT_NAME)
                .eventOpenTime(DEFAULT_EVENT_OPEN_TIME)
                .eventCloseTime(DEFAULT_EVENT_CLOSE_TIME)
                .active(DEFAULT_ACTIVE)
                .throughputPerMinute(DEFAULT_THROUGHPUT_PER_MINUTE)
                .maxPassages(DEFAULT_MAX_PASSAGES)
                .redirectUrl(DEFAULT_REDIRECT_URL);
        return event;
    }

    @Before
    public void initTest() {
        eventSearchRepository.deleteAll();
        event = createEntity(em);
    }

    @Test
    @Transactional
    public void createEvent() throws Exception {
        int databaseSizeBeforeCreate = eventRepository.findAll().size();

        // Create the Event
        EventDTO eventDTO = eventMapper.eventToEventDTO(event);

        restEventMockMvc.perform(post("/api/events")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
                .andExpect(status().isCreated());

        // Validate the Event in the database
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(databaseSizeBeforeCreate + 1);
        Event testEvent = events.get(events.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEvent.getEventOpenTime()).isEqualTo(DEFAULT_EVENT_OPEN_TIME);
        assertThat(testEvent.getEventCloseTime()).isEqualTo(DEFAULT_EVENT_CLOSE_TIME);
        assertThat(testEvent.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testEvent.getThroughputPerMinute()).isEqualTo(DEFAULT_THROUGHPUT_PER_MINUTE);
        assertThat(testEvent.getMaxPassages()).isEqualTo(DEFAULT_MAX_PASSAGES);
        assertThat(testEvent.getRedirectUrl()).isEqualTo(DEFAULT_REDIRECT_URL);

        // Validate the Event in ElasticSearch
        Event eventEs = eventSearchRepository.findOne(testEvent.getId());
        assertThat(eventEs).isEqualToComparingFieldByField(testEvent);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = eventRepository.findAll().size();
        // set the field null
        event.setName(null);

        // Create the Event, which fails.
        EventDTO eventDTO = eventMapper.eventToEventDTO(event);

        restEventMockMvc.perform(post("/api/events")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
                .andExpect(status().isBadRequest());

        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEvents() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get all the events
        restEventMockMvc.perform(get("/api/events?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].eventOpenTime").value(hasItem(DEFAULT_EVENT_OPEN_TIME_STR)))
                .andExpect(jsonPath("$.[*].eventCloseTime").value(hasItem(DEFAULT_EVENT_CLOSE_TIME_STR)))
                .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].throughputPerMinute").value(hasItem(DEFAULT_THROUGHPUT_PER_MINUTE)))
                .andExpect(jsonPath("$.[*].maxPassages").value(hasItem(DEFAULT_MAX_PASSAGES)))
                .andExpect(jsonPath("$.[*].redirectUrl").value(hasItem(DEFAULT_REDIRECT_URL.toString())));
    }

    @Test
    @Transactional
    public void getEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);

        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(event.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.eventOpenTime").value(DEFAULT_EVENT_OPEN_TIME_STR))
            .andExpect(jsonPath("$.eventCloseTime").value(DEFAULT_EVENT_CLOSE_TIME_STR))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.throughputPerMinute").value(DEFAULT_THROUGHPUT_PER_MINUTE))
            .andExpect(jsonPath("$.maxPassages").value(DEFAULT_MAX_PASSAGES))
            .andExpect(jsonPath("$.redirectUrl").value(DEFAULT_REDIRECT_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEvent() throws Exception {
        // Get the event
        restEventMockMvc.perform(get("/api/events/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);
        eventSearchRepository.save(event);
        int databaseSizeBeforeUpdate = eventRepository.findAll().size();

        // Update the event
        Event updatedEvent = eventRepository.findOne(event.getId());
        updatedEvent
                .name(UPDATED_NAME)
                .eventOpenTime(UPDATED_EVENT_OPEN_TIME)
                .eventCloseTime(UPDATED_EVENT_CLOSE_TIME)
                .active(UPDATED_ACTIVE)
                .throughputPerMinute(UPDATED_THROUGHPUT_PER_MINUTE)
                .maxPassages(UPDATED_MAX_PASSAGES)
                .redirectUrl(UPDATED_REDIRECT_URL);
        EventDTO eventDTO = eventMapper.eventToEventDTO(updatedEvent);

        restEventMockMvc.perform(put("/api/events")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(eventDTO)))
                .andExpect(status().isOk());

        // Validate the Event in the database
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(databaseSizeBeforeUpdate);
        Event testEvent = events.get(events.size() - 1);
        assertThat(testEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEvent.getEventOpenTime()).isEqualTo(UPDATED_EVENT_OPEN_TIME);
        assertThat(testEvent.getEventCloseTime()).isEqualTo(UPDATED_EVENT_CLOSE_TIME);
        assertThat(testEvent.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testEvent.getThroughputPerMinute()).isEqualTo(UPDATED_THROUGHPUT_PER_MINUTE);
        assertThat(testEvent.getMaxPassages()).isEqualTo(UPDATED_MAX_PASSAGES);
        assertThat(testEvent.getRedirectUrl()).isEqualTo(UPDATED_REDIRECT_URL);

        // Validate the Event in ElasticSearch
        Event eventEs = eventSearchRepository.findOne(testEvent.getId());
        assertThat(eventEs).isEqualToComparingFieldByField(testEvent);
    }

    @Test
    @Transactional
    public void deleteEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);
        eventSearchRepository.save(event);
        int databaseSizeBeforeDelete = eventRepository.findAll().size();

        // Get the event
        restEventMockMvc.perform(delete("/api/events/{id}", event.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean eventExistsInEs = eventSearchRepository.exists(event.getId());
        assertThat(eventExistsInEs).isFalse();

        // Validate the database is empty
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEvent() throws Exception {
        // Initialize the database
        eventRepository.saveAndFlush(event);
        eventSearchRepository.save(event);

        // Search the event
        restEventMockMvc.perform(get("/api/_search/events?query=id:" + event.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(event.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].eventOpenTime").value(hasItem(DEFAULT_EVENT_OPEN_TIME_STR)))
            .andExpect(jsonPath("$.[*].eventCloseTime").value(hasItem(DEFAULT_EVENT_CLOSE_TIME_STR)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].throughputPerMinute").value(hasItem(DEFAULT_THROUGHPUT_PER_MINUTE)))
            .andExpect(jsonPath("$.[*].maxPassages").value(hasItem(DEFAULT_MAX_PASSAGES)))
            .andExpect(jsonPath("$.[*].redirectUrl").value(hasItem(DEFAULT_REDIRECT_URL.toString())));
    }
}
