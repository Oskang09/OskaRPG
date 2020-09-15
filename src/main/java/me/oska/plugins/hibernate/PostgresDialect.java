package me.oska.plugins.hibernate;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class PostgresDialect extends PostgreSQL94Dialect {
    public PostgresDialect() {
        super();

        this.registerColumnType(300031246, "jsonb");
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
