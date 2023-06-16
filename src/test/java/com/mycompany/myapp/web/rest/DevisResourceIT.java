package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.mapper.DevisMapper;
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
 * Integration tests for the {@link DevisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DevisResourceIT {

    private static final Double DEFAULT_PRIX_TOTAL = 1D;
    private static final Double UPDATED_PRIX_TOTAL = 2D;

    private static final Double DEFAULT_PRIX_HT = 1D;
    private static final Double UPDATED_PRIX_HT = 2D;

    private static final Double DEFAULT_PRIX_SERVICE = 1D;
    private static final Double UPDATED_PRIX_SERVICE = 2D;

    private static final LocalDate DEFAULT_DUREE_PROJET = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUREE_PROJET = LocalDate.now(ZoneId.systemDefault());

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
    private WebTestClient webTestClient;

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
            .dureeProjet(DEFAULT_DUREE_PROJET);
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
            .dureeProjet(UPDATED_DUREE_PROJET);
        return devis;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Devis.class).block();
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
        devis = createEntity(em);
    }

    @Test
    void createDevis() throws Exception {
        int databaseSizeBeforeCreate = devisRepository.findAll().collectList().block().size();
        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeCreate + 1);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(DEFAULT_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(DEFAULT_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(DEFAULT_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(DEFAULT_DUREE_PROJET);
    }

    @Test
    void createDevisWithExistingId() throws Exception {
        // Create the Devis with an existing ID
        devis.setId(1L);
        DevisDTO devisDTO = devisMapper.toDto(devis);

        int databaseSizeBeforeCreate = devisRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDevis() {
        // Initialize the database
        devisRepository.save(devis).block();

        // Get all the devisList
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
            .value(hasItem(devis.getId().intValue()))
            .jsonPath("$.[*].prixTotal")
            .value(hasItem(DEFAULT_PRIX_TOTAL.doubleValue()))
            .jsonPath("$.[*].prixHT")
            .value(hasItem(DEFAULT_PRIX_HT.doubleValue()))
            .jsonPath("$.[*].prixService")
            .value(hasItem(DEFAULT_PRIX_SERVICE.doubleValue()))
            .jsonPath("$.[*].dureeProjet")
            .value(hasItem(DEFAULT_DUREE_PROJET.toString()));
    }

    @Test
    void getDevis() {
        // Initialize the database
        devisRepository.save(devis).block();

        // Get the devis
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, devis.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(devis.getId().intValue()))
            .jsonPath("$.prixTotal")
            .value(is(DEFAULT_PRIX_TOTAL.doubleValue()))
            .jsonPath("$.prixHT")
            .value(is(DEFAULT_PRIX_HT.doubleValue()))
            .jsonPath("$.prixService")
            .value(is(DEFAULT_PRIX_SERVICE.doubleValue()))
            .jsonPath("$.dureeProjet")
            .value(is(DEFAULT_DUREE_PROJET.toString()));
    }

    @Test
    void getNonExistingDevis() {
        // Get the devis
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDevis() throws Exception {
        // Initialize the database
        devisRepository.save(devis).block();

        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();

        // Update the devis
        Devis updatedDevis = devisRepository.findById(devis.getId()).block();
        updatedDevis
            .prixTotal(UPDATED_PRIX_TOTAL)
            .prixHT(UPDATED_PRIX_HT)
            .prixService(UPDATED_PRIX_SERVICE)
            .dureeProjet(UPDATED_DUREE_PROJET);
        DevisDTO devisDTO = devisMapper.toDto(updatedDevis);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, devisDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(UPDATED_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(UPDATED_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(UPDATED_DUREE_PROJET);
    }

    @Test
    void putNonExistingDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, devisDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDevisWithPatch() throws Exception {
        // Initialize the database
        devisRepository.save(devis).block();

        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();

        // Update the devis using partial update
        Devis partialUpdatedDevis = new Devis();
        partialUpdatedDevis.setId(devis.getId());

        partialUpdatedDevis.prixTotal(UPDATED_PRIX_TOTAL).prixHT(UPDATED_PRIX_HT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDevis.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDevis))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(UPDATED_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(DEFAULT_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(DEFAULT_DUREE_PROJET);
    }

    @Test
    void fullUpdateDevisWithPatch() throws Exception {
        // Initialize the database
        devisRepository.save(devis).block();

        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();

        // Update the devis using partial update
        Devis partialUpdatedDevis = new Devis();
        partialUpdatedDevis.setId(devis.getId());

        partialUpdatedDevis
            .prixTotal(UPDATED_PRIX_TOTAL)
            .prixHT(UPDATED_PRIX_HT)
            .prixService(UPDATED_PRIX_SERVICE)
            .dureeProjet(UPDATED_DUREE_PROJET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDevis.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDevis))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
        Devis testDevis = devisList.get(devisList.size() - 1);
        assertThat(testDevis.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testDevis.getPrixHT()).isEqualTo(UPDATED_PRIX_HT);
        assertThat(testDevis.getPrixService()).isEqualTo(UPDATED_PRIX_SERVICE);
        assertThat(testDevis.getDureeProjet()).isEqualTo(UPDATED_DUREE_PROJET);
    }

    @Test
    void patchNonExistingDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, devisDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDevis() throws Exception {
        int databaseSizeBeforeUpdate = devisRepository.findAll().collectList().block().size();
        devis.setId(count.incrementAndGet());

        // Create the Devis
        DevisDTO devisDTO = devisMapper.toDto(devis);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(devisDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Devis in the database
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDevis() {
        // Initialize the database
        devisRepository.save(devis).block();

        int databaseSizeBeforeDelete = devisRepository.findAll().collectList().block().size();

        // Delete the devis
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, devis.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Devis> devisList = devisRepository.findAll().collectList().block();
        assertThat(devisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
