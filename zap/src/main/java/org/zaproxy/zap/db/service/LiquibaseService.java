package org.zaproxy.zap.db.service;

import liquibase.exception.DatabaseException;
import lombok.NonNull;

public interface LiquibaseService {

    String DEFAULT_CONTEXT = "update";

    String DEFAULT_CHANGELOG_FILE = "db/changelog/master.xml";

    default void update() throws DatabaseException {
        update(DEFAULT_CONTEXT, DEFAULT_CHANGELOG_FILE);
    }

    default void update(@NonNull String changeLogFile) throws DatabaseException {
        update(changeLogFile, DEFAULT_CHANGELOG_FILE);
    }

    void update(@NonNull String context, @NonNull String changeLogFile) throws DatabaseException;

}