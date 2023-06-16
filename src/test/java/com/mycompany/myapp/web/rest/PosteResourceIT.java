package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.PosteRepository;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.mapper.PosteMapper;
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
 * Integration tests for the {@link PosteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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

    @Autowired
    private PosteMapper posteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Poste.class).block();
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
        poste = createEntity(em);
    }

    @Test
    void createPoste() throws Exception {
        int databaseSizeBeforeCreate = posteRepository.findAll().collectList().block().size();
        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeCreate + 1);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createPosteWithExistingId() throws Exception {
        // Create the Poste with an existing ID
        poste.setId(1L);
        PosteDTO posteDTO = posteMapper.toDto(poste);

        int databaseSizeBeforeCreate = posteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPostes() {
        // Initialize the database
        posteRepository.save(poste).block();

        // Get all the posteList
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
            .value(hasItem(poste.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getPoste() {
        // Initialize the database
        posteRepository.save(poste).block();

        // Get the poste
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, poste.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(poste.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingPoste() {
        // Get the poste
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPoste() throws Exception {
        // Initialize the database
        posteRepository.save(poste).block();

        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();

        // Update the poste
        Poste updatedPoste = posteRepository.findById(poste.getId()).block();
        updatedPoste.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        PosteDTO posteDTO = posteMapper.toDto(updatedPoste);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, posteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, posteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePosteWithPatch() throws Exception {
        // Initialize the database
        posteRepository.save(poste).block();

        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();

        // Update the poste using partial update
        Poste partialUpdatedPoste = new Poste();
        partialUpdatedPoste.setId(poste.getId());

        partialUpdatedPoste.title(UPDATED_TITLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPoste))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdatePosteWithPatch() throws Exception {
        // Initialize the database
        posteRepository.save(poste).block();

        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();

        // Update the poste using partial update
        Poste partialUpdatedPoste = new Poste();
        partialUpdatedPoste.setId(poste.getId());

        partialUpdatedPoste.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPoste.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPoste))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
        Poste testPoste = posteList.get(posteList.size() - 1);
        assertThat(testPoste.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPoste.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, posteDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPoste() throws Exception {
        int databaseSizeBeforeUpdate = posteRepository.findAll().collectList().block().size();
        poste.setId(count.incrementAndGet());

        // Create the Poste
        PosteDTO posteDTO = posteMapper.toDto(poste);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(posteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Poste in the database
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePoste() {
        // Initialize the database
        posteRepository.save(poste).block();

        int databaseSizeBeforeDelete = posteRepository.findAll().collectList().block().size();

        // Delete the poste
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, poste.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Poste> posteList = posteRepository.findAll().collectList().block();
        assertThat(posteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
