package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.StatusEmployeRepository;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.mapper.StatusEmployeMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link StatusEmployeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StatusEmployeResourceIT {

    private static final Boolean DEFAULT_DISPONIBILITE = false;
    private static final Boolean UPDATED_DISPONIBILITE = true;

    private static final Boolean DEFAULT_MISSION = false;
    private static final Boolean UPDATED_MISSION = true;

    private static final LocalDate DEFAULT_DEBUT_CONGE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEBUT_CONGE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FIN_CONGE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FIN_CONGE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/status-employes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatusEmployeRepository statusEmployeRepository;

    @Autowired
    private StatusEmployeMapper statusEmployeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private StatusEmploye statusEmploye;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StatusEmploye createEntity(EntityManager em) {
        StatusEmploye statusEmploye = new StatusEmploye()
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
            .disponibilite(UPDATED_DISPONIBILITE)
            .mission(UPDATED_MISSION)
            .debutConge(UPDATED_DEBUT_CONGE)
            .finConge(UPDATED_FIN_CONGE);
        return statusEmploye;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(StatusEmploye.class).block();
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
        statusEmploye = createEntity(em);
    }

    @Test
    void createStatusEmploye() throws Exception {
        int databaseSizeBeforeCreate = statusEmployeRepository.findAll().collectList().block().size();
        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeCreate + 1);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(DEFAULT_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(DEFAULT_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(DEFAULT_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(DEFAULT_FIN_CONGE);
    }

    @Test
    void createStatusEmployeWithExistingId() throws Exception {
        // Create the StatusEmploye with an existing ID
        statusEmploye.setId(1L);
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        int databaseSizeBeforeCreate = statusEmployeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllStatusEmployes() {
        // Initialize the database
        statusEmployeRepository.save(statusEmploye).block();

        // Get all the statusEmployeList
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
            .value(hasItem(statusEmploye.getId().intValue()))
            .jsonPath("$.[*].disponibilite")
            .value(hasItem(DEFAULT_DISPONIBILITE.booleanValue()))
            .jsonPath("$.[*].mission")
            .value(hasItem(DEFAULT_MISSION.booleanValue()))
            .jsonPath("$.[*].debutConge")
            .value(hasItem(DEFAULT_DEBUT_CONGE.toString()))
            .jsonPath("$.[*].finConge")
            .value(hasItem(DEFAULT_FIN_CONGE.toString()));
    }

    @Test
    void getStatusEmploye() {
        // Initialize the database
        statusEmployeRepository.save(statusEmploye).block();

        // Get the statusEmploye
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, statusEmploye.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(statusEmploye.getId().intValue()))
            .jsonPath("$.disponibilite")
            .value(is(DEFAULT_DISPONIBILITE.booleanValue()))
            .jsonPath("$.mission")
            .value(is(DEFAULT_MISSION.booleanValue()))
            .jsonPath("$.debutConge")
            .value(is(DEFAULT_DEBUT_CONGE.toString()))
            .jsonPath("$.finConge")
            .value(is(DEFAULT_FIN_CONGE.toString()));
    }

    @Test
    void getNonExistingStatusEmploye() {
        // Get the statusEmploye
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewStatusEmploye() throws Exception {
        // Initialize the database
        statusEmployeRepository.save(statusEmploye).block();

        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();

        // Update the statusEmploye
        StatusEmploye updatedStatusEmploye = statusEmployeRepository.findById(statusEmploye.getId()).block();
        updatedStatusEmploye
            .disponibilite(UPDATED_DISPONIBILITE)
            .mission(UPDATED_MISSION)
            .debutConge(UPDATED_DEBUT_CONGE)
            .finConge(UPDATED_FIN_CONGE);
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(updatedStatusEmploye);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, statusEmployeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(UPDATED_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(UPDATED_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(UPDATED_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(UPDATED_FIN_CONGE);
    }

    @Test
    void putNonExistingStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, statusEmployeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStatusEmployeWithPatch() throws Exception {
        // Initialize the database
        statusEmployeRepository.save(statusEmploye).block();

        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();

        // Update the statusEmploye using partial update
        StatusEmploye partialUpdatedStatusEmploye = new StatusEmploye();
        partialUpdatedStatusEmploye.setId(statusEmploye.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStatusEmploye.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStatusEmploye))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(DEFAULT_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(DEFAULT_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(DEFAULT_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(DEFAULT_FIN_CONGE);
    }

    @Test
    void fullUpdateStatusEmployeWithPatch() throws Exception {
        // Initialize the database
        statusEmployeRepository.save(statusEmploye).block();

        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();

        // Update the statusEmploye using partial update
        StatusEmploye partialUpdatedStatusEmploye = new StatusEmploye();
        partialUpdatedStatusEmploye.setId(statusEmploye.getId());

        partialUpdatedStatusEmploye
            .disponibilite(UPDATED_DISPONIBILITE)
            .mission(UPDATED_MISSION)
            .debutConge(UPDATED_DEBUT_CONGE)
            .finConge(UPDATED_FIN_CONGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStatusEmploye.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStatusEmploye))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
        StatusEmploye testStatusEmploye = statusEmployeList.get(statusEmployeList.size() - 1);
        assertThat(testStatusEmploye.getDisponibilite()).isEqualTo(UPDATED_DISPONIBILITE);
        assertThat(testStatusEmploye.getMission()).isEqualTo(UPDATED_MISSION);
        assertThat(testStatusEmploye.getDebutConge()).isEqualTo(UPDATED_DEBUT_CONGE);
        assertThat(testStatusEmploye.getFinConge()).isEqualTo(UPDATED_FIN_CONGE);
    }

    @Test
    void patchNonExistingStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, statusEmployeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStatusEmploye() throws Exception {
        int databaseSizeBeforeUpdate = statusEmployeRepository.findAll().collectList().block().size();
        statusEmploye.setId(count.incrementAndGet());

        // Create the StatusEmploye
        StatusEmployeDTO statusEmployeDTO = statusEmployeMapper.toDto(statusEmploye);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(statusEmployeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the StatusEmploye in the database
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStatusEmploye() {
        // Initialize the database
        statusEmployeRepository.save(statusEmploye).block();

        int databaseSizeBeforeDelete = statusEmployeRepository.findAll().collectList().block().size();

        // Delete the statusEmploye
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, statusEmploye.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<StatusEmploye> statusEmployeList = statusEmployeRepository.findAll().collectList().block();
        assertThat(statusEmployeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
