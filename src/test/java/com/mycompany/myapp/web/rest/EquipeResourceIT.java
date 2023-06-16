package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.EquipeRepository;
import com.mycompany.myapp.service.EquipeService;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.mapper.EquipeMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link EquipeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EquipeResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Long DEFAULT_NOMBRE_PERSONNE = 1L;
    private static final Long UPDATED_NOMBRE_PERSONNE = 2L;

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
    private WebTestClient webTestClient;

    private Equipe equipe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipe createEntity(EntityManager em) {
        Equipe equipe = new Equipe().nom(DEFAULT_NOM).nombrePersonne(DEFAULT_NOMBRE_PERSONNE);
        return equipe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipe createUpdatedEntity(EntityManager em) {
        Equipe equipe = new Equipe().nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE);
        return equipe;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_equipe__vote").block();
            em.deleteAll(Equipe.class).block();
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
        equipe = createEntity(em);
    }

    @Test
    void createEquipe() throws Exception {
        int databaseSizeBeforeCreate = equipeRepository.findAll().collectList().block().size();
        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeCreate + 1);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(DEFAULT_NOMBRE_PERSONNE);
    }

    @Test
    void createEquipeWithExistingId() throws Exception {
        // Create the Equipe with an existing ID
        equipe.setId(1L);
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        int databaseSizeBeforeCreate = equipeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEquipes() {
        // Initialize the database
        equipeRepository.save(equipe).block();

        // Get all the equipeList
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
            .value(hasItem(equipe.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].nombrePersonne")
            .value(hasItem(DEFAULT_NOMBRE_PERSONNE.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipesWithEagerRelationshipsIsEnabled() {
        when(equipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(equipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipesWithEagerRelationshipsIsNotEnabled() {
        when(equipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(equipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEquipe() {
        // Initialize the database
        equipeRepository.save(equipe).block();

        // Get the equipe
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, equipe.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(equipe.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.nombrePersonne")
            .value(is(DEFAULT_NOMBRE_PERSONNE.intValue()));
    }

    @Test
    void getNonExistingEquipe() {
        // Get the equipe
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEquipe() throws Exception {
        // Initialize the database
        equipeRepository.save(equipe).block();

        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();

        // Update the equipe
        Equipe updatedEquipe = equipeRepository.findById(equipe.getId()).block();
        updatedEquipe.nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE);
        EquipeDTO equipeDTO = equipeMapper.toDto(updatedEquipe);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(UPDATED_NOMBRE_PERSONNE);
    }

    @Test
    void putNonExistingEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, equipeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEquipeWithPatch() throws Exception {
        // Initialize the database
        equipeRepository.save(equipe).block();

        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();

        // Update the equipe using partial update
        Equipe partialUpdatedEquipe = new Equipe();
        partialUpdatedEquipe.setId(equipe.getId());

        partialUpdatedEquipe.nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(UPDATED_NOMBRE_PERSONNE);
    }

    @Test
    void fullUpdateEquipeWithPatch() throws Exception {
        // Initialize the database
        equipeRepository.save(equipe).block();

        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();

        // Update the equipe using partial update
        Equipe partialUpdatedEquipe = new Equipe();
        partialUpdatedEquipe.setId(equipe.getId());

        partialUpdatedEquipe.nom(UPDATED_NOM).nombrePersonne(UPDATED_NOMBRE_PERSONNE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEquipe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEquipe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
        Equipe testEquipe = equipeList.get(equipeList.size() - 1);
        assertThat(testEquipe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEquipe.getNombrePersonne()).isEqualTo(UPDATED_NOMBRE_PERSONNE);
    }

    @Test
    void patchNonExistingEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, equipeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEquipe() throws Exception {
        int databaseSizeBeforeUpdate = equipeRepository.findAll().collectList().block().size();
        equipe.setId(count.incrementAndGet());

        // Create the Equipe
        EquipeDTO equipeDTO = equipeMapper.toDto(equipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(equipeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Equipe in the database
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEquipe() {
        // Initialize the database
        equipeRepository.save(equipe).block();

        int databaseSizeBeforeDelete = equipeRepository.findAll().collectList().block().size();

        // Delete the equipe
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, equipe.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Equipe> equipeList = equipeRepository.findAll().collectList().block();
        assertThat(equipeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
