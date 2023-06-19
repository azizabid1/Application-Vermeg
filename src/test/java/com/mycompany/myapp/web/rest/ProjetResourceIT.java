package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.ProjetRepository;
import com.mycompany.myapp.service.ProjetService;
import com.mycompany.myapp.service.criteria.ProjetCriteria;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.mapper.ProjetMapper;
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
 * Integration tests for the {@link ProjetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjetResourceIT {

    private static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
    private static final UUID UPDATED_USER_UUID = UUID.randomUUID();

    private static final String DEFAULT_NOM_PROJET = "AAAAAAAAAA";
    private static final String UPDATED_NOM_PROJET = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_DEBUT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_FIN = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_TECHNOLOGIES = "AAAAAAAAAA";
    private static final String UPDATED_TECHNOLOGIES = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS_PROJET = Status.ATTENTE;
    private static final Status UPDATED_STATUS_PROJET = Status.COURS;

    private static final Long DEFAULT_NOMBRE_TOTAL = 1L;
    private static final Long UPDATED_NOMBRE_TOTAL = 2L;
    private static final Long SMALLER_NOMBRE_TOTAL = 1L - 1L;

    private static final Long DEFAULT_NOMBRE_RESTANT = 1L;
    private static final Long UPDATED_NOMBRE_RESTANT = 2L;
    private static final Long SMALLER_NOMBRE_RESTANT = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/projets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjetRepository projetRepository;

    @Mock
    private ProjetRepository projetRepositoryMock;

    @Autowired
    private ProjetMapper projetMapper;

    @Mock
    private ProjetService projetServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjetMockMvc;

    private Projet projet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projet createEntity(EntityManager em) {
        Projet projet = new Projet()
            .userUuid(DEFAULT_USER_UUID)
            .nomProjet(DEFAULT_NOM_PROJET)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN)
            .technologies(DEFAULT_TECHNOLOGIES)
            .statusProjet(DEFAULT_STATUS_PROJET)
            .nombreTotal(DEFAULT_NOMBRE_TOTAL)
            .nombreRestant(DEFAULT_NOMBRE_RESTANT);
        return projet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projet createUpdatedEntity(EntityManager em) {
        Projet projet = new Projet()
            .userUuid(UPDATED_USER_UUID)
            .nomProjet(UPDATED_NOM_PROJET)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .technologies(UPDATED_TECHNOLOGIES)
            .statusProjet(UPDATED_STATUS_PROJET)
            .nombreTotal(UPDATED_NOMBRE_TOTAL)
            .nombreRestant(UPDATED_NOMBRE_RESTANT);
        return projet;
    }

    @BeforeEach
    public void initTest() {
        projet = createEntity(em);
    }

    @Test
    @Transactional
    void createProjet() throws Exception {
        int databaseSizeBeforeCreate = projetRepository.findAll().size();
        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);
        restProjetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projetDTO)))
            .andExpect(status().isCreated());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeCreate + 1);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
        assertThat(testProjet.getNomProjet()).isEqualTo(DEFAULT_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(DEFAULT_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(DEFAULT_STATUS_PROJET);
        assertThat(testProjet.getNombreTotal()).isEqualTo(DEFAULT_NOMBRE_TOTAL);
        assertThat(testProjet.getNombreRestant()).isEqualTo(DEFAULT_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void createProjetWithExistingId() throws Exception {
        // Create the Projet with an existing ID
        projet.setId(1L);
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        int databaseSizeBeforeCreate = projetRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = projetRepository.findAll().size();
        // set the field null
        projet.setUserUuid(null);

        // Create the Projet, which fails.
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        restProjetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projetDTO)))
            .andExpect(status().isBadRequest());

        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjets() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList
        restProjetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projet.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].nomProjet").value(hasItem(DEFAULT_NOM_PROJET)))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].technologies").value(hasItem(DEFAULT_TECHNOLOGIES)))
            .andExpect(jsonPath("$.[*].statusProjet").value(hasItem(DEFAULT_STATUS_PROJET.toString())))
            .andExpect(jsonPath("$.[*].nombreTotal").value(hasItem(DEFAULT_NOMBRE_TOTAL.intValue())))
            .andExpect(jsonPath("$.[*].nombreRestant").value(hasItem(DEFAULT_NOMBRE_RESTANT.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(projetServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projetServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(projetServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projetServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getProjet() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get the projet
        restProjetMockMvc
            .perform(get(ENTITY_API_URL_ID, projet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projet.getId().intValue()))
            .andExpect(jsonPath("$.userUuid").value(DEFAULT_USER_UUID.toString()))
            .andExpect(jsonPath("$.nomProjet").value(DEFAULT_NOM_PROJET))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()))
            .andExpect(jsonPath("$.technologies").value(DEFAULT_TECHNOLOGIES))
            .andExpect(jsonPath("$.statusProjet").value(DEFAULT_STATUS_PROJET.toString()))
            .andExpect(jsonPath("$.nombreTotal").value(DEFAULT_NOMBRE_TOTAL.intValue()))
            .andExpect(jsonPath("$.nombreRestant").value(DEFAULT_NOMBRE_RESTANT.intValue()));
    }

    @Test
    @Transactional
    void getProjetsByIdFiltering() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        Long id = projet.getId();

        defaultProjetShouldBeFound("id.equals=" + id);
        defaultProjetShouldNotBeFound("id.notEquals=" + id);

        defaultProjetShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjetShouldNotBeFound("id.greaterThan=" + id);

        defaultProjetShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjetShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjetsByUserUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where userUuid equals to DEFAULT_USER_UUID
        defaultProjetShouldBeFound("userUuid.equals=" + DEFAULT_USER_UUID);

        // Get all the projetList where userUuid equals to UPDATED_USER_UUID
        defaultProjetShouldNotBeFound("userUuid.equals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllProjetsByUserUuidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where userUuid not equals to DEFAULT_USER_UUID
        defaultProjetShouldNotBeFound("userUuid.notEquals=" + DEFAULT_USER_UUID);

        // Get all the projetList where userUuid not equals to UPDATED_USER_UUID
        defaultProjetShouldBeFound("userUuid.notEquals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllProjetsByUserUuidIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where userUuid in DEFAULT_USER_UUID or UPDATED_USER_UUID
        defaultProjetShouldBeFound("userUuid.in=" + DEFAULT_USER_UUID + "," + UPDATED_USER_UUID);

        // Get all the projetList where userUuid equals to UPDATED_USER_UUID
        defaultProjetShouldNotBeFound("userUuid.in=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllProjetsByUserUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where userUuid is not null
        defaultProjetShouldBeFound("userUuid.specified=true");

        // Get all the projetList where userUuid is null
        defaultProjetShouldNotBeFound("userUuid.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByNomProjetIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nomProjet equals to DEFAULT_NOM_PROJET
        defaultProjetShouldBeFound("nomProjet.equals=" + DEFAULT_NOM_PROJET);

        // Get all the projetList where nomProjet equals to UPDATED_NOM_PROJET
        defaultProjetShouldNotBeFound("nomProjet.equals=" + UPDATED_NOM_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByNomProjetIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nomProjet not equals to DEFAULT_NOM_PROJET
        defaultProjetShouldNotBeFound("nomProjet.notEquals=" + DEFAULT_NOM_PROJET);

        // Get all the projetList where nomProjet not equals to UPDATED_NOM_PROJET
        defaultProjetShouldBeFound("nomProjet.notEquals=" + UPDATED_NOM_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByNomProjetIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nomProjet in DEFAULT_NOM_PROJET or UPDATED_NOM_PROJET
        defaultProjetShouldBeFound("nomProjet.in=" + DEFAULT_NOM_PROJET + "," + UPDATED_NOM_PROJET);

        // Get all the projetList where nomProjet equals to UPDATED_NOM_PROJET
        defaultProjetShouldNotBeFound("nomProjet.in=" + UPDATED_NOM_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByNomProjetIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nomProjet is not null
        defaultProjetShouldBeFound("nomProjet.specified=true");

        // Get all the projetList where nomProjet is null
        defaultProjetShouldNotBeFound("nomProjet.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByNomProjetContainsSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nomProjet contains DEFAULT_NOM_PROJET
        defaultProjetShouldBeFound("nomProjet.contains=" + DEFAULT_NOM_PROJET);

        // Get all the projetList where nomProjet contains UPDATED_NOM_PROJET
        defaultProjetShouldNotBeFound("nomProjet.contains=" + UPDATED_NOM_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByNomProjetNotContainsSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nomProjet does not contain DEFAULT_NOM_PROJET
        defaultProjetShouldNotBeFound("nomProjet.doesNotContain=" + DEFAULT_NOM_PROJET);

        // Get all the projetList where nomProjet does not contain UPDATED_NOM_PROJET
        defaultProjetShouldBeFound("nomProjet.doesNotContain=" + UPDATED_NOM_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut equals to DEFAULT_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.equals=" + DEFAULT_DATE_DEBUT);

        // Get all the projetList where dateDebut equals to UPDATED_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.equals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut not equals to DEFAULT_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.notEquals=" + DEFAULT_DATE_DEBUT);

        // Get all the projetList where dateDebut not equals to UPDATED_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.notEquals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut in DEFAULT_DATE_DEBUT or UPDATED_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.in=" + DEFAULT_DATE_DEBUT + "," + UPDATED_DATE_DEBUT);

        // Get all the projetList where dateDebut equals to UPDATED_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.in=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut is not null
        defaultProjetShouldBeFound("dateDebut.specified=true");

        // Get all the projetList where dateDebut is null
        defaultProjetShouldNotBeFound("dateDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut is greater than or equal to DEFAULT_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.greaterThanOrEqual=" + DEFAULT_DATE_DEBUT);

        // Get all the projetList where dateDebut is greater than or equal to UPDATED_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.greaterThanOrEqual=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut is less than or equal to DEFAULT_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.lessThanOrEqual=" + DEFAULT_DATE_DEBUT);

        // Get all the projetList where dateDebut is less than or equal to SMALLER_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.lessThanOrEqual=" + SMALLER_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsLessThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut is less than DEFAULT_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.lessThan=" + DEFAULT_DATE_DEBUT);

        // Get all the projetList where dateDebut is less than UPDATED_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.lessThan=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateDebutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateDebut is greater than DEFAULT_DATE_DEBUT
        defaultProjetShouldNotBeFound("dateDebut.greaterThan=" + DEFAULT_DATE_DEBUT);

        // Get all the projetList where dateDebut is greater than SMALLER_DATE_DEBUT
        defaultProjetShouldBeFound("dateDebut.greaterThan=" + SMALLER_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin equals to DEFAULT_DATE_FIN
        defaultProjetShouldBeFound("dateFin.equals=" + DEFAULT_DATE_FIN);

        // Get all the projetList where dateFin equals to UPDATED_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.equals=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin not equals to DEFAULT_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.notEquals=" + DEFAULT_DATE_FIN);

        // Get all the projetList where dateFin not equals to UPDATED_DATE_FIN
        defaultProjetShouldBeFound("dateFin.notEquals=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin in DEFAULT_DATE_FIN or UPDATED_DATE_FIN
        defaultProjetShouldBeFound("dateFin.in=" + DEFAULT_DATE_FIN + "," + UPDATED_DATE_FIN);

        // Get all the projetList where dateFin equals to UPDATED_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.in=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin is not null
        defaultProjetShouldBeFound("dateFin.specified=true");

        // Get all the projetList where dateFin is null
        defaultProjetShouldNotBeFound("dateFin.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin is greater than or equal to DEFAULT_DATE_FIN
        defaultProjetShouldBeFound("dateFin.greaterThanOrEqual=" + DEFAULT_DATE_FIN);

        // Get all the projetList where dateFin is greater than or equal to UPDATED_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.greaterThanOrEqual=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin is less than or equal to DEFAULT_DATE_FIN
        defaultProjetShouldBeFound("dateFin.lessThanOrEqual=" + DEFAULT_DATE_FIN);

        // Get all the projetList where dateFin is less than or equal to SMALLER_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.lessThanOrEqual=" + SMALLER_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsLessThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin is less than DEFAULT_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.lessThan=" + DEFAULT_DATE_FIN);

        // Get all the projetList where dateFin is less than UPDATED_DATE_FIN
        defaultProjetShouldBeFound("dateFin.lessThan=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByDateFinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where dateFin is greater than DEFAULT_DATE_FIN
        defaultProjetShouldNotBeFound("dateFin.greaterThan=" + DEFAULT_DATE_FIN);

        // Get all the projetList where dateFin is greater than SMALLER_DATE_FIN
        defaultProjetShouldBeFound("dateFin.greaterThan=" + SMALLER_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllProjetsByTechnologiesIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where technologies equals to DEFAULT_TECHNOLOGIES
        defaultProjetShouldBeFound("technologies.equals=" + DEFAULT_TECHNOLOGIES);

        // Get all the projetList where technologies equals to UPDATED_TECHNOLOGIES
        defaultProjetShouldNotBeFound("technologies.equals=" + UPDATED_TECHNOLOGIES);
    }

    @Test
    @Transactional
    void getAllProjetsByTechnologiesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where technologies not equals to DEFAULT_TECHNOLOGIES
        defaultProjetShouldNotBeFound("technologies.notEquals=" + DEFAULT_TECHNOLOGIES);

        // Get all the projetList where technologies not equals to UPDATED_TECHNOLOGIES
        defaultProjetShouldBeFound("technologies.notEquals=" + UPDATED_TECHNOLOGIES);
    }

    @Test
    @Transactional
    void getAllProjetsByTechnologiesIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where technologies in DEFAULT_TECHNOLOGIES or UPDATED_TECHNOLOGIES
        defaultProjetShouldBeFound("technologies.in=" + DEFAULT_TECHNOLOGIES + "," + UPDATED_TECHNOLOGIES);

        // Get all the projetList where technologies equals to UPDATED_TECHNOLOGIES
        defaultProjetShouldNotBeFound("technologies.in=" + UPDATED_TECHNOLOGIES);
    }

    @Test
    @Transactional
    void getAllProjetsByTechnologiesIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where technologies is not null
        defaultProjetShouldBeFound("technologies.specified=true");

        // Get all the projetList where technologies is null
        defaultProjetShouldNotBeFound("technologies.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByTechnologiesContainsSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where technologies contains DEFAULT_TECHNOLOGIES
        defaultProjetShouldBeFound("technologies.contains=" + DEFAULT_TECHNOLOGIES);

        // Get all the projetList where technologies contains UPDATED_TECHNOLOGIES
        defaultProjetShouldNotBeFound("technologies.contains=" + UPDATED_TECHNOLOGIES);
    }

    @Test
    @Transactional
    void getAllProjetsByTechnologiesNotContainsSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where technologies does not contain DEFAULT_TECHNOLOGIES
        defaultProjetShouldNotBeFound("technologies.doesNotContain=" + DEFAULT_TECHNOLOGIES);

        // Get all the projetList where technologies does not contain UPDATED_TECHNOLOGIES
        defaultProjetShouldBeFound("technologies.doesNotContain=" + UPDATED_TECHNOLOGIES);
    }

    @Test
    @Transactional
    void getAllProjetsByStatusProjetIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where statusProjet equals to DEFAULT_STATUS_PROJET
        defaultProjetShouldBeFound("statusProjet.equals=" + DEFAULT_STATUS_PROJET);

        // Get all the projetList where statusProjet equals to UPDATED_STATUS_PROJET
        defaultProjetShouldNotBeFound("statusProjet.equals=" + UPDATED_STATUS_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByStatusProjetIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where statusProjet not equals to DEFAULT_STATUS_PROJET
        defaultProjetShouldNotBeFound("statusProjet.notEquals=" + DEFAULT_STATUS_PROJET);

        // Get all the projetList where statusProjet not equals to UPDATED_STATUS_PROJET
        defaultProjetShouldBeFound("statusProjet.notEquals=" + UPDATED_STATUS_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByStatusProjetIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where statusProjet in DEFAULT_STATUS_PROJET or UPDATED_STATUS_PROJET
        defaultProjetShouldBeFound("statusProjet.in=" + DEFAULT_STATUS_PROJET + "," + UPDATED_STATUS_PROJET);

        // Get all the projetList where statusProjet equals to UPDATED_STATUS_PROJET
        defaultProjetShouldNotBeFound("statusProjet.in=" + UPDATED_STATUS_PROJET);
    }

    @Test
    @Transactional
    void getAllProjetsByStatusProjetIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where statusProjet is not null
        defaultProjetShouldBeFound("statusProjet.specified=true");

        // Get all the projetList where statusProjet is null
        defaultProjetShouldNotBeFound("statusProjet.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal equals to DEFAULT_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.equals=" + DEFAULT_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal equals to UPDATED_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.equals=" + UPDATED_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal not equals to DEFAULT_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.notEquals=" + DEFAULT_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal not equals to UPDATED_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.notEquals=" + UPDATED_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal in DEFAULT_NOMBRE_TOTAL or UPDATED_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.in=" + DEFAULT_NOMBRE_TOTAL + "," + UPDATED_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal equals to UPDATED_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.in=" + UPDATED_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal is not null
        defaultProjetShouldBeFound("nombreTotal.specified=true");

        // Get all the projetList where nombreTotal is null
        defaultProjetShouldNotBeFound("nombreTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal is greater than or equal to DEFAULT_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.greaterThanOrEqual=" + DEFAULT_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal is greater than or equal to UPDATED_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.greaterThanOrEqual=" + UPDATED_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal is less than or equal to DEFAULT_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.lessThanOrEqual=" + DEFAULT_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal is less than or equal to SMALLER_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.lessThanOrEqual=" + SMALLER_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal is less than DEFAULT_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.lessThan=" + DEFAULT_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal is less than UPDATED_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.lessThan=" + UPDATED_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreTotal is greater than DEFAULT_NOMBRE_TOTAL
        defaultProjetShouldNotBeFound("nombreTotal.greaterThan=" + DEFAULT_NOMBRE_TOTAL);

        // Get all the projetList where nombreTotal is greater than SMALLER_NOMBRE_TOTAL
        defaultProjetShouldBeFound("nombreTotal.greaterThan=" + SMALLER_NOMBRE_TOTAL);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant equals to DEFAULT_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.equals=" + DEFAULT_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant equals to UPDATED_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.equals=" + UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant not equals to DEFAULT_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.notEquals=" + DEFAULT_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant not equals to UPDATED_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.notEquals=" + UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsInShouldWork() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant in DEFAULT_NOMBRE_RESTANT or UPDATED_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.in=" + DEFAULT_NOMBRE_RESTANT + "," + UPDATED_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant equals to UPDATED_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.in=" + UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsNullOrNotNull() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant is not null
        defaultProjetShouldBeFound("nombreRestant.specified=true");

        // Get all the projetList where nombreRestant is null
        defaultProjetShouldNotBeFound("nombreRestant.specified=false");
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant is greater than or equal to DEFAULT_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.greaterThanOrEqual=" + DEFAULT_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant is greater than or equal to UPDATED_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.greaterThanOrEqual=" + UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant is less than or equal to DEFAULT_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.lessThanOrEqual=" + DEFAULT_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant is less than or equal to SMALLER_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.lessThanOrEqual=" + SMALLER_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsLessThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant is less than DEFAULT_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.lessThan=" + DEFAULT_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant is less than UPDATED_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.lessThan=" + UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByNombreRestantIsGreaterThanSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        // Get all the projetList where nombreRestant is greater than DEFAULT_NOMBRE_RESTANT
        defaultProjetShouldNotBeFound("nombreRestant.greaterThan=" + DEFAULT_NOMBRE_RESTANT);

        // Get all the projetList where nombreRestant is greater than SMALLER_NOMBRE_RESTANT
        defaultProjetShouldBeFound("nombreRestant.greaterThan=" + SMALLER_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void getAllProjetsByEquipeIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);
        Equipe equipe;
        if (TestUtil.findAll(em, Equipe.class).isEmpty()) {
            equipe = EquipeResourceIT.createEntity(em);
            em.persist(equipe);
            em.flush();
        } else {
            equipe = TestUtil.findAll(em, Equipe.class).get(0);
        }
        em.persist(equipe);
        em.flush();
        projet.setEquipe(equipe);
        projetRepository.saveAndFlush(projet);
        Long equipeId = equipe.getId();

        // Get all the projetList where equipe equals to equipeId
        defaultProjetShouldBeFound("equipeId.equals=" + equipeId);

        // Get all the projetList where equipe equals to (equipeId + 1)
        defaultProjetShouldNotBeFound("equipeId.equals=" + (equipeId + 1));
    }

    @Test
    @Transactional
    void getAllProjetsByTachesIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);
        Tache taches;
        if (TestUtil.findAll(em, Tache.class).isEmpty()) {
            taches = TacheResourceIT.createEntity(em);
            em.persist(taches);
            em.flush();
        } else {
            taches = TestUtil.findAll(em, Tache.class).get(0);
        }
        em.persist(taches);
        em.flush();
        projet.addTaches(taches);
        projetRepository.saveAndFlush(projet);
        Long tachesId = taches.getId();

        // Get all the projetList where taches equals to tachesId
        defaultProjetShouldBeFound("tachesId.equals=" + tachesId);

        // Get all the projetList where taches equals to (tachesId + 1)
        defaultProjetShouldNotBeFound("tachesId.equals=" + (tachesId + 1));
    }

    @Test
    @Transactional
    void getAllProjetsByDevisIsEqualToSomething() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);
        Devis devis;
        if (TestUtil.findAll(em, Devis.class).isEmpty()) {
            devis = DevisResourceIT.createEntity(em);
            em.persist(devis);
            em.flush();
        } else {
            devis = TestUtil.findAll(em, Devis.class).get(0);
        }
        em.persist(devis);
        em.flush();
        projet.setDevis(devis);
        devis.setProjet(projet);
        projetRepository.saveAndFlush(projet);
        Long devisId = devis.getId();

        // Get all the projetList where devis equals to devisId
        defaultProjetShouldBeFound("devisId.equals=" + devisId);

        // Get all the projetList where devis equals to (devisId + 1)
        defaultProjetShouldNotBeFound("devisId.equals=" + (devisId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjetShouldBeFound(String filter) throws Exception {
        restProjetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projet.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].nomProjet").value(hasItem(DEFAULT_NOM_PROJET)))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].technologies").value(hasItem(DEFAULT_TECHNOLOGIES)))
            .andExpect(jsonPath("$.[*].statusProjet").value(hasItem(DEFAULT_STATUS_PROJET.toString())))
            .andExpect(jsonPath("$.[*].nombreTotal").value(hasItem(DEFAULT_NOMBRE_TOTAL.intValue())))
            .andExpect(jsonPath("$.[*].nombreRestant").value(hasItem(DEFAULT_NOMBRE_RESTANT.intValue())));

        // Check, that the count call also returns 1
        restProjetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjetShouldNotBeFound(String filter) throws Exception {
        restProjetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjet() throws Exception {
        // Get the projet
        restProjetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProjet() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        int databaseSizeBeforeUpdate = projetRepository.findAll().size();

        // Update the projet
        Projet updatedProjet = projetRepository.findById(projet.getId()).get();
        // Disconnect from session so that the updates on updatedProjet are not directly saved in db
        em.detach(updatedProjet);
        updatedProjet
            .userUuid(UPDATED_USER_UUID)
            .nomProjet(UPDATED_NOM_PROJET)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .technologies(UPDATED_TECHNOLOGIES)
            .statusProjet(UPDATED_STATUS_PROJET)
            .nombreTotal(UPDATED_NOMBRE_TOTAL)
            .nombreRestant(UPDATED_NOMBRE_RESTANT);
        ProjetDTO projetDTO = projetMapper.toDto(updatedProjet);

        restProjetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projetDTO))
            )
            .andExpect(status().isOk());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testProjet.getNomProjet()).isEqualTo(UPDATED_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(UPDATED_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(UPDATED_STATUS_PROJET);
        assertThat(testProjet.getNombreTotal()).isEqualTo(UPDATED_NOMBRE_TOTAL);
        assertThat(testProjet.getNombreRestant()).isEqualTo(UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void putNonExistingProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(projetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(projetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjetWithPatch() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        int databaseSizeBeforeUpdate = projetRepository.findAll().size();

        // Update the projet using partial update
        Projet partialUpdatedProjet = new Projet();
        partialUpdatedProjet.setId(projet.getId());

        partialUpdatedProjet.dateDebut(UPDATED_DATE_DEBUT);

        restProjetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjet))
            )
            .andExpect(status().isOk());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
        assertThat(testProjet.getNomProjet()).isEqualTo(DEFAULT_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(DEFAULT_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(DEFAULT_STATUS_PROJET);
        assertThat(testProjet.getNombreTotal()).isEqualTo(DEFAULT_NOMBRE_TOTAL);
        assertThat(testProjet.getNombreRestant()).isEqualTo(DEFAULT_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void fullUpdateProjetWithPatch() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        int databaseSizeBeforeUpdate = projetRepository.findAll().size();

        // Update the projet using partial update
        Projet partialUpdatedProjet = new Projet();
        partialUpdatedProjet.setId(projet.getId());

        partialUpdatedProjet
            .userUuid(UPDATED_USER_UUID)
            .nomProjet(UPDATED_NOM_PROJET)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .technologies(UPDATED_TECHNOLOGIES)
            .statusProjet(UPDATED_STATUS_PROJET)
            .nombreTotal(UPDATED_NOMBRE_TOTAL)
            .nombreRestant(UPDATED_NOMBRE_RESTANT);

        restProjetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProjet))
            )
            .andExpect(status().isOk());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testProjet.getNomProjet()).isEqualTo(UPDATED_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(UPDATED_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(UPDATED_STATUS_PROJET);
        assertThat(testProjet.getNombreTotal()).isEqualTo(UPDATED_NOMBRE_TOTAL);
        assertThat(testProjet.getNombreRestant()).isEqualTo(UPDATED_NOMBRE_RESTANT);
    }

    @Test
    @Transactional
    void patchNonExistingProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projetDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(projetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjetMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(projetDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjet() throws Exception {
        // Initialize the database
        projetRepository.saveAndFlush(projet);

        int databaseSizeBeforeDelete = projetRepository.findAll().size();

        // Delete the projet
        restProjetMockMvc
            .perform(delete(ENTITY_API_URL_ID, projet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Projet> projetList = projetRepository.findAll();
        assertThat(projetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
