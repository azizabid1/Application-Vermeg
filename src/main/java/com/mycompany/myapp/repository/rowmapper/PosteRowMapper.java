package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Poste;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Poste}, with proper type conversions.
 */
@Service
public class PosteRowMapper implements BiFunction<Row, String, Poste> {

    private final ColumnConverter converter;

    public PosteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Poste} stored in the database.
     */
    @Override
    public Poste apply(Row row, String prefix) {
        Poste entity = new Poste();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setUserIdId(converter.fromRow(row, prefix + "_user_id_id", Long.class));
        return entity;
    }
}
