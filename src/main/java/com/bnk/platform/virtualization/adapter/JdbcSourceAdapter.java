package com.bnk.platform.virtualization.adapter;

import com.bnk.platform.virtualization.config.AffiliateProperties;
import com.bnk.platform.virtualization.domain.ProductView;
import com.bnk.platform.virtualization.service.SourceSqlBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** 실제 계열사 DB에 JDBC로 조회를 수행하는 Adapter입니다. local 프로필에서는 생성되지 않습니다. */
@Component
@Profile("!local")
public class JdbcSourceAdapter implements SourceAdapter, DisposableBean {
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
    private final SourceSqlBuilder sqlBuilder;

    public JdbcSourceAdapter(SourceSqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    @Override
    public List<ProductView> execute(SourceQuery query, String itemName) {
        try (var connection = dataSource(query).getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sqlBuilder.productByName(query.affiliate()))) {
            statement.setString(1, itemName);
            statement.setQueryTimeout(10);
            try (ResultSet resultSet = statement.executeQuery()) {
                var result = new java.util.ArrayList<ProductView>();
                while (resultSet.next()) {
                    result.add(new ProductView(
                            query.affiliateCode(),
                            resultSet.getString("ITEM_CODE"),
                            resultSet.getString("ITEM_NAME"),
                            resultSet.getString("ITEM_TYPE"),
                            resultSet.getBigDecimal("ANNUAL_RATE"),
                            resultSet.getString("STATUS"),
                            lastChangedAt(resultSet, "LAST_CHANGED_AT")));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB 조회에 실패했습니다: " + query.affiliateCode(), e);
        }
    }

    private DataSource dataSource(SourceQuery query) {
        return dataSources.computeIfAbsent(query.affiliateCode(), ignored -> createDataSource(query));
    }

    private HikariDataSource createDataSource(SourceQuery query) {
        AffiliateProperties.Affiliate source = query.affiliate();
        if (isBlank(source.getJdbcUrl()) || isBlank(source.getUsername()) || source.getPassword() == null) {
            throw new IllegalStateException("DB 접속 설정이 비어 있습니다: " + query.affiliateCode());
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("affiliate-" + query.affiliateCode());
        config.setJdbcUrl(source.getJdbcUrl());
        config.setUsername(source.getUsername());
        config.setPassword(source.getPassword());
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(5_000);
        config.setValidationTimeout(3_000);
        config.setInitializationFailTimeout(0);
        return new HikariDataSource(config);
    }

    private OffsetDateTime lastChangedAt(ResultSet resultSet, String column) throws SQLException {
        Timestamp value = resultSet.getTimestamp(column);
        return value == null ? null : value.toInstant().atOffset(ZoneOffset.UTC);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public void destroy() {
        dataSources.values().forEach(HikariDataSource::close);
    }
}
