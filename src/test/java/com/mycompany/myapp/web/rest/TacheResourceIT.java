package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.criteria.TacheCriteria;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
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
 * Integration tests for the {@link TacheResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TacheResourceIT {

    private static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
    private static final UUID UPDATED_USER_UUID = UUID.randomUUID();

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS_TACHE = Status.ATTENTE;
    private static final Status UPDATED_STATUS_TACHE = Status.COURS;

    private static final String ENTITY_API_URL = "/api/taches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private TacheMapper tacheMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTacheMockMvc;

    private Tache tache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity(EntityManager em) {
        Tache tache = new Tache()
            .userUuid(DEFAULT_USER_UUID)
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .statusTache(DEFAULT_STATUS_TACHE);
        return tache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity(EntityManager em) {
        Tache tache = new Tache()
            .userUuid(UPDATED_USER_UUID)
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .statusTache(UPDATED_STATUS_TACHE);
        return tache;
    }

    @BeforeEach
    public void initTest() {
        tache = createEntity(em);
    }

    @Test
    @Transactional
    void createTache() throws Exception {
        int databaseSizeBeforeCreate = tacheRepository.findAll().size();
        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);
        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tacheDTO)))
            .andExpect(status().isCreated());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate + 1);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
        assertThat(testTache.getTitre()).isEqualTo(DEFAULT_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(DEFAULT_STATUS_TACHE);
    }

    @Test
    @Transactional
    void createTacheWithExistingId() throws Exception {
        // Create the Tache with an existing ID
        tache.setId(1L);
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        int databaseSizeBeforeCreate = tacheRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = tacheRepository.findAll().size();
        // set the field null
        tache.setUserUuid(null);

        // Create the Tache, which fails.
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaches() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tache.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statusTache").value(hasItem(DEFAULT_STATUS_TACHE.toString())));
    }

    @Test
    @Transactional
    void getTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get the tache
        restTacheMockMvc
            .perform(get(ENTITY_API_URL_ID, tache.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tache.getId().intValue()))
            .andExpect(jsonPath("$.userUuid").value(DEFAULT_USER_UUID.toString()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.statusTache").value(DEFAULT_STATUS_TACHE.toString()));
    }

    @Test
    @Transactional
    void getTachesByIdFiltering() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        Long id = tache.getId();

        defaultTacheShouldBeFound("id.equals=" + id);
        defaultTacheShouldNotBeFound("id.notEquals=" + id);

        defaultTacheShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTacheShouldNotBeFound("id.greaterThan=" + id);

        defaultTacheShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTacheShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTachesByUserUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where userUuid equals to DEFAULT_USER_UUID
        defaultTacheShouldBeFound("userUuid.equals=" + DEFAULT_USER_UUID);

        // Get all the tacheList where userUuid equals to UPDATED_USER_UUID
        defaultTacheShouldNotBeFound("userUuid.equals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllTachesByUserUuidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where userUuid not equals to DEFAULT_USER_UUID
        defaultTacheShouldNotBeFound("userUuid.notEquals=" + DEFAULT_USER_UUID);

        // Get all the tacheList where userUuid not equals to UPDATED_USER_UUID
        defaultTacheShouldBeFound("userUuid.notEquals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllTachesByUserUuidIsInShouldWork() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where userUuid in DEFAULT_USER_UUID or UPDATED_USER_UUID
        defaultTacheShouldBeFound("userUuid.in=" + DEFAULT_USER_UUID + "," + UPDATED_USER_UUID);

        // Get all the tacheList where userUuid equals to UPDATED_USER_UUID
        defaultTacheShouldNotBeFound("userUuid.in=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllTachesByUserUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where userUuid is not null
        defaultTacheShouldBeFound("userUuid.specified=true");

        // Get all the tacheList where userUuid is null
        defaultTacheShouldNotBeFound("userUuid.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre equals to DEFAULT_TITRE
        defaultTacheShouldBeFound("titre.equals=" + DEFAULT_TITRE);

        // Get all the tacheList where titre equals to UPDATED_TITRE
        defaultTacheShouldNotBeFound("titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre not equals to DEFAULT_TITRE
        defaultTacheShouldNotBeFound("titre.notEquals=" + DEFAULT_TITRE);

        // Get all the tacheList where titre not equals to UPDATED_TITRE
        defaultTacheShouldBeFound("titre.notEquals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre in DEFAULT_TITRE or UPDATED_TITRE
        defaultTacheShouldBeFound("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE);

        // Get all the tacheList where titre equals to UPDATED_TITRE
        defaultTacheShouldNotBeFound("titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre is not null
        defaultTacheShouldBeFound("titre.specified=true");

        // Get all the tacheList where titre is null
        defaultTacheShouldNotBeFound("titre.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByTitreContainsSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre contains DEFAULT_TITRE
        defaultTacheShouldBeFound("titre.contains=" + DEFAULT_TITRE);

        // Get all the tacheList where titre contains UPDATED_TITRE
        defaultTacheShouldNotBeFound("titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre does not contain DEFAULT_TITRE
        defaultTacheShouldNotBeFound("titre.doesNotContain=" + DEFAULT_TITRE);

        // Get all the tacheList where titre does not contain UPDATED_TITRE
        defaultTacheShouldBeFound("titre.doesNotContain=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description equals to DEFAULT_DESCRIPTION
        defaultTacheShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the tacheList where description equals to UPDATED_DESCRIPTION
        defaultTacheShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description not equals to DEFAULT_DESCRIPTION
        defaultTacheShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the tacheList where description not equals to UPDATED_DESCRIPTION
        defaultTacheShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTacheShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the tacheList where description equals to UPDATED_DESCRIPTION
        defaultTacheShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description is not null
        defaultTacheShouldBeFound("description.specified=true");

        // Get all the tacheList where description is null
        defaultTacheShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description contains DEFAULT_DESCRIPTION
        defaultTacheShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the tacheList where description contains UPDATED_DESCRIPTION
        defaultTacheShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description does not contain DEFAULT_DESCRIPTION
        defaultTacheShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the tacheList where description does not contain UPDATED_DESCRIPTION
        defaultTacheShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByStatusTacheIsEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statusTache equals to DEFAULT_STATUS_TACHE
        defaultTacheShouldBeFound("statusTache.equals=" + DEFAULT_STATUS_TACHE);

        // Get all the tacheList where statusTache equals to UPDATED_STATUS_TACHE
        defaultTacheShouldNotBeFound("statusTache.equals=" + UPDATED_STATUS_TACHE);
    }

    @Test
    @Transactional
    void getAllTachesByStatusTacheIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statusTache not equals to DEFAULT_STATUS_TACHE
        defaultTacheShouldNotBeFound("statusTache.notEquals=" + DEFAULT_STATUS_TACHE);

        // Get all the tacheList where statusTache not equals to UPDATED_STATUS_TACHE
        defaultTacheShouldBeFound("statusTache.notEquals=" + UPDATED_STATUS_TACHE);
    }

    @Test
    @Transactional
    void getAllTachesByStatusTacheIsInShouldWork() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statusTache in DEFAULT_STATUS_TACHE or UPDATED_STATUS_TACHE
        defaultTacheShouldBeFound("statusTache.in=" + DEFAULT_STATUS_TACHE + "," + UPDATED_STATUS_TACHE);

        // Get all the tacheList where statusTache equals to UPDATED_STATUS_TACHE
        defaultTacheShouldNotBeFound("statusTache.in=" + UPDATED_STATUS_TACHE);
    }

    @Test
    @Transactional
    void getAllTachesByStatusTacheIsNullOrNotNull() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statusTache is not null
        defaultTacheShouldBeFound("statusTache.specified=true");

        // Get all the tacheList where statusTache is null
        defaultTacheShouldNotBeFound("statusTache.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByProjetIsEqualToSomething() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);
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
        tache.addProjet(projet);
        tacheRepository.saveAndFlush(tache);
        Long projetId = projet.getId();

        // Get all the tacheList where projet equals to projetId
        defaultTacheShouldBeFound("projetId.equals=" + projetId);

        // Get all the tacheList where projet equals to (projetId + 1)
        defaultTacheShouldNotBeFound("projetId.equals=" + (projetId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTacheShouldBeFound(String filter) throws Exception {
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tache.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statusTache").value(hasItem(DEFAULT_STATUS_TACHE.toString())));

        // Check, that the count call also returns 1
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTacheShouldNotBeFound(String filter) throws Exception {
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTache() throws Exception {
        // Get the tache
        restTacheMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).get();
        // Disconnect from session so that the updates on updatedTache are not directly saved in db
        em.detach(updatedTache);
        updatedTache.userUuid(UPDATED_USER_UUID).titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).statusTache(UPDATED_STATUS_TACHE);
        TacheDTO tacheDTO = tacheMapper.toDto(updatedTache);

        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tacheDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tacheDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testTache.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(UPDATED_STATUS_TACHE);
    }

    @Test
    @Transactional
    void putNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tacheDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tacheDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache.userUuid(UPDATED_USER_UUID).titre(UPDATED_TITRE);

        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testTache.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(DEFAULT_STATUS_TACHE);
    }

    @Test
    @Transactional
    void fullUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache
            .userUuid(UPDATED_USER_UUID)
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .statusTache(UPDATED_STATUS_TACHE);

        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testTache.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(UPDATED_STATUS_TACHE);
    }

    @Test
    @Transactional
    void patchNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tacheDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tacheDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTache() throws Exception {
        // Initialize the database
        tacheRepository.saveAndFlush(tache);

        int databaseSizeBeforeDelete = tacheRepository.findAll().size();

        // Delete the tache
        restTacheMockMvc
            .perform(delete(ENTITY_API_URL_ID, tache.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tache> tacheList = tacheRepository.findAll();
        assertThat(tacheList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
