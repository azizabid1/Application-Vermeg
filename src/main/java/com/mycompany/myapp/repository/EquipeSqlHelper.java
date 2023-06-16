package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EquipeSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nom", table, columnPrefix + "_nom"));
        columns.add(Column.aliased("nombre_personne", table, columnPrefix + "_nombre_personne"));

        columns.add(Column.aliased("user_id_id", table, columnPrefix + "_user_id_id"));
        return columns;
    }
}
