package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.domain.enumeration.Rendement;
import com.mycompany.myapp.repository.VoteRepository;
import com.mycompany.myapp.service.criteria.VoteCriteria;
import com.mycompany.myapp.service.dto.VoteDTO;
import com.mycompany.myapp.service.mapper.VoteMapper;
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
 * Integration tests for the {@link VoteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VoteResourceIT {

    private static final UUID DEFAULT_USER_UUID = UUID.randomUUID();
    private static final UUID UPDATED_USER_UUID = UUID.randomUUID();

    private static final Rendement DEFAULT_TYPE_VOTE = Rendement.FAIBLE;
    private static final Rendement UPDATED_TYPE_VOTE = Rendement.MOYEN;

    private static final String ENTITY_API_URL = "/api/votes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVoteMockMvc;

    private Vote vote;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vote createEntity(EntityManager em) {
        Vote vote = new Vote().userUuid(DEFAULT_USER_UUID).typeVote(DEFAULT_TYPE_VOTE);
        return vote;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vote createUpdatedEntity(EntityManager em) {
        Vote vote = new Vote().userUuid(UPDATED_USER_UUID).typeVote(UPDATED_TYPE_VOTE);
        return vote;
    }

    @BeforeEach
    public void initTest() {
        vote = createEntity(em);
    }

    @Test
    @Transactional
    void createVote() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();
        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);
        restVoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(voteDTO)))
            .andExpect(status().isCreated());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate + 1);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getUserUuid()).isEqualTo(DEFAULT_USER_UUID);
        assertThat(testVote.getTypeVote()).isEqualTo(DEFAULT_TYPE_VOTE);
    }

    @Test
    @Transactional
    void createVoteWithExistingId() throws Exception {
        // Create the Vote with an existing ID
        vote.setId(1L);
        VoteDTO voteDTO = voteMapper.toDto(vote);

        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(voteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserUuidIsRequired() throws Exception {
        int databaseSizeBeforeTest = voteRepository.findAll().size();
        // set the field null
        vote.setUserUuid(null);

        // Create the Vote, which fails.
        VoteDTO voteDTO = voteMapper.toDto(vote);

        restVoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(voteDTO)))
            .andExpect(status().isBadRequest());

        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeVoteIsRequired() throws Exception {
        int databaseSizeBeforeTest = voteRepository.findAll().size();
        // set the field null
        vote.setTypeVote(null);

        // Create the Vote, which fails.
        VoteDTO voteDTO = voteMapper.toDto(vote);

        restVoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(voteDTO)))
            .andExpect(status().isBadRequest());

        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVotes() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList
        restVoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].typeVote").value(hasItem(DEFAULT_TYPE_VOTE.toString())));
    }

    @Test
    @Transactional
    void getVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get the vote
        restVoteMockMvc
            .perform(get(ENTITY_API_URL_ID, vote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vote.getId().intValue()))
            .andExpect(jsonPath("$.userUuid").value(DEFAULT_USER_UUID.toString()))
            .andExpect(jsonPath("$.typeVote").value(DEFAULT_TYPE_VOTE.toString()));
    }

    @Test
    @Transactional
    void getVotesByIdFiltering() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        Long id = vote.getId();

        defaultVoteShouldBeFound("id.equals=" + id);
        defaultVoteShouldNotBeFound("id.notEquals=" + id);

        defaultVoteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultVoteShouldNotBeFound("id.greaterThan=" + id);

        defaultVoteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultVoteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVotesByUserUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where userUuid equals to DEFAULT_USER_UUID
        defaultVoteShouldBeFound("userUuid.equals=" + DEFAULT_USER_UUID);

        // Get all the voteList where userUuid equals to UPDATED_USER_UUID
        defaultVoteShouldNotBeFound("userUuid.equals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllVotesByUserUuidIsNotEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where userUuid not equals to DEFAULT_USER_UUID
        defaultVoteShouldNotBeFound("userUuid.notEquals=" + DEFAULT_USER_UUID);

        // Get all the voteList where userUuid not equals to UPDATED_USER_UUID
        defaultVoteShouldBeFound("userUuid.notEquals=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllVotesByUserUuidIsInShouldWork() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where userUuid in DEFAULT_USER_UUID or UPDATED_USER_UUID
        defaultVoteShouldBeFound("userUuid.in=" + DEFAULT_USER_UUID + "," + UPDATED_USER_UUID);

        // Get all the voteList where userUuid equals to UPDATED_USER_UUID
        defaultVoteShouldNotBeFound("userUuid.in=" + UPDATED_USER_UUID);
    }

    @Test
    @Transactional
    void getAllVotesByUserUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where userUuid is not null
        defaultVoteShouldBeFound("userUuid.specified=true");

        // Get all the voteList where userUuid is null
        defaultVoteShouldNotBeFound("userUuid.specified=false");
    }

    @Test
    @Transactional
    void getAllVotesByTypeVoteIsEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where typeVote equals to DEFAULT_TYPE_VOTE
        defaultVoteShouldBeFound("typeVote.equals=" + DEFAULT_TYPE_VOTE);

        // Get all the voteList where typeVote equals to UPDATED_TYPE_VOTE
        defaultVoteShouldNotBeFound("typeVote.equals=" + UPDATED_TYPE_VOTE);
    }

    @Test
    @Transactional
    void getAllVotesByTypeVoteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where typeVote not equals to DEFAULT_TYPE_VOTE
        defaultVoteShouldNotBeFound("typeVote.notEquals=" + DEFAULT_TYPE_VOTE);

        // Get all the voteList where typeVote not equals to UPDATED_TYPE_VOTE
        defaultVoteShouldBeFound("typeVote.notEquals=" + UPDATED_TYPE_VOTE);
    }

    @Test
    @Transactional
    void getAllVotesByTypeVoteIsInShouldWork() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where typeVote in DEFAULT_TYPE_VOTE or UPDATED_TYPE_VOTE
        defaultVoteShouldBeFound("typeVote.in=" + DEFAULT_TYPE_VOTE + "," + UPDATED_TYPE_VOTE);

        // Get all the voteList where typeVote equals to UPDATED_TYPE_VOTE
        defaultVoteShouldNotBeFound("typeVote.in=" + UPDATED_TYPE_VOTE);
    }

    @Test
    @Transactional
    void getAllVotesByTypeVoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where typeVote is not null
        defaultVoteShouldBeFound("typeVote.specified=true");

        // Get all the voteList where typeVote is null
        defaultVoteShouldNotBeFound("typeVote.specified=false");
    }

    @Test
    @Transactional
    void getAllVotesByEquipeIsEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);
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
        vote.addEquipe(equipe);
        voteRepository.saveAndFlush(vote);
        Long equipeId = equipe.getId();

        // Get all the voteList where equipe equals to equipeId
        defaultVoteShouldBeFound("equipeId.equals=" + equipeId);

        // Get all the voteList where equipe equals to (equipeId + 1)
        defaultVoteShouldNotBeFound("equipeId.equals=" + (equipeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVoteShouldBeFound(String filter) throws Exception {
        restVoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
            .andExpect(jsonPath("$.[*].userUuid").value(hasItem(DEFAULT_USER_UUID.toString())))
            .andExpect(jsonPath("$.[*].typeVote").value(hasItem(DEFAULT_TYPE_VOTE.toString())));

        // Check, that the count call also returns 1
        restVoteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVoteShouldNotBeFound(String filter) throws Exception {
        restVoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVoteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVote() throws Exception {
        // Get the vote
        restVoteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote
        Vote updatedVote = voteRepository.findById(vote.getId()).get();
        // Disconnect from session so that the updates on updatedVote are not directly saved in db
        em.detach(updatedVote);
        updatedVote.userUuid(UPDATED_USER_UUID).typeVote(UPDATED_TYPE_VOTE);
        VoteDTO voteDTO = voteMapper.toDto(updatedVote);

        restVoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(voteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testVote.getTypeVote()).isEqualTo(UPDATED_TYPE_VOTE);
    }

    @Test
    @Transactional
    void putNonExistingVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, voteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(voteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(voteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(voteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVoteWithPatch() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote using partial update
        Vote partialUpdatedVote = new Vote();
        partialUpdatedVote.setId(vote.getId());

        partialUpdatedVote.userUuid(UPDATED_USER_UUID).typeVote(UPDATED_TYPE_VOTE);

        restVoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVote.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVote))
            )
            .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testVote.getTypeVote()).isEqualTo(UPDATED_TYPE_VOTE);
    }

    @Test
    @Transactional
    void fullUpdateVoteWithPatch() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote using partial update
        Vote partialUpdatedVote = new Vote();
        partialUpdatedVote.setId(vote.getId());

        partialUpdatedVote.userUuid(UPDATED_USER_UUID).typeVote(UPDATED_TYPE_VOTE);

        restVoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVote.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVote))
            )
            .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getUserUuid()).isEqualTo(UPDATED_USER_UUID);
        assertThat(testVote.getTypeVote()).isEqualTo(UPDATED_TYPE_VOTE);
    }

    @Test
    @Transactional
    void patchNonExistingVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, voteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(voteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(voteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVoteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(voteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        int databaseSizeBeforeDelete = voteRepository.findAll().size();

        // Delete the vote
        restVoteMockMvc
            .perform(delete(ENTITY_API_URL_ID, vote.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
