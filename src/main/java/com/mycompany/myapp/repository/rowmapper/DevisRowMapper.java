package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Devis;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Devis}, with proper type conversions.
 */
@Service
public class DevisRowMapper implements BiFunction<Row, String, Devis> {

    private final ColumnConverter converter;

    public DevisRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Devis} stored in the database.
     */
    @Override
    public Devis apply(Row row, String prefix) {
        Devis entity = new Devis();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPrixTotal(converter.fromRow(row, prefix + "_prix_total", Double.class));
        entity.setPrixHT(converter.fromRow(row, prefix + "_prix_ht", Double.class));
        entity.setPrixService(converter.fromRow(row, prefix + "_prix_service", Double.class));
        entity.setDureeProjet(converter.fromRow(row, prefix + "_duree_projet", LocalDate.class));
        return entity;
    }
}
