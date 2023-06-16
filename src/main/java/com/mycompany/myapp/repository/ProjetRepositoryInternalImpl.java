package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.rowmapper.DevisRowMapper;
import com.mycompany.myapp.repository.rowmapper.EquipeRowMapper;
import com.mycompany.myapp.repository.rowmapper.ProjetRowMapper;
import com.mycompany.myapp.repository.rowmapper.TacheRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Projet entity.
 */
@SuppressWarnings("unused")
class ProjetRepositoryInternalImpl extends SimpleR2dbcRepository<Projet, Long> implements ProjetRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DevisRowMapper devisMapper;
    private final EquipeRowMapper equipeMapper;
    private final TacheRowMapper tacheMapper;
    private final ProjetRowMapper projetMapper;

    private static final Table entityTable = Table.aliased("projet", EntityManager.ENTITY_ALIAS);
    private static final Table devisTable = Table.aliased("devis", "devis");
    private static final Table equipeTable = Table.aliased("equipe", "equipe");
    private static final Table tacheTable = Table.aliased("tache", "tache");

    public ProjetRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DevisRowMapper devisMapper,
        EquipeRowMapper equipeMapper,
        TacheRowMapper tacheMapper,
        ProjetRowMapper projetMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Projet.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.devisMapper = devisMapper;
        this.equipeMapper = equipeMapper;
        this.tacheMapper = tacheMapper;
        this.projetMapper = projetMapper;
    }

    @Override
    public Flux<Projet> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Projet> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjetSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(DevisSqlHelper.getColumns(devisTable, "devis"));
        columns.addAll(EquipeSqlHelper.getColumns(equipeTable, "equipe"));
        columns.addAll(TacheSqlHelper.getColumns(tacheTable, "tache"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(devisTable)
            .on(Column.create("devis_id", entityTable))
            .equals(Column.create("id", devisTable))
            .leftOuterJoin(equipeTable)
            .on(Column.create("equipe_id", entityTable))
            .equals(Column.create("id", equipeTable))
            .leftOuterJoin(tacheTable)
            .on(Column.create("tache_id", entityTable))
            .equals(Column.create("id", tacheTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Projet.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Projet> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Projet> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Projet process(Row row, RowMetadata metadata) {
        Projet entity = projetMapper.apply(row, "e");
        entity.setDevis(devisMapper.apply(row, "devis"));
        entity.setEquipe(equipeMapper.apply(row, "equipe"));
        entity.setTache(tacheMapper.apply(row, "tache"));
        return entity;
    }

    @Override
    public <S extends Projet> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
