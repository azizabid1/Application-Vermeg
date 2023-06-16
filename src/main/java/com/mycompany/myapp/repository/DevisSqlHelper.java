package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DevisSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("prix_total", table, columnPrefix + "_prix_total"));
        columns.add(Column.aliased("prix_ht", table, columnPrefix + "_prix_ht"));
        columns.add(Column.aliased("prix_service", table, columnPrefix + "_prix_service"));
        columns.add(Column.aliased("duree_projet", table, columnPrefix + "_duree_projet"));

        return columns;
    }
}
