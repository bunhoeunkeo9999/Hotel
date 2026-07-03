package com.example.record.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RecordSchemaInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RecordSchemaInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public RecordSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void migrateGuestProfileSchema() {
        try {
            if (!tableExists("guest_profiles")) {
                return;
            }

            jdbcTemplate.execute("ALTER TABLE guest_profiles ADD COLUMN IF NOT EXISTS name VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE guest_profiles ADD COLUMN IF NOT EXISTS check_in TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            jdbcTemplate.update("UPDATE guest_profiles SET name = CONCAT(COALESCE(first_name, ''), ' ', COALESCE(last_name, '')) WHERE (name IS NULL OR name = '') AND (first_name IS NOT NULL OR last_name IS NOT NULL)");
            jdbcTemplate.update("UPDATE guest_profiles SET name = 'Unknown Guest' WHERE name IS NULL OR name = ''");
            jdbcTemplate.update("UPDATE guest_profiles SET check_in = CURRENT_TIMESTAMP WHERE check_in IS NULL");
            jdbcTemplate.execute("DROP TABLE IF EXISTS guest_preferences");
            jdbcTemplate.execute("DROP TABLE IF EXISTS guest_stay_history");
            jdbcTemplate.execute("ALTER TABLE guest_profiles DROP COLUMN IF EXISTS first_name");
            jdbcTemplate.execute("ALTER TABLE guest_profiles DROP COLUMN IF EXISTS last_name");
            jdbcTemplate.execute("ALTER TABLE guest_profiles DROP COLUMN IF EXISTS email");
        } catch (Exception ex) {
            logger.warn("Failed to migrate guest_profiles schema", ex);
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName);
        return count != null && count > 0;
    }
}
