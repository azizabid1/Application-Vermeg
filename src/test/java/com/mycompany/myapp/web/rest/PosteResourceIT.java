package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.PosteRepository;
import com.mycompany.myapp.service.PosteService;
import com.mycompany.myapp.service.criteria.PosteCriteria;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.mapper.PosteMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
 * Integration tests for the {@link PosteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PosteResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/postes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PosteRepository posteRepository;

    @Mock
    private PosteRepository posteRepositoryMock;

    @Autowired
    private PosteMapper posteMapper;

    @Mock
    private PosteService posteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPosteMockMvc;

    private Poste poste;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poste createEntity(EntityManager em) {
        Poste poste = new Poste().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return poste;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poste createUpdatedEntity(EntityManager em) {
        Poste poste = new Poste().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return poste;
    }

    @BeforeEach
    public void initTest() {
        poste = createEntity(em);
    }

    @Test
    @Transactional
    void createPoste() throws Exception {
        int databaseSizeBeforeCreate = posteRepository.findAll().size();
        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);
        restPosteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(posteDTO)))
            .andExpect(status().isCreated());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeCreate + 1);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createPosteWithExistingId() throws Exception {
        // Create the Poste with an existing ID
        poste.setId(1L);
        PosteDTO posteDTO = posteMapper.toDto(poste);

        int databaseSizeBeforeCreate = posteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPosteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(posteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPostes() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poste.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostesWithEagerRelationshipsIsEnabled() throws Exception {
        when(posteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPosteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(posteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPostesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(posteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPosteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(posteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPoste() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get the poste
        restPosteMockMvc
            .perform(get(ENTITY_API_URL_ID, poste.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(poste.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getPostesByIdFiltering() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        Long id = poste.getId();

        defaultPosteShouldBeFound("id.equals=" + id);
        defaultPosteShouldNotBeFound("id.notEquals=" + id);

        defaultPosteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPosteShouldNotBeFound("id.greaterThan=" + id);

        defaultPosteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPosteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPostesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where title equals to DEFAULT_TITLE
        defaultPosteShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the posteList where title equals to UPDATED_TITLE
        defaultPosteShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPostesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where title not equals to DEFAULT_TITLE
        defaultPosteShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the posteList where title not equals to UPDATED_TITLE
        defaultPosteShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPostesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultPosteShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the posteList where title equals to UPDATED_TITLE
        defaultPosteShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPostesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where title is not null
        defaultPosteShouldBeFound("title.specified=true");

        // Get all the posteList where title is null
        defaultPosteShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllPostesByTitleContainsSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where title contains DEFAULT_TITLE
        defaultPosteShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the posteList where title contains UPDATED_TITLE
        defaultPosteShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPostesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where title does not contain DEFAULT_TITLE
        defaultPosteShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the posteList where title does not contain UPDATED_TITLE
        defaultPosteShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPostesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where description equals to DEFAULT_DESCRIPTION
        defaultPosteShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the posteList where description equals to UPDATED_DESCRIPTION
        defaultPosteShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPostesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where description not equals to DEFAULT_DESCRIPTION
        defaultPosteShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the posteList where description not equals to UPDATED_DESCRIPTION
        defaultPosteShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPostesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPosteShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the posteList where description equals to UPDATED_DESCRIPTION
        defaultPosteShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPostesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where description is not null
        defaultPosteShouldBeFound("description.specified=true");

        // Get all the posteList where description is null
        defaultPosteShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllPostesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where description contains DEFAULT_DESCRIPTION
        defaultPosteShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the posteList where description contains UPDATED_DESCRIPTION
        defaultPosteShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPostesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        // Get all the posteList where description does not contain DEFAULT_DESCRIPTION
        defaultPosteShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the posteList where description does not contain UPDATED_DESCRIPTION
        defaultPosteShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPostesByUsersIsEqualToSomething() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);
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
        poste.addUsers(users);
        posteRepository.saveAndFlush(poste);
        Long usersId = users.getId();

        // Get all the posteList where users equals to usersId
        defaultPosteShouldBeFound("usersId.equals=" + usersId);

        // Get all the posteList where users equals to (usersId + 1)
        defaultPosteShouldNotBeFound("usersId.equals=" + (usersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPosteShouldBeFound(String filter) throws Exception {
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poste.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPosteShouldNotBeFound(String filter) throws Exception {
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPosteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPoste() throws Exception {
        // Get the poste
        restPosteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPoste() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        int databaseSizeBeforeUpdate = posteRepository.findAll().size();

        // Update the poste
        Poste updatedPoste = posteRepository.findById(poste.getId()).get();
        // Disconnect from session so that the updates on updatedPoste are not directly saved in db
        em.detach(updatedPoste);
        updatedPoste.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        PosteDTO posteDTO = posteMapper.toDto(updatedPoste);

        restPosteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, posteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(posteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, posteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(posteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePosteWithPatch() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        int databaseSizeBeforeUpdate = posteRepository.findAll().size();

        // Update the poste using partial update
        Poste partialUpdatedPoste = new Poste();
        partialUpdatedPoste.setId(poste.getId());

        partialUpdatedPoste.title(UPDATED_TITLE);

        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPoste))
            )
            .andExpect(status().isOk());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdatePosteWithPatch() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        int databaseSizeBeforeUpdate = posteRepository.findAll().size();

        // Update the poste using partial update
        Poste partialUpdatedPoste = new Poste();
        partialUpdatedPoste.setId(poste.getId());

        partialUpdatedPoste.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPoste))
            )
            .andExpect(status().isOk());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, posteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(posteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPosteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(posteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoste() throws Exception {
        // Initialize the database
        posteRepository.saveAndFlush(poste);

        int databaseSizeBeforeDelete = posteRepository.findAll().size();

        // Delete the poste
        restPosteMockMvc
            .perform(delete(ENTITY_API_URL_ID, poste.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Poste> posteList = posteRepository.findAll();
        assertThat(posteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
