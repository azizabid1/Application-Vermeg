package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.StatusEmploye;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link StatusEmploye}, with proper type conversions.
 */
@Service
public class StatusEmployeRowMapper implements BiFunction<Row, String, StatusEmploye> {

    private final ColumnConverter converter;

    public StatusEmployeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link StatusEmploye} stored in the database.
     */
    @Override
    public StatusEmploye apply(Row row, String prefix) {
        StatusEmploye entity = new StatusEmploye();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDisponibilite(converter.fromRow(row, prefix + "_disponibilite", Boolean.class));
        entity.setMission(converter.fromRow(row, prefix + "_mission", Boolean.class));
        entity.setDebutConge(converter.fromRow(row, prefix + "_debut_conge", LocalDate.class));
        entity.setFinConge(converter.fromRow(row, prefix + "_fin_conge", LocalDate.class));
        entity.setUserIdId(converter.fromRow(row, prefix + "_user_id_id", Long.class));
        return entity;
    }
}
