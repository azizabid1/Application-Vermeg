package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.enumeration.Status;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Projet}, with proper type conversions.
 */
@Service
public class ProjetRowMapper implements BiFunction<Row, String, Projet> {

    private final ColumnConverter converter;

    public ProjetRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Projet} stored in the database.
     */
    @Override
    public Projet apply(Row row, String prefix) {
        Projet entity = new Projet();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomProjet(converter.fromRow(row, prefix + "_nom_projet", String.class));
        entity.setDateDebut(converter.fromRow(row, prefix + "_date_debut", LocalDate.class));
        entity.setDateFin(converter.fromRow(row, prefix + "_date_fin", LocalDate.class));
        entity.setTechnologies(converter.fromRow(row, prefix + "_technologies", String.class));
        entity.setStatusProjet(converter.fromRow(row, prefix + "_status_projet", Status.class));
        entity.setDevisId(converter.fromRow(row, prefix + "_devis_id", Long.class));
        entity.setEquipeId(converter.fromRow(row, prefix + "_equipe_id", Long.class));
        entity.setTacheId(converter.fromRow(row, prefix + "_tache_id", Long.class));
        return entity;
    }
}
