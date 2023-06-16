package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.Status;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tache}, with proper type conversions.
 */
@Service
public class TacheRowMapper implements BiFunction<Row, String, Tache> {

    private final ColumnConverter converter;

    public TacheRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tache} stored in the database.
     */
    @Override
    public Tache apply(Row row, String prefix) {
        Tache entity = new Tache();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitre(converter.fromRow(row, prefix + "_titre", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStatusTache(converter.fromRow(row, prefix + "_status_tache", Status.class));
        return entity;
    }
}
