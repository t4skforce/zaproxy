package org.zaproxy.zap.db.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("databaseServer")
public class DatabaseServer implements org.parosproxy.paros.db.DatabaseServer {

    @Autowired
    private DataSource dataSource;

    // private static final Connection connection;

    @Override
    public Connection getNewConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getSingletonConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
