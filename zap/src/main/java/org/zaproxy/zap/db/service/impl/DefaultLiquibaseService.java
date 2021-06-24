package org.zaproxy.zap.db.service.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zaproxy.zap.db.service.LiquibaseService;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.NonNull;

@Service
public class DefaultLiquibaseService implements LiquibaseService {

    @Autowired
    private DataSource dataSource;

    @Override
    public synchronized void update(@NonNull String context, @NonNull String changeLogFile) throws DatabaseException {
        try (java.sql.Connection conn = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));
            try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(context);
            }
        } catch (SQLException | LiquibaseException e) {
            throw new DatabaseException(e);
        }
    }

}
