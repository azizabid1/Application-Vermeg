package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.repository.rowmapper.StatusEmployeRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the StatusEmploye entity.
 */
@SuppressWarnings("unused")
class StatusEmployeRepositoryInternalImpl extends SimpleR2dbcRepository<StatusEmploye, Long> implements StatusEmployeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final StatusEmployeRowMapper statusemployeMapper;

    private static final Table entityTable = Table.aliased("status_employe", EntityManager.ENTITY_ALIAS);
    private static final Table userIdTable = Table.aliased("jhi_user", "userId");

    public StatusEmployeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        StatusEmployeRowMapper statusemployeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(StatusEmploye.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.statusemployeMapper = statusemployeMapper;
    }

    @Override
    public Flux<StatusEmploye> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<StatusEmploye> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = StatusEmployeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userIdTable, "userId"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userIdTable)
            .on(Column.create("user_id_id", entityTable))
            .equals(Column.create("id", userIdTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, StatusEmploye.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<StatusEmploye> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<StatusEmploye> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private StatusEmploye process(Row row, RowMetadata metadata) {
        StatusEmploye entity = statusemployeMapper.apply(row, "e");
        entity.setUserId(userMapper.apply(row, "userId"));
        return entity;
    }

    @Override
    public <S extends StatusEmploye> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
