package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.StatusEmployeRepository;
import com.mycompany.myapp.service.StatusEmployeService;
import com.mycompany.myapp.service.criteria.StatusEmployeCriteria;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.mapper.StatusEmployeMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StatusEmployeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StatusEmployeResourceIT {

    private static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
    private static final UUID UPDATED_USER_UUID = UUID.randomUUID();

    private static final Boolean DEFAULT_DISPONIBILITE = false;
    private static final Boolean UPDATED_DISPONIBILITE = true;

    private static final Boolean DEFAULT_MISSION = false;
    private static final Boolean UPDATED_MISSION = true;

    private static final LocalDate DEFAULT_DEBUT_CONGE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEBUT_CONGE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DEBUT_CONGE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_FIN_CONGE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FIN_CONGE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_FIN_CONGE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/status-employes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatusEmployeRepository statusEmployeRepository;

    @Mock
    private StatusEmployeRepository statusEmployeRepositoryMock;

    @Autowired
    private StatusEmployeMapper statusEmployeMapper;

    @Mock
    private StatusEmployeService statusEmployeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatusEmployeMockMvc;

    private StatusEmploye statusEmploye;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusEmploye createEntity(EntityManager em) {
        StatusEmploye statusEmploye = new StatusEmploye()
            .userUuid(DEFAULT_USER_UUID)
            .disponibilite(DEFAULT_DISPONIBILITE)
            .mission(DEFAULT_MISSION)
            .debutConge(DEFAULT_DEBUT_CONGE)
            .finConge(DEFAULT_FIN_CONGE);
        return statusEmploye;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusEmploye createUpdatedEntity(EntityManager em) {
        StatusEmploye statusEmploye = new StatusEmploye()
            .userUuid(UPDATED_USER_UUID)
            .disponibilite(UPDATED_DISPONIBILITE)
            .mission(UPDATED_MISSION)
            .debutConge(UPDATED_DEBUT_CONGE)
            .finConge(UPDATED_FIN_CONGE);
        return statusEmploye;
    }

    @BeforeEach
    public void initTest() {
        statusEmploye = createEntity(em);
    }

    @Test
    @Transactional
    void createStatusEmploye() throws Exception {
        int databaseSizeBeforeCreate = statusEmployeRepository.findAll().size();
        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);
        restStatusEmployeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeCreate + 1);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(DEFAULT_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(DEFAULT_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(DEFAULT_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(DEFAULT_FIN_CONGE);
    }

    @Test
    @Transactional
    void createStatusEmployeWithExistingId() throws Exception {
        // Create the StatusEmploye with an existing ID
        statusEmploye.setId(1L);
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        int databaseSizeBeforeCreate = statusEmployeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusEmployeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusEmployeRepository.findAll().size();
        // set the field null
        statusEmploye.setUserUuid(null);

        // Create the StatusEmploye, which fails.
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        restStatusEmployeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isBadRequest());

        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStatusEmployes() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList
        restStatusEmployeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusEmploye.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].disponibilite").value(hasItem(DEFAULT_DISPONIBILITE.booleanValue())))
            .andExpect(jsonPath("$.[*].mission").value(hasItem(DEFAULT_MISSION.booleanValue())))
            .andExpect(jsonPath("$.[*].debutConge").value(hasItem(DEFAULT_DEBUT_CONGE.toString())))
            .andExpect(jsonPath("$.[*].finConge").value(hasItem(DEFAULT_FIN_CONGE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStatusEmployesWithEagerRelationshipsIsEnabled() throws Exception {
        when(statusEmployeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStatusEmployeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(statusEmployeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStatusEmployesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(statusEmployeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStatusEmployeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(statusEmployeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getStatusEmploye() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get the statusEmploye
        restStatusEmployeMockMvc
            .perform(get(ENTITY_API_URL_ID, statusEmploye.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statusEmploye.getId().intValue()))
            .andExpect(jsonPath("$.userUuid").value(DEFAULT_USER_UUID.toString()))
            .andExpect(jsonPath("$.disponibilite").value(DEFAULT_DISPONIBILITE.booleanValue()))
            .andExpect(jsonPath("$.mission").value(DEFAULT_MISSION.booleanValue()))
            .andExpect(jsonPath("$.debutConge").value(DEFAULT_DEBUT_CONGE.toString()))
            .andExpect(jsonPath("$.finConge").value(DEFAULT_FIN_CONGE.toString()));
    }

    @Test
    @Transactional
    void getStatusEmployesByIdFiltering() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        Long id = statusEmploye.getId();

        defaultStatusEmployeShouldBeFound("id.equals=" + id);
        defaultStatusEmployeShouldNotBeFound("id.notEquals=" + id);

        defaultStatusEmployeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStatusEmployeShouldNotBeFound("id.greaterThan=" + id);

        defaultStatusEmployeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStatusEmployeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByUserUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where userUuid equals to DEFAULT_USER_UUID
        defaultStatusEmployeShouldBeFound("userUuid.equals=" + DEFAULT_USER_UUID);

        // Get all the statusEmployeList where userUuid equals to UPDATED_USER_UUID
        defaultStatusEmployeShouldNotBeFound("userUuid.equals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByUserUuidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where userUuid not equals to DEFAULT_USER_UUID
        defaultStatusEmployeShouldNotBeFound("userUuid.notEquals=" + DEFAULT_USER_UUID);

        // Get all the statusEmployeList where userUuid not equals to UPDATED_USER_UUID
        defaultStatusEmployeShouldBeFound("userUuid.notEquals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByUserUuidIsInShouldWork() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where userUuid in DEFAULT_USER_UUID or UPDATED_USER_UUID
        defaultStatusEmployeShouldBeFound("userUuid.in=" + DEFAULT_USER_UUID + "," + UPDATED_USER_UUID);

        // Get all the statusEmployeList where userUuid equals to UPDATED_USER_UUID
        defaultStatusEmployeShouldNotBeFound("userUuid.in=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByUserUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where userUuid is not null
        defaultStatusEmployeShouldBeFound("userUuid.specified=true");

        // Get all the statusEmployeList where userUuid is null
        defaultStatusEmployeShouldNotBeFound("userUuid.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDisponibiliteIsEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where disponibilite equals to DEFAULT_DISPONIBILITE
        defaultStatusEmployeShouldBeFound("disponibilite.equals=" + DEFAULT_DISPONIBILITE);

        // Get all the statusEmployeList where disponibilite equals to UPDATED_DISPONIBILITE
        defaultStatusEmployeShouldNotBeFound("disponibilite.equals=" + UPDATED_DISPONIBILITE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDisponibiliteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where disponibilite not equals to DEFAULT_DISPONIBILITE
        defaultStatusEmployeShouldNotBeFound("disponibilite.notEquals=" + DEFAULT_DISPONIBILITE);

        // Get all the statusEmployeList where disponibilite not equals to UPDATED_DISPONIBILITE
        defaultStatusEmployeShouldBeFound("disponibilite.notEquals=" + UPDATED_DISPONIBILITE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDisponibiliteIsInShouldWork() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where disponibilite in DEFAULT_DISPONIBILITE or UPDATED_DISPONIBILITE
        defaultStatusEmployeShouldBeFound("disponibilite.in=" + DEFAULT_DISPONIBILITE + "," + UPDATED_DISPONIBILITE);

        // Get all the statusEmployeList where disponibilite equals to UPDATED_DISPONIBILITE
        defaultStatusEmployeShouldNotBeFound("disponibilite.in=" + UPDATED_DISPONIBILITE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDisponibiliteIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where disponibilite is not null
        defaultStatusEmployeShouldBeFound("disponibilite.specified=true");

        // Get all the statusEmployeList where disponibilite is null
        defaultStatusEmployeShouldNotBeFound("disponibilite.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusEmployesByMissionIsEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where mission equals to DEFAULT_MISSION
        defaultStatusEmployeShouldBeFound("mission.equals=" + DEFAULT_MISSION);

        // Get all the statusEmployeList where mission equals to UPDATED_MISSION
        defaultStatusEmployeShouldNotBeFound("mission.equals=" + UPDATED_MISSION);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByMissionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where mission not equals to DEFAULT_MISSION
        defaultStatusEmployeShouldNotBeFound("mission.notEquals=" + DEFAULT_MISSION);

        // Get all the statusEmployeList where mission not equals to UPDATED_MISSION
        defaultStatusEmployeShouldBeFound("mission.notEquals=" + UPDATED_MISSION);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByMissionIsInShouldWork() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where mission in DEFAULT_MISSION or UPDATED_MISSION
        defaultStatusEmployeShouldBeFound("mission.in=" + DEFAULT_MISSION + "," + UPDATED_MISSION);

        // Get all the statusEmployeList where mission equals to UPDATED_MISSION
        defaultStatusEmployeShouldNotBeFound("mission.in=" + UPDATED_MISSION);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByMissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where mission is not null
        defaultStatusEmployeShouldBeFound("mission.specified=true");

        // Get all the statusEmployeList where mission is null
        defaultStatusEmployeShouldNotBeFound("mission.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge equals to DEFAULT_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.equals=" + DEFAULT_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge equals to UPDATED_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.equals=" + UPDATED_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge not equals to DEFAULT_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.notEquals=" + DEFAULT_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge not equals to UPDATED_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.notEquals=" + UPDATED_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsInShouldWork() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge in DEFAULT_DEBUT_CONGE or UPDATED_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.in=" + DEFAULT_DEBUT_CONGE + "," + UPDATED_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge equals to UPDATED_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.in=" + UPDATED_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge is not null
        defaultStatusEmployeShouldBeFound("debutConge.specified=true");

        // Get all the statusEmployeList where debutConge is null
        defaultStatusEmployeShouldNotBeFound("debutConge.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge is greater than or equal to DEFAULT_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.greaterThanOrEqual=" + DEFAULT_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge is greater than or equal to UPDATED_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.greaterThanOrEqual=" + UPDATED_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge is less than or equal to DEFAULT_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.lessThanOrEqual=" + DEFAULT_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge is less than or equal to SMALLER_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.lessThanOrEqual=" + SMALLER_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsLessThanSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge is less than DEFAULT_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.lessThan=" + DEFAULT_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge is less than UPDATED_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.lessThan=" + UPDATED_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByDebutCongeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where debutConge is greater than DEFAULT_DEBUT_CONGE
        defaultStatusEmployeShouldNotBeFound("debutConge.greaterThan=" + DEFAULT_DEBUT_CONGE);

        // Get all the statusEmployeList where debutConge is greater than SMALLER_DEBUT_CONGE
        defaultStatusEmployeShouldBeFound("debutConge.greaterThan=" + SMALLER_DEBUT_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge equals to DEFAULT_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.equals=" + DEFAULT_FIN_CONGE);

        // Get all the statusEmployeList where finConge equals to UPDATED_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.equals=" + UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge not equals to DEFAULT_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.notEquals=" + DEFAULT_FIN_CONGE);

        // Get all the statusEmployeList where finConge not equals to UPDATED_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.notEquals=" + UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsInShouldWork() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge in DEFAULT_FIN_CONGE or UPDATED_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.in=" + DEFAULT_FIN_CONGE + "," + UPDATED_FIN_CONGE);

        // Get all the statusEmployeList where finConge equals to UPDATED_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.in=" + UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge is not null
        defaultStatusEmployeShouldBeFound("finConge.specified=true");

        // Get all the statusEmployeList where finConge is null
        defaultStatusEmployeShouldNotBeFound("finConge.specified=false");
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge is greater than or equal to DEFAULT_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.greaterThanOrEqual=" + DEFAULT_FIN_CONGE);

        // Get all the statusEmployeList where finConge is greater than or equal to UPDATED_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.greaterThanOrEqual=" + UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge is less than or equal to DEFAULT_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.lessThanOrEqual=" + DEFAULT_FIN_CONGE);

        // Get all the statusEmployeList where finConge is less than or equal to SMALLER_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.lessThanOrEqual=" + SMALLER_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsLessThanSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge is less than DEFAULT_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.lessThan=" + DEFAULT_FIN_CONGE);

        // Get all the statusEmployeList where finConge is less than UPDATED_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.lessThan=" + UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByFinCongeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        // Get all the statusEmployeList where finConge is greater than DEFAULT_FIN_CONGE
        defaultStatusEmployeShouldNotBeFound("finConge.greaterThan=" + DEFAULT_FIN_CONGE);

        // Get all the statusEmployeList where finConge is greater than SMALLER_FIN_CONGE
        defaultStatusEmployeShouldBeFound("finConge.greaterThan=" + SMALLER_FIN_CONGE);
    }

    @Test
    @Transactional
    void getAllStatusEmployesByUsersIsEqualToSomething() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);
        User users;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            users = UserResourceIT.createEntity(em);
            em.persist(users);
            em.flush();
        } else {
            users = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(users);
        em.flush();
        statusEmploye.addUsers(users);
        statusEmployeRepository.saveAndFlush(statusEmploye);
        Long usersId = users.getId();

        // Get all the statusEmployeList where users equals to usersId
        defaultStatusEmployeShouldBeFound("usersId.equals=" + usersId);

        // Get all the statusEmployeList where users equals to (usersId + 1)
        defaultStatusEmployeShouldNotBeFound("usersId.equals=" + (usersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStatusEmployeShouldBeFound(String filter) throws Exception {
        restStatusEmployeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statusEmploye.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].disponibilite").value(hasItem(DEFAULT_DISPONIBILITE.booleanValue())))
            .andExpect(jsonPath("$.[*].mission").value(hasItem(DEFAULT_MISSION.booleanValue())))
            .andExpect(jsonPath("$.[*].debutConge").value(hasItem(DEFAULT_DEBUT_CONGE.toString())))
            .andExpect(jsonPath("$.[*].finConge").value(hasItem(DEFAULT_FIN_CONGE.toString())));

        // Check, that the count call also returns 1
        restStatusEmployeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStatusEmployeShouldNotBeFound(String filter) throws Exception {
        restStatusEmployeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStatusEmployeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStatusEmploye() throws Exception {
        // Get the statusEmploye
        restStatusEmployeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStatusEmploye() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();

        // Update the statusEmploye
        StatusEmploye updatedStatusEmploye = statusEmployeRepository.findById(statusEmploye.getId()).get();
        // Disconnect from session so that the updates on updatedStatusEmploye are not directly saved in db
        em.detach(updatedStatusEmploye);
        updatedStatusEmploye
            .userUuid(UPDATED_USER_UUID)
            .disponibilite(UPDATED_DISPONIBILITE)
            .mission(UPDATED_MISSION)
            .debutConge(UPDATED_DEBUT_CONGE)
            .finConge(UPDATED_FIN_CONGE);
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(updatedStatusEmploye);

        restStatusEmployeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusEmployeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isOk());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(UPDATED_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(UPDATED_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(UPDATED_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void putNonExistingStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusEmployeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statusEmployeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusEmployeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusEmployeMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatusEmployeWithPatch() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();

        // Update the statusEmploye using partial update
        StatusEmploye partialUpdatedStatusEmploye = new StatusEmploye();
        partialUpdatedStatusEmploye.setId(statusEmploye.getId());

        partialUpdatedStatusEmploye.finConge(UPDATED_FIN_CONGE);

        restStatusEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusEmploye.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatusEmploye))
            )
            .andExpect(status().isOk());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(DEFAULT_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(DEFAULT_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(DEFAULT_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void fullUpdateStatusEmployeWithPatch() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();

        // Update the statusEmploye using partial update
        StatusEmploye partialUpdatedStatusEmploye = new StatusEmploye();
        partialUpdatedStatusEmploye.setId(statusEmploye.getId());

        partialUpdatedStatusEmploye
            .userUuid(UPDATED_USER_UUID)
            .disponibilite(UPDATED_DISPONIBILITE)
            .mission(UPDATED_MISSION)
            .debutConge(UPDATED_DEBUT_CONGE)
            .finConge(UPDATED_FIN_CONGE);

        restStatusEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatusEmploye.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatusEmploye))
            )
            .andExpect(status().isOk());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(UPDATED_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(UPDATED_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(UPDATED_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(UPDATED_FIN_CONGE);
    }

    @Test
    @Transactional
    void patchNonExistingStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statusEmployeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatusEmployeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatusEmploye() throws Exception {
        // Initialize the database
        statusEmployeRepository.saveAndFlush(statusEmploye);

        int databaseSizeBeforeDelete = statusEmployeRepository.findAll().size();

        // Delete the statusEmploye
        restStatusEmployeMockMvc
            .perform(delete(ENTITY_API_URL_ID, statusEmploye.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
