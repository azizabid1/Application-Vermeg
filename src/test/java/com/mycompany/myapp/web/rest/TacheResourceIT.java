package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
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
 * Integration tests for the {@link TacheResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TacheResourceIT {

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
    private WebTestClient webTestClient;

    private Tache tache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity(EntityManager em) {
        Tache tache = new Tache().titre(DEFAULT_TITRE).description(DEFAULT_DESCRIPTION).statusTache(DEFAULT_STATUS_TACHE);
        return tache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity(EntityManager em) {
        Tache tache = new Tache().titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).statusTache(UPDATED_STATUS_TACHE);
        return tache;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tache.class).block();
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
        tache = createEntity(em);
    }

    @Test
    void createTache() throws Exception {
        int databaseSizeBeforeCreate = tacheRepository.findAll().collectList().block().size();
        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate + 1);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getTitre()).isEqualTo(DEFAULT_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(DEFAULT_STATUS_TACHE);
    }

    @Test
    void createTacheWithExistingId() throws Exception {
        // Create the Tache with an existing ID
        tache.setId(1L);
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        int databaseSizeBeforeCreate = tacheRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTaches() {
        // Initialize the database
        tacheRepository.save(tache).block();

        // Get all the tacheList
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
            .value(hasItem(tache.getId().intValue()))
            .jsonPath("$.[*].titre")
            .value(hasItem(DEFAULT_TITRE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].statusTache")
            .value(hasItem(DEFAULT_STATUS_TACHE.toString()));
    }

    @Test
    void getTache() {
        // Initialize the database
        tacheRepository.save(tache).block();

        // Get the tache
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tache.getId().intValue()))
            .jsonPath("$.titre")
            .value(is(DEFAULT_TITRE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.statusTache")
            .value(is(DEFAULT_STATUS_TACHE.toString()));
    }

    @Test
    void getNonExistingTache() {
        // Get the tache
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTache() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).block();
        updatedTache.titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).statusTache(UPDATED_STATUS_TACHE);
        TacheDTO tacheDTO = tacheMapper.toDto(updatedTache);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tacheDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(UPDATED_STATUS_TACHE);
    }

    @Test
    void putNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tacheDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache.titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(DEFAULT_STATUS_TACHE);
    }

    @Test
    void fullUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache.titre(UPDATED_TITRE).description(UPDATED_DESCRIPTION).statusTache(UPDATED_STATUS_TACHE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTache.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTache))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
        Tache testTache = tacheList.get(tacheList.size() - 1);
        assertThat(testTache.getTitre()).isEqualTo(UPDATED_TITRE);
        assertThat(testTache.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTache.getStatusTache()).isEqualTo(UPDATED_STATUS_TACHE);
    }

    @Test
    void patchNonExistingTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tacheDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTache() throws Exception {
        int databaseSizeBeforeUpdate = tacheRepository.findAll().collectList().block().size();
        tache.setId(count.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tacheDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tache in the database
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTache() {
        // Initialize the database
        tacheRepository.save(tache).block();

        int databaseSizeBeforeDelete = tacheRepository.findAll().collectList().block().size();

        // Delete the tache
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tache.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tache> tacheList = tacheRepository.findAll().collectList().block();
        assertThat(tacheList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
