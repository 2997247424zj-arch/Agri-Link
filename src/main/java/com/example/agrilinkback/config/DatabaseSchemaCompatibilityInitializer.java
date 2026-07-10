package com.example.agrilinkback.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaCompatibilityInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaCompatibilityInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DatabaseSchemaCompatibilityInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        addColumnIfMissing("tb_order", "stock", "int default null");
        addColumnIfMissing("tb_order", "spec", "varchar(255) default null");
        addColumnIfMissing("tb_order", "unit", "varchar(64) default null");
        addColumnIfMissing("tb_order", "min_purchase", "int default null");
        addColumnIfMissing("tb_purchase", "cancel_reason", "varchar(255) default null");
        addColumnIfMissing("tb_purchase", "delivery_no", "varchar(255) default null");
        addColumnIfMissing("tb_finance", "materials", "text");
        addColumnIfMissing("tb_question", "attachments", "text");
        addColumnIfMissing("tb_reserve", "appointment_time", "varchar(64) default null");
        addColumnIfMissing("tb_reserve", "service_mode", "varchar(64) default null");
        addColumnIfMissing("tb_knowledge", "status", "int default 1");
    }

    private void addColumnIfMissing(String tableName, String columnName, String columnDefinition) throws SQLException {
        if (!tableExists(tableName) || columnExists(tableName, columnName)) {
            return;
        }
        jdbcTemplate.execute("alter table " + tableName + " add column " + columnName + " " + columnDefinition);
        log.info("Added missing database column {}.{}", tableName, columnName);
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, tableName, null)) {
            return tables.next();
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
                return columns.next();
            }
        }
    }
}
