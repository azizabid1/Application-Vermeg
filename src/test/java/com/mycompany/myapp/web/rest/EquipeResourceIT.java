package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.repository.EquipeRepository;
import com.mycompany.myapp.service.EquipeService;
import com.mycompany.myapp.service.criteria.EquipeCriteria;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.mapper.EquipeMapper;
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
 * Integration tests for the {@link EquipeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EquipeResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Long DEFAULT_NOMBRE_PERSONNE = 4L;
    private static final Long UPDATED_NOMBRE_PERSONNE = 5L;
    private static final Long SMALLER_NOMBRE_PERSONNE = 4L - 1L;

    private static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
    private static final UUID UPDATED_USER_UUID = UUID.randomUUID();

    private static final String ENTITY_API_URL = "/api/equipes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EquipeRepository equipeRepository;

    @Mock
    private EquipeRepository equipeRepositoryMock;

    @Autowired
    private EquipeMapper equipeMapper;

    @Mock
    private EquipeService equipeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEquipeMockMvc;

    private Equipe equipe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipe createEntity(EntityManager em) {
        Equipe equipe = new Equipe().nom(DEFAULT_NOM).nombrePersonne(DEFAULT_NOMBRE_PERSONNE).userUuid(DEFAULT_USER_UUID);
        return equipe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipe createUpdatedEntity(EntityManager em) {
        Equipe equipe = new Equipe().nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE).userUuid(UPDATED_USER_UUID);
        return equipe;
    }

    @BeforeEach
    public void initTest() {
        equipe = createEntity(em);
    }

    @Test
    @Transactional
    void createEquipe() throws Exception {
        int databaseSizeBeforeCreate = equipeRepository.findAll().size();
        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);
        restEquipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
            .andExpect(status().isCreated());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeCreate + 1);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(DEFAULT_NOMBRE_PERSONNE);
        assertThat(testEquipe.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
    }

    @Test
    @Transactional
    void createEquipeWithExistingId() throws Exception {
        // Create the Equipe with an existing ID
        equipe.setId(1L);
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        int databaseSizeBeforeCreate = equipeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEquipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = equipeRepository.findAll().size();
        // set the field null
        equipe.setUserUuid(null);

        // Create the Equipe, which fails.
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        restEquipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
            .andExpect(status().isBadRequest());

        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEquipes() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList
        restEquipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].nombrePersonne").value(hasItem(DEFAULT_NOMBRE_PERSONNE.intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipesWithEagerRelationshipsIsEnabled() throws Exception {
        when(equipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(equipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(equipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(equipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getEquipe() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get the equipe
        restEquipeMockMvc
            .perform(get(ENTITY_API_URL_ID, equipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(equipe.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.nombrePersonne").value(DEFAULT_NOMBRE_PERSONNE.intValue()))
            .andExpect(jsonPath("$.userUuid").value(DEFAULT_USER_UUID.toString()));
    }

    @Test
    @Transactional
    void getEquipesByIdFiltering() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        Long id = equipe.getId();

        defaultEquipeShouldBeFound("id.equals=" + id);
        defaultEquipeShouldNotBeFound("id.notEquals=" + id);

        defaultEquipeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEquipeShouldNotBeFound("id.greaterThan=" + id);

        defaultEquipeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEquipeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEquipesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nom equals to DEFAULT_NOM
        defaultEquipeShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the equipeList where nom equals to UPDATED_NOM
        defaultEquipeShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEquipesByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nom not equals to DEFAULT_NOM
        defaultEquipeShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the equipeList where nom not equals to UPDATED_NOM
        defaultEquipeShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEquipesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultEquipeShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the equipeList where nom equals to UPDATED_NOM
        defaultEquipeShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEquipesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nom is not null
        defaultEquipeShouldBeFound("nom.specified=true");

        // Get all the equipeList where nom is null
        defaultEquipeShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipesByNomContainsSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nom contains DEFAULT_NOM
        defaultEquipeShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the equipeList where nom contains UPDATED_NOM
        defaultEquipeShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEquipesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nom does not contain DEFAULT_NOM
        defaultEquipeShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the equipeList where nom does not contain UPDATED_NOM
        defaultEquipeShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne equals to DEFAULT_NOMBRE_PERSONNE
        defaultEquipeShouldBeFound("nombrePersonne.equals=" + DEFAULT_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne equals to UPDATED_NOMBRE_PERSONNE
        defaultEquipeShouldNotBeFound("nombrePersonne.equals=" + UPDATED_NOMBRE_PERSONNE);
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne not equals to DEFAULT_NOMBRE_PERSONNE
        defaultEquipeShouldNotBeFound("nombrePersonne.notEquals=" + DEFAULT_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne not equals to UPDATED_NOMBRE_PERSONNE
        defaultEquipeShouldBeFound("nombrePersonne.notEquals=" + UPDATED_NOMBRE_PERSONNE);
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsInShouldWork() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne in DEFAULT_NOMBRE_PERSONNE or UPDATED_NOMBRE_PERSONNE
        defaultEquipeShouldBeFound("nombrePersonne.in=" + DEFAULT_NOMBRE_PERSONNE + "," + UPDATED_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne equals to UPDATED_NOMBRE_PERSONNE
        defaultEquipeShouldNotBeFound("nombrePersonne.in=" + UPDATED_NOMBRE_PERSONNE);
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne is not null
        defaultEquipeShouldBeFound("nombrePersonne.specified=true");

        // Get all the equipeList where nombrePersonne is null
        defaultEquipeShouldNotBeFound("nombrePersonne.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne is greater than or equal to DEFAULT_NOMBRE_PERSONNE
        defaultEquipeShouldBeFound("nombrePersonne.greaterThanOrEqual=" + DEFAULT_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne is greater than or equal to (DEFAULT_NOMBRE_PERSONNE + 1)
        defaultEquipeShouldNotBeFound("nombrePersonne.greaterThanOrEqual=" + (DEFAULT_NOMBRE_PERSONNE + 1));
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne is less than or equal to DEFAULT_NOMBRE_PERSONNE
        defaultEquipeShouldBeFound("nombrePersonne.lessThanOrEqual=" + DEFAULT_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne is less than or equal to SMALLER_NOMBRE_PERSONNE
        defaultEquipeShouldNotBeFound("nombrePersonne.lessThanOrEqual=" + SMALLER_NOMBRE_PERSONNE);
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsLessThanSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne is less than DEFAULT_NOMBRE_PERSONNE
        defaultEquipeShouldNotBeFound("nombrePersonne.lessThan=" + DEFAULT_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne is less than (DEFAULT_NOMBRE_PERSONNE + 1)
        defaultEquipeShouldBeFound("nombrePersonne.lessThan=" + (DEFAULT_NOMBRE_PERSONNE + 1));
    }

    @Test
    @Transactional
    void getAllEquipesByNombrePersonneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where nombrePersonne is greater than DEFAULT_NOMBRE_PERSONNE
        defaultEquipeShouldNotBeFound("nombrePersonne.greaterThan=" + DEFAULT_NOMBRE_PERSONNE);

        // Get all the equipeList where nombrePersonne is greater than SMALLER_NOMBRE_PERSONNE
        defaultEquipeShouldBeFound("nombrePersonne.greaterThan=" + SMALLER_NOMBRE_PERSONNE);
    }

    @Test
    @Transactional
    void getAllEquipesByUserUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where userUuid equals to DEFAULT_USER_UUID
        defaultEquipeShouldBeFound("userUuid.equals=" + DEFAULT_USER_UUID);

        // Get all the equipeList where userUuid equals to UPDATED_USER_UUID
        defaultEquipeShouldNotBeFound("userUuid.equals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllEquipesByUserUuidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where userUuid not equals to DEFAULT_USER_UUID
        defaultEquipeShouldNotBeFound("userUuid.notEquals=" + DEFAULT_USER_UUID);

        // Get all the equipeList where userUuid not equals to UPDATED_USER_UUID
        defaultEquipeShouldBeFound("userUuid.notEquals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllEquipesByUserUuidIsInShouldWork() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where userUuid in DEFAULT_USER_UUID or UPDATED_USER_UUID
        defaultEquipeShouldBeFound("userUuid.in=" + DEFAULT_USER_UUID + "," + UPDATED_USER_UUID);

        // Get all the equipeList where userUuid equals to UPDATED_USER_UUID
        defaultEquipeShouldNotBeFound("userUuid.in=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllEquipesByUserUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        // Get all the equipeList where userUuid is not null
        defaultEquipeShouldBeFound("userUuid.specified=true");

        // Get all the equipeList where userUuid is null
        defaultEquipeShouldNotBeFound("userUuid.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipesByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);
        User userId;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            userId = UserResourceIT.createEntity(em);
            em.persist(userId);
            em.flush();
        } else {
            userId = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(userId);
        em.flush();
        equipe.setUserId(userId);
        equipeRepository.saveAndFlush(equipe);
        Long userIdId = userId.getId();

        // Get all the equipeList where userId equals to userIdId
        defaultEquipeShouldBeFound("userIdId.equals=" + userIdId);

        // Get all the equipeList where userId equals to (userIdId + 1)
        defaultEquipeShouldNotBeFound("userIdId.equals=" + (userIdId + 1));
    }

    @Test
    @Transactional
    void getAllEquipesByVoteIsEqualToSomething() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);
        Vote vote;
        if (TestUtil.findAll(em, Vote.class).isEmpty()) {
            vote = VoteResourceIT.createEntity(em);
            em.persist(vote);
            em.flush();
        } else {
            vote = TestUtil.findAll(em, Vote.class).get(0);
        }
        em.persist(vote);
        em.flush();
        equipe.addVote(vote);
        equipeRepository.saveAndFlush(equipe);
        Long voteId = vote.getId();

        // Get all the equipeList where vote equals to voteId
        defaultEquipeShouldBeFound("voteId.equals=" + voteId);

        // Get all the equipeList where vote equals to (voteId + 1)
        defaultEquipeShouldNotBeFound("voteId.equals=" + (voteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEquipeShouldBeFound(String filter) throws Exception {
        restEquipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].nombrePersonne").value(hasItem(DEFAULT_NOMBRE_PERSONNE.intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())));

        // Check, that the count call also returns 1
        restEquipeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEquipeShouldNotBeFound(String filter) throws Exception {
        restEquipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEquipeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEquipe() throws Exception {
        // Get the equipe
        restEquipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEquipe() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();

        // Update the equipe
        Equipe updatedEquipe = equipeRepository.findById(equipe.getId()).get();
        // Disconnect from session so that the updates on updatedEquipe are not directly saved in db
        em.detach(updatedEquipe);
        updatedEquipe.nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE).userUuid(UPDATED_USER_UUID);
        EquipeDTO equipeDTO = equipeMapper.toDto(updatedEquipe);

        restEquipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(UPDATED_NOMBRE_PERSONNE);
        assertThat(testEquipe.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void putNonExistingEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(equipeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEquipeWithPatch() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();

        // Update the equipe using partial update
        Equipe partialUpdatedEquipe = new Equipe();
        partialUpdatedEquipe.setId(equipe.getId());

        partialUpdatedEquipe.nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE);

        restEquipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipe))
            )
            .andExpect(status().isOk());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(UPDATED_NOMBRE_PERSONNE);
        assertThat(testEquipe.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
    }

    @Test
    @Transactional
    void fullUpdateEquipeWithPatch() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();

        // Update the equipe using partial update
        Equipe partialUpdatedEquipe = new Equipe();
        partialUpdatedEquipe.setId(equipe.getId());

        partialUpdatedEquipe.nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE).userUuid(UPDATED_USER_UUID);

        restEquipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipe))
            )
            .andExpect(status().isOk());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(UPDATED_NOMBRE_PERSONNE);
        assertThat(testEquipe.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void patchNonExistingEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, equipeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(equipeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(equipeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEquipe() throws Exception {
        // Initialize the database
        equipeRepository.saveAndFlush(equipe);

        int databaseSizeBeforeDelete = equipeRepository.findAll().size();

        // Delete the equipe
        restEquipeMockMvc
            .perform(delete(ENTITY_API_URL_ID, equipe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Equipe> equipeList = equipeRepository.findAll();
        assertThat(equipeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
