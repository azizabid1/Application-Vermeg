package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.domain.enumeration.Rendement;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.VoteRepository;
import com.mycompany.myapp.service.dto.VoteDTO;
import com.mycompany.myapp.service.mapper.VoteMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link VoteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VoteResourceIT {

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
    private WebTestClient webTestClient;

    private Vote vote;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vote createEntity(EntityManager em) {
        Vote vote = new Vote().typeVote(DEFAULT_TYPE_VOTE);
        return vote;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vote createUpdatedEntity(EntityManager em) {
        Vote vote = new Vote().typeVote(UPDATED_TYPE_VOTE);
        return vote;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Vote.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        vote = createEntity(em);
    }

    @Test
    void createVote() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().collectList().block().size();
        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate + 1);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getTypeVote()).isEqualTo(DEFAULT_TYPE_VOTE);
    }

    @Test
    void createVoteWithExistingId() throws Exception {
        // Create the Vote with an existing ID
        vote.setId(1L);
        VoteDTO voteDTO = voteMapper.toDto(vote);

        int databaseSizeBeforeCreate = voteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllVotes() {
        // Initialize the database
        voteRepository.save(vote).block();

        // Get all the voteList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(vote.getId().intValue()))
            .jsonPath("$.[*].typeVote")
            .value(hasItem(DEFAULT_TYPE_VOTE.toString()));
    }

    @Test
    void getVote() {
        // Initialize the database
        voteRepository.save(vote).block();

        // Get the vote
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, vote.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(vote.getId().intValue()))
            .jsonPath("$.typeVote")
            .value(is(DEFAULT_TYPE_VOTE.toString()));
    }

    @Test
    void getNonExistingVote() {
        // Get the vote
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewVote() throws Exception {
        // Initialize the database
        voteRepository.save(vote).block();

        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();

        // Update the vote
        Vote updatedVote = voteRepository.findById(vote.getId()).block();
        updatedVote.typeVote(UPDATED_TYPE_VOTE);
        VoteDTO voteDTO = voteMapper.toDto(updatedVote);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, voteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getTypeVote()).isEqualTo(UPDATED_TYPE_VOTE);
    }

    @Test
    void putNonExistingVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, voteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVoteWithPatch() throws Exception {
        // Initialize the database
        voteRepository.save(vote).block();

        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();

        // Update the vote using partial update
        Vote partialUpdatedVote = new Vote();
        partialUpdatedVote.setId(vote.getId());

        partialUpdatedVote.typeVote(UPDATED_TYPE_VOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVote.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVote))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getTypeVote()).isEqualTo(UPDATED_TYPE_VOTE);
    }

    @Test
    void fullUpdateVoteWithPatch() throws Exception {
        // Initialize the database
        voteRepository.save(vote).block();

        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();

        // Update the vote using partial update
        Vote partialUpdatedVote = new Vote();
        partialUpdatedVote.setId(vote.getId());

        partialUpdatedVote.typeVote(UPDATED_TYPE_VOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVote.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVote))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getTypeVote()).isEqualTo(UPDATED_TYPE_VOTE);
    }

    @Test
    void patchNonExistingVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, voteDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().collectList().block().size();
        vote.setId(count.incrementAndGet());

        // Create the Vote
        VoteDTO voteDTO = voteMapper.toDto(vote);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(voteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVote() {
        // Initialize the database
        voteRepository.save(vote).block();

        int databaseSizeBeforeDelete = voteRepository.findAll().collectList().block().size();

        // Delete the vote
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, vote.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Vote> voteList = voteRepository.findAll().collectList().block();
        assertThat(voteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
