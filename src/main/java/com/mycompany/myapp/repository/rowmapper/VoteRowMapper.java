package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.domain.enumeration.Rendement;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Vote}, with proper type conversions.
 */
@Service
public class VoteRowMapper implements BiFunction<Row, String, Vote> {

    private final ColumnConverter converter;

    public VoteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Vote} stored in the database.
     */
    @Override
    public Vote apply(Row row, String prefix) {
        Vote entity = new Vote();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTypeVote(converter.fromRow(row, prefix + "_type_vote", Rendement.class));
        return entity;
    }
}
