package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class StatusEmployeSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("disponibilite", table, columnPrefix + "_disponibilite"));
        columns.add(Column.aliased("mission", table, columnPrefix + "_mission"));
        columns.add(Column.aliased("debut_conge", table, columnPrefix + "_debut_conge"));
        columns.add(Column.aliased("fin_conge", table, columnPrefix + "_fin_conge"));

        columns.add(Column.aliased("user_id_id", table, columnPrefix + "_user_id_id"));
        return columns;
    }
}
