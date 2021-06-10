package org.zaproxy.zap.db.liquibase;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAutoIncrementChange implements CustomSqlChange, CustomSqlRollback {

    private String catalogName;
    private String schemaName;
    private String tableName;
    private String columnName;
    private String columnDataType;
    private BigInteger startAt;
    private BigInteger incrementBy;
    private Boolean defaultOnNull;
    private String generationType;

    @SuppressWarnings("unused")
    private ResourceAccessor resourceAccessor;

    @Override
    public String getConfirmationMessage() {
        return "Auto-increment added to " + getTableName() + "." + getColumnName() + " starting at " + getStartAt();
    }

    @Override
    public void setUp() throws SetupException {
        // NOP
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    @Override
    public ValidationErrors validate(Database database) {
        ValidationErrors validation = new ValidationErrors();
        validation.checkRequiredField("tableName", getTableName());
        validation.checkRequiredField("columnName", getColumnName());
        validation.checkRequiredField("columnDataType", getColumnDataType());
        return validation;
    }

    @Override
    public SqlStatement[] generateRollbackStatements(Database database)
            throws CustomChangeException, RollbackImpossibleException {
        return getChange().generateRollbackStatements(database);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) throws CustomChangeException {

        try {
            final JdbcConnection connection = (JdbcConnection) database.getConnection();
            String maxIdSql = new StringBuilder().append("SELECT MAX(")
                    .append(database.escapeColumnName(getCatalogName(), getSchemaName(), getTableName(),
                            getColumnName()))
                    .append(") FROM ")
                    .append(database.escapeTableName(getCatalogName(), getSchemaName(), getTableName()))
                    .toString();

            final Statement selectmaxId = connection.createStatement();
            selectmaxId.execute(maxIdSql);
            ResultSet results = selectmaxId.getResultSet();
            while (results.next()) {
                BigInteger selMax = BigInteger.valueOf(results.getLong(1) + 1);
                if (null == getStartAt()) {
                    setStartAt(selMax);
                } else {
                    setStartAt(getStartAt().max(selMax));
                }
            }
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }

        List<SqlStatement> statements = new ArrayList<>(Arrays.asList(getChange().generateStatements(database)));

        String alterSql = new StringBuilder().append("ALTER TABLE ")
                .append(database.escapeTableName(getCatalogName(), getSchemaName(), getTableName()))
                .append(" ALTER COLUMN ")
                .append(database.escapeColumnName(getCatalogName(), getSchemaName(), getTableName(), getColumnName()))
                .append(" RESTART WITH ")
                .append(getStartAt())
                .toString();

        statements.add(new RawSqlStatement(alterSql));
        return statements.toArray(new SqlStatement[] {});
    }

    private liquibase.change.core.AddAutoIncrementChange getChange() {
        liquibase.change.core.AddAutoIncrementChange change = new liquibase.change.core.AddAutoIncrementChange();
        change.setCatalogName(getCatalogName());
        change.setSchemaName(getSchemaName());
        change.setTableName(getTableName());
        change.setColumnName(getColumnName());
        change.setColumnDataType(getColumnDataType());
        change.setIncrementBy(null);
        change.setStartWith(null);
        change.setDefaultOnNull(getDefaultOnNull());
        change.setGenerationType(getGenerationType());
        return change;
    }
}
