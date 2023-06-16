package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProjetSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nom_projet", table, columnPrefix + "_nom_projet"));
        columns.add(Column.aliased("date_debut", table, columnPrefix + "_date_debut"));
        columns.add(Column.aliased("date_fin", table, columnPrefix + "_date_fin"));
        columns.add(Column.aliased("technologies", table, columnPrefix + "_technologies"));
        columns.add(Column.aliased("status_projet", table, columnPrefix + "_status_projet"));

        columns.add(Column.aliased("devis_id", table, columnPrefix + "_devis_id"));
        columns.add(Column.aliased("equipe_id", table, columnPrefix + "_equipe_id"));
        columns.add(Column.aliased("tache_id", table, columnPrefix + "_tache_id"));
        return columns;
    }
}
