package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.service.criteria.DevisCriteria;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.mapper.DevisMapper;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DevisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DevisResourceIT {

    private static final Double DEFAULT_PRIX_TOTAL = 1D;
    private static final Double UPDATED_PRIX_TOTAL = 2D;
    private static final Double SMALLER_PRIX_TOTAL = 1D - 1D;

    private static final Double DEFAULT_PRIX_HT = 1D;
    private static final Double UPDATED_PRIX_HT = 2D;
    private static final Double SMALLER_PRIX_HT = 1D - 1D;

    private static final Double DEFAULT_PRIX_SERVICE = 1D;
    private static final Double UPDATED_PRIX_SERVICE = 2D;
    private static final Double SMALLER_PRIX_SERVICE = 1D - 1D;

    private static final Float DEFAULT_DUREE_PROJET = 0F;
    private static final Float UPDATED_DUREE_PROJET = 1F;
    private static final Float SMALLER_DUREE_PROJET = 0F - 1F;

    private static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
    private static final UUID UPDATED_USER_UUID = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/devis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DevisRepository devisRepository;

    @Autowired
    private DevisMapper devisMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDevisMockMvc;

    private Devis devis;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Devis createEntity(EntityManager em) {
        Devis devis = new Devis()
            .prixTotal(DEFAULT_PRIX_TOTAL)
            .prixHT(DEFAULT_PRIX_HT)
            .prixService(DEFAULT_PRIX_SERVICE)
            .dureeProjet(DEFAULT_DUREE_PROJET)
            .userUuid(DEFAULT_USER_UUID);
        return devis;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Devis createUpdatedEntity(EntityManager em) {
        Devis devis = new Devis()
            .prixTotal(UPDATED_PRIX_TOTAL)
            .prixHT(UPDATED_PRIX_HT)
            .prixService(UPDATED_PRIX_SERVICE)
            .dureeProjet(UPDATED_DUREE_PROJET)
            .userUuid(UPDATED_USER_UUID);
        return devis;
    }

    @BeforeEach
    public void initTest() {
        devis = createEntity(em);
    }

    @Test
    @Transactional
    void createDevis() throws Exception {
        int databaseSizeBeforeCreate = devisRepository.findAll().size();
        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);
        restDevisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(devisDTO)))
            .andExpect(status().isCreated());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeCreate + 1);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(DEFAULT_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(DEFAULT_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(DEFAULT_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(DEFAULT_DUREE_PROJET);
        assertThat(testDevis.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
    }

    @Test
    @Transactional
    void createDevisWithExistingId() throws Exception {
        // Create the Devis with an existing ID
        devis.setId(1L);
        DevisDTO devisDTO = devisMapper.toDto(devis);

        int databaseSizeBeforeCreate = devisRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDevisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(devisDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = devisRepository.findAll().size();
        // set the field null
        devis.setUserUuid(null);

        // Create the Devis, which fails.
        DevisDTO devisDTO = devisMapper.toDto(devis);

        restDevisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(devisDTO)))
            .andExpect(status().isBadRequest());

        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDevis() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList
        restDevisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(devis.getId().intValue())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(DEFAULT_PRIX_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].prixHT").value(hasItem(DEFAULT_PRIX_HT.doubleValue())))
            .andExpect(jsonPath("$.[*].prixService").value(hasItem(DEFAULT_PRIX_SERVICE.doubleValue())))
            .andExpect(jsonPath("$.[*].dureeProjet").value(hasItem(DEFAULT_DUREE_PROJET.doubleValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())));
    }

    @Test
    @Transactional
    void getDevis() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get the devis
        restDevisMockMvc
            .perform(get(ENTITY_API_URL_ID, devis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(devis.getId().intValue()))
            .andExpect(jsonPath("$.prixTotal").value(DEFAULT_PRIX_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.prixHT").value(DEFAULT_PRIX_HT.doubleValue()))
            .andExpect(jsonPath("$.prixService").value(DEFAULT_PRIX_SERVICE.doubleValue()))
            .andExpect(jsonPath("$.dureeProjet").value(DEFAULT_DUREE_PROJET.doubleValue()))
            .andExpect(jsonPath("$.userUuid").value(DEFAULT_USER_UUID.toString()));
    }

    @Test
    @Transactional
    void getDevisByIdFiltering() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        Long id = devis.getId();

        defaultDevisShouldBeFound("id.equals=" + id);
        defaultDevisShouldNotBeFound("id.notEquals=" + id);

        defaultDevisShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDevisShouldNotBeFound("id.greaterThan=" + id);

        defaultDevisShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDevisShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal equals to DEFAULT_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.equals=" + DEFAULT_PRIX_TOTAL);

        // Get all the devisList where prixTotal equals to UPDATED_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.equals=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal not equals to DEFAULT_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.notEquals=" + DEFAULT_PRIX_TOTAL);

        // Get all the devisList where prixTotal not equals to UPDATED_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.notEquals=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsInShouldWork() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal in DEFAULT_PRIX_TOTAL or UPDATED_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.in=" + DEFAULT_PRIX_TOTAL + "," + UPDATED_PRIX_TOTAL);

        // Get all the devisList where prixTotal equals to UPDATED_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.in=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal is not null
        defaultDevisShouldBeFound("prixTotal.specified=true");

        // Get all the devisList where prixTotal is null
        defaultDevisShouldNotBeFound("prixTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal is greater than or equal to DEFAULT_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.greaterThanOrEqual=" + DEFAULT_PRIX_TOTAL);

        // Get all the devisList where prixTotal is greater than or equal to UPDATED_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.greaterThanOrEqual=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal is less than or equal to DEFAULT_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.lessThanOrEqual=" + DEFAULT_PRIX_TOTAL);

        // Get all the devisList where prixTotal is less than or equal to SMALLER_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.lessThanOrEqual=" + SMALLER_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal is less than DEFAULT_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.lessThan=" + DEFAULT_PRIX_TOTAL);

        // Get all the devisList where prixTotal is less than UPDATED_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.lessThan=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixTotal is greater than DEFAULT_PRIX_TOTAL
        defaultDevisShouldNotBeFound("prixTotal.greaterThan=" + DEFAULT_PRIX_TOTAL);

        // Get all the devisList where prixTotal is greater than SMALLER_PRIX_TOTAL
        defaultDevisShouldBeFound("prixTotal.greaterThan=" + SMALLER_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT equals to DEFAULT_PRIX_HT
        defaultDevisShouldBeFound("prixHT.equals=" + DEFAULT_PRIX_HT);

        // Get all the devisList where prixHT equals to UPDATED_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.equals=" + UPDATED_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsNotEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT not equals to DEFAULT_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.notEquals=" + DEFAULT_PRIX_HT);

        // Get all the devisList where prixHT not equals to UPDATED_PRIX_HT
        defaultDevisShouldBeFound("prixHT.notEquals=" + UPDATED_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsInShouldWork() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT in DEFAULT_PRIX_HT or UPDATED_PRIX_HT
        defaultDevisShouldBeFound("prixHT.in=" + DEFAULT_PRIX_HT + "," + UPDATED_PRIX_HT);

        // Get all the devisList where prixHT equals to UPDATED_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.in=" + UPDATED_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsNullOrNotNull() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT is not null
        defaultDevisShouldBeFound("prixHT.specified=true");

        // Get all the devisList where prixHT is null
        defaultDevisShouldNotBeFound("prixHT.specified=false");
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT is greater than or equal to DEFAULT_PRIX_HT
        defaultDevisShouldBeFound("prixHT.greaterThanOrEqual=" + DEFAULT_PRIX_HT);

        // Get all the devisList where prixHT is greater than or equal to UPDATED_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.greaterThanOrEqual=" + UPDATED_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT is less than or equal to DEFAULT_PRIX_HT
        defaultDevisShouldBeFound("prixHT.lessThanOrEqual=" + DEFAULT_PRIX_HT);

        // Get all the devisList where prixHT is less than or equal to SMALLER_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.lessThanOrEqual=" + SMALLER_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsLessThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT is less than DEFAULT_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.lessThan=" + DEFAULT_PRIX_HT);

        // Get all the devisList where prixHT is less than UPDATED_PRIX_HT
        defaultDevisShouldBeFound("prixHT.lessThan=" + UPDATED_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixHTIsGreaterThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixHT is greater than DEFAULT_PRIX_HT
        defaultDevisShouldNotBeFound("prixHT.greaterThan=" + DEFAULT_PRIX_HT);

        // Get all the devisList where prixHT is greater than SMALLER_PRIX_HT
        defaultDevisShouldBeFound("prixHT.greaterThan=" + SMALLER_PRIX_HT);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService equals to DEFAULT_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.equals=" + DEFAULT_PRIX_SERVICE);

        // Get all the devisList where prixService equals to UPDATED_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.equals=" + UPDATED_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService not equals to DEFAULT_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.notEquals=" + DEFAULT_PRIX_SERVICE);

        // Get all the devisList where prixService not equals to UPDATED_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.notEquals=" + UPDATED_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsInShouldWork() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService in DEFAULT_PRIX_SERVICE or UPDATED_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.in=" + DEFAULT_PRIX_SERVICE + "," + UPDATED_PRIX_SERVICE);

        // Get all the devisList where prixService equals to UPDATED_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.in=" + UPDATED_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsNullOrNotNull() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService is not null
        defaultDevisShouldBeFound("prixService.specified=true");

        // Get all the devisList where prixService is null
        defaultDevisShouldNotBeFound("prixService.specified=false");
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService is greater than or equal to DEFAULT_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.greaterThanOrEqual=" + DEFAULT_PRIX_SERVICE);

        // Get all the devisList where prixService is greater than or equal to UPDATED_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.greaterThanOrEqual=" + UPDATED_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService is less than or equal to DEFAULT_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.lessThanOrEqual=" + DEFAULT_PRIX_SERVICE);

        // Get all the devisList where prixService is less than or equal to SMALLER_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.lessThanOrEqual=" + SMALLER_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsLessThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService is less than DEFAULT_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.lessThan=" + DEFAULT_PRIX_SERVICE);

        // Get all the devisList where prixService is less than UPDATED_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.lessThan=" + UPDATED_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByPrixServiceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where prixService is greater than DEFAULT_PRIX_SERVICE
        defaultDevisShouldNotBeFound("prixService.greaterThan=" + DEFAULT_PRIX_SERVICE);

        // Get all the devisList where prixService is greater than SMALLER_PRIX_SERVICE
        defaultDevisShouldBeFound("prixService.greaterThan=" + SMALLER_PRIX_SERVICE);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet equals to DEFAULT_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.equals=" + DEFAULT_DUREE_PROJET);

        // Get all the devisList where dureeProjet equals to UPDATED_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.equals=" + UPDATED_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsNotEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet not equals to DEFAULT_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.notEquals=" + DEFAULT_DUREE_PROJET);

        // Get all the devisList where dureeProjet not equals to UPDATED_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.notEquals=" + UPDATED_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsInShouldWork() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet in DEFAULT_DUREE_PROJET or UPDATED_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.in=" + DEFAULT_DUREE_PROJET + "," + UPDATED_DUREE_PROJET);

        // Get all the devisList where dureeProjet equals to UPDATED_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.in=" + UPDATED_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsNullOrNotNull() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet is not null
        defaultDevisShouldBeFound("dureeProjet.specified=true");

        // Get all the devisList where dureeProjet is null
        defaultDevisShouldNotBeFound("dureeProjet.specified=false");
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet is greater than or equal to DEFAULT_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.greaterThanOrEqual=" + DEFAULT_DUREE_PROJET);

        // Get all the devisList where dureeProjet is greater than or equal to UPDATED_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.greaterThanOrEqual=" + UPDATED_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet is less than or equal to DEFAULT_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.lessThanOrEqual=" + DEFAULT_DUREE_PROJET);

        // Get all the devisList where dureeProjet is less than or equal to SMALLER_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.lessThanOrEqual=" + SMALLER_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsLessThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet is less than DEFAULT_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.lessThan=" + DEFAULT_DUREE_PROJET);

        // Get all the devisList where dureeProjet is less than UPDATED_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.lessThan=" + UPDATED_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByDureeProjetIsGreaterThanSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where dureeProjet is greater than DEFAULT_DUREE_PROJET
        defaultDevisShouldNotBeFound("dureeProjet.greaterThan=" + DEFAULT_DUREE_PROJET);

        // Get all the devisList where dureeProjet is greater than SMALLER_DUREE_PROJET
        defaultDevisShouldBeFound("dureeProjet.greaterThan=" + SMALLER_DUREE_PROJET);
    }

    @Test
    @Transactional
    void getAllDevisByUserUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where userUuid equals to DEFAULT_USER_UUID
        defaultDevisShouldBeFound("userUuid.equals=" + DEFAULT_USER_UUID);

        // Get all the devisList where userUuid equals to UPDATED_USER_UUID
        defaultDevisShouldNotBeFound("userUuid.equals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllDevisByUserUuidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where userUuid not equals to DEFAULT_USER_UUID
        defaultDevisShouldNotBeFound("userUuid.notEquals=" + DEFAULT_USER_UUID);

        // Get all the devisList where userUuid not equals to UPDATED_USER_UUID
        defaultDevisShouldBeFound("userUuid.notEquals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllDevisByUserUuidIsInShouldWork() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where userUuid in DEFAULT_USER_UUID or UPDATED_USER_UUID
        defaultDevisShouldBeFound("userUuid.in=" + DEFAULT_USER_UUID + "," + UPDATED_USER_UUID);

        // Get all the devisList where userUuid equals to UPDATED_USER_UUID
        defaultDevisShouldNotBeFound("userUuid.in=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllDevisByUserUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        // Get all the devisList where userUuid is not null
        defaultDevisShouldBeFound("userUuid.specified=true");

        // Get all the devisList where userUuid is null
        defaultDevisShouldNotBeFound("userUuid.specified=false");
    }

    @Test
    @Transactional
    void getAllDevisByProjetIsEqualToSomething() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);
        Projet projet;
        if (TestUtil.findAll(em, Projet.class).isEmpty()) {
            projet = ProjetResourceIT.createEntity(em);
            em.persist(projet);
            em.flush();
        } else {
            projet = TestUtil.findAll(em, Projet.class).get(0);
        }
        em.persist(projet);
        em.flush();
        devis.setProjet(projet);
        devisRepository.saveAndFlush(devis);
        Long projetId = projet.getId();

        // Get all the devisList where projet equals to projetId
        defaultDevisShouldBeFound("projetId.equals=" + projetId);

        // Get all the devisList where projet equals to (projetId + 1)
        defaultDevisShouldNotBeFound("projetId.equals=" + (projetId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDevisShouldBeFound(String filter) throws Exception {
        restDevisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(devis.getId().intValue())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(DEFAULT_PRIX_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].prixHT").value(hasItem(DEFAULT_PRIX_HT.doubleValue())))
            .andExpect(jsonPath("$.[*].prixService").value(hasItem(DEFAULT_PRIX_SERVICE.doubleValue())))
            .andExpect(jsonPath("$.[*].dureeProjet").value(hasItem(DEFAULT_DUREE_PROJET.doubleValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())));

        // Check, that the count call also returns 1
        restDevisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDevisShouldNotBeFound(String filter) throws Exception {
        restDevisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDevisMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDevis() throws Exception {
        // Get the devis
        restDevisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDevis() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        int databaseSizeBeforeUpdate = devisRepository.findAll().size();

        // Update the devis
        Devis updatedDevis = devisRepository.findById(devis.getId()).get();
        // Disconnect from session so that the updates on updatedDevis are not directly saved in db
        em.detach(updatedDevis);
        updatedDevis
            .prixTotal(UPDATED_PRIX_TOTAL)
            .prixHT(UPDATED_PRIX_HT)
            .prixService(UPDATED_PRIX_SERVICE)
            .dureeProjet(UPDATED_DUREE_PROJET)
            .userUuid(UPDATED_USER_UUID);
        DevisDTO devisDTO = devisMapper.toDto(updatedDevis);

        restDevisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, devisDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(devisDTO))
            )
            .andExpect(status().isOk());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(UPDATED_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(UPDATED_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(UPDATED_DUREE_PROJET);
        assertThat(testDevis.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void putNonExistingDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDevisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, devisDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(devisDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDevisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(devisDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDevisMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(devisDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDevisWithPatch() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        int databaseSizeBeforeUpdate = devisRepository.findAll().size();

        // Update the devis using partial update
        Devis partialUpdatedDevis = new Devis();
        partialUpdatedDevis.setId(devis.getId());

        partialUpdatedDevis.prixTotal(UPDATED_PRIX_TOTAL).prixHT(UPDATED_PRIX_HT).userUuid(UPDATED_USER_UUID);

        restDevisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDevis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDevis))
            )
            .andExpect(status().isOk());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(UPDATED_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(DEFAULT_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(DEFAULT_DUREE_PROJET);
        assertThat(testDevis.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void fullUpdateDevisWithPatch() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        int databaseSizeBeforeUpdate = devisRepository.findAll().size();

        // Update the devis using partial update
        Devis partialUpdatedDevis = new Devis();
        partialUpdatedDevis.setId(devis.getId());

        partialUpdatedDevis
            .prixTotal(UPDATED_PRIX_TOTAL)
            .prixHT(UPDATED_PRIX_HT)
            .prixService(UPDATED_PRIX_SERVICE)
            .dureeProjet(UPDATED_DUREE_PROJET)
            .userUuid(UPDATED_USER_UUID);

        restDevisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDevis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDevis))
            )
            .andExpect(status().isOk());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(UPDATED_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(UPDATED_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(UPDATED_DUREE_PROJET);
        assertThat(testDevis.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void patchNonExistingDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDevisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, devisDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(devisDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDevisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(devisDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDevisMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(devisDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDevis() throws Exception {
        // Initialize the database
        devisRepository.saveAndFlush(devis);

        int databaseSizeBeforeDelete = devisRepository.findAll().size();

        // Delete the devis
        restDevisMockMvc
            .perform(delete(ENTITY_API_URL_ID, devis.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Devis> devisList = devisRepository.findAll();
        assertThat(devisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
