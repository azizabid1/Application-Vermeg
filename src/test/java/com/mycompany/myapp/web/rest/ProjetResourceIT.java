package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProjetRepository;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.mapper.ProjetMapper;
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
 * Integration tests for the {@link ProjetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjetResourceIT {

    private static final String DEFAULT_NOM_PROJET = "AAAAAAAAAA";
    private static final String UPDATED_NOM_PROJET = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TECHNOLOGIES = "AAAAAAAAAA";
    private static final String UPDATED_TECHNOLOGIES = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS_PROJET = Status.ATTENTE;
    private static final Status UPDATED_STATUS_PROJET = Status.COURS;

    private static final String ENTITY_API_URL = "/api/projets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private ProjetMapper projetMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Projet projet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projet createEntity(EntityManager em) {
        Projet projet = new Projet()
            .nomProjet(DEFAULT_NOM_PROJET)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN)
            .technologies(DEFAULT_TECHNOLOGIES)
            .statusProjet(DEFAULT_STATUS_PROJET);
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
            .nomProjet(UPDATED_NOM_PROJET)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .technologies(UPDATED_TECHNOLOGIES)
            .statusProjet(UPDATED_STATUS_PROJET);
        return projet;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Projet.class).block();
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
        projet = createEntity(em);
    }

    @Test
    void createProjet() throws Exception {
        int databaseSizeBeforeCreate = projetRepository.findAll().collectList().block().size();
        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeCreate + 1);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getNomProjet()).isEqualTo(DEFAULT_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(DEFAULT_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(DEFAULT_STATUS_PROJET);
    }

    @Test
    void createProjetWithExistingId() throws Exception {
        // Create the Projet with an existing ID
        projet.setId(1L);
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        int databaseSizeBeforeCreate = projetRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProjets() {
        // Initialize the database
        projetRepository.save(projet).block();

        // Get all the projetList
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
            .value(hasItem(projet.getId().intValue()))
            .jsonPath("$.[*].nomProjet")
            .value(hasItem(DEFAULT_NOM_PROJET))
            .jsonPath("$.[*].dateDebut")
            .value(hasItem(DEFAULT_DATE_DEBUT.toString()))
            .jsonPath("$.[*].dateFin")
            .value(hasItem(DEFAULT_DATE_FIN.toString()))
            .jsonPath("$.[*].technologies")
            .value(hasItem(DEFAULT_TECHNOLOGIES))
            .jsonPath("$.[*].statusProjet")
            .value(hasItem(DEFAULT_STATUS_PROJET.toString()));
    }

    @Test
    void getProjet() {
        // Initialize the database
        projetRepository.save(projet).block();

        // Get the projet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projet.getId().intValue()))
            .jsonPath("$.nomProjet")
            .value(is(DEFAULT_NOM_PROJET))
            .jsonPath("$.dateDebut")
            .value(is(DEFAULT_DATE_DEBUT.toString()))
            .jsonPath("$.dateFin")
            .value(is(DEFAULT_DATE_FIN.toString()))
            .jsonPath("$.technologies")
            .value(is(DEFAULT_TECHNOLOGIES))
            .jsonPath("$.statusProjet")
            .value(is(DEFAULT_STATUS_PROJET.toString()));
    }

    @Test
    void getNonExistingProjet() {
        // Get the projet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProjet() throws Exception {
        // Initialize the database
        projetRepository.save(projet).block();

        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();

        // Update the projet
        Projet updatedProjet = projetRepository.findById(projet.getId()).block();
        updatedProjet
            .nomProjet(UPDATED_NOM_PROJET)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .technologies(UPDATED_TECHNOLOGIES)
            .statusProjet(UPDATED_STATUS_PROJET);
        ProjetDTO projetDTO = projetMapper.toDto(updatedProjet);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projetDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getNomProjet()).isEqualTo(UPDATED_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(UPDATED_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(UPDATED_STATUS_PROJET);
    }

    @Test
    void putNonExistingProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projetDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProjetWithPatch() throws Exception {
        // Initialize the database
        projetRepository.save(projet).block();

        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();

        // Update the projet using partial update
        Projet partialUpdatedProjet = new Projet();
        partialUpdatedProjet.setId(projet.getId());

        partialUpdatedProjet.dateFin(UPDATED_DATE_FIN);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getNomProjet()).isEqualTo(DEFAULT_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(DEFAULT_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(DEFAULT_STATUS_PROJET);
    }

    @Test
    void fullUpdateProjetWithPatch() throws Exception {
        // Initialize the database
        projetRepository.save(projet).block();

        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();

        // Update the projet using partial update
        Projet partialUpdatedProjet = new Projet();
        partialUpdatedProjet.setId(projet.getId());

        partialUpdatedProjet
            .nomProjet(UPDATED_NOM_PROJET)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .technologies(UPDATED_TECHNOLOGIES)
            .statusProjet(UPDATED_STATUS_PROJET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
        Projet testProjet = projetList.get(projetList.size() - 1);
        assertThat(testProjet.getNomProjet()).isEqualTo(UPDATED_NOM_PROJET);
        assertThat(testProjet.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testProjet.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testProjet.getTechnologies()).isEqualTo(UPDATED_TECHNOLOGIES);
        assertThat(testProjet.getStatusProjet()).isEqualTo(UPDATED_STATUS_PROJET);
    }

    @Test
    void patchNonExistingProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projetDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProjet() throws Exception {
        int databaseSizeBeforeUpdate = projetRepository.findAll().collectList().block().size();
        projet.setId(count.incrementAndGet());

        // Create the Projet
        ProjetDTO projetDTO = projetMapper.toDto(projet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projetDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Projet in the database
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProjet() {
        // Initialize the database
        projetRepository.save(projet).block();

        int databaseSizeBeforeDelete = projetRepository.findAll().collectList().block().size();

        // Delete the projet
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Projet> projetList = projetRepository.findAll().collectList().block();
        assertThat(projetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
