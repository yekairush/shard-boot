package cloud.shard.config;



import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

/**
 * 
 * @author User
 *
 */
@Configuration
public class DataSourceConfig {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    private Environment env;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public DataSource getDataSource() throws SQLException {
        // 配置表规则
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();
        tableRuleConfig.setLogicTable(env.getProperty("shardingjdbc.logic-table"));
        String actualDataNdes = env.getProperty("shardingjdbc.actual-data-nodes");
        tableRuleConfig.setActualDataNodes(actualDataNdes.replace("->", ""));

        // 配置分库 + 分表策略
        String databaseShardingColumn = env.getProperty("shardingjdbc.database-strategy.inline.sharding-column");
        String databaseAlgorithmExpression = env.getProperty("shardingjdbc.database-strategy.inline.algorithm-expression");
        String tableShardingColumn = env.getProperty("shardingjdbc.tables.standard.sharding-column");
        tableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration(
                databaseShardingColumn, databaseAlgorithmExpression.replace("->", "")));
        //分库分表算法
        String preciseAlgorithmClassName = env.getProperty("shardingjdbc.tables.standard.precise-algorithm-class-name");
        try {
            PreciseShardingAlgorithm algorithm = (PreciseShardingAlgorithm) Class.forName(preciseAlgorithmClassName).newInstance();
            //分表策略，缺省表示使用默认分表策略
            tableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(tableShardingColumn, algorithm));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setDefaultDataSourceName(env.getProperty("shardingjdbc.default-data-source-name"));
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfig);
        logger.info("getDataSource---->shardingRuleConfig "+shardingRuleConfig);
        // 获取数据源对象
        return ShardingDataSourceFactory.createDataSource(getDataSourceMap(), shardingRuleConfig,
                new ConcurrentHashMap(), new Properties());
    }

    /**
     * 获取数据源
     * @return
     */
    private Map<String, DataSource> getDataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        String[] names = env.getProperty("shardingjdbc.datasource.names").split(",");
        //获取属性
        Properties properties = getProperties();
        logger.info("getDataSourceMap---->properties "+properties);
        for (String name : names) {
        	//数据源对应的配置参数
            dataSourceMap.put(name, buildDataSource(name, properties));
        }
        logger.info("DataSourceConfig---->getDataSourceMap "+dataSourceMap);
        return dataSourceMap;
    }

    @SuppressWarnings("unchecked")
    private DataSource buildDataSource(String name, Properties properties) {
        try {
            String prefix = "shardingjdbc.datasource." + name;
            String type = env.getProperty(prefix + ".type");
            Class<DataSource> typeClass = (Class<DataSource>) Class.forName(type);
            String driverClassName = env.getProperty(prefix + ".driver-class-name");
            String url = env.getProperty(prefix + ".url");
            logger.info("buildDataSource--->url="+url);
            String username = env.getProperty(prefix + ".username");
            String password = env.getProperty(prefix + ".password");
            DataSource dataSource = DataSourceBuilder.create().type(typeClass).driverClassName(driverClassName).url(url)
                    .username(username).password(password).build();
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).setDataSourceProperties(properties);
            }
            logger.info("DataSourceConfig---->buildDataSource "+dataSource);
            return dataSource;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 獲取数据源属性集
     * @return
     */
    private Properties getProperties() {
        Properties p = new Properties();
        p.put("minimum-idle", env.getProperty("hikari.minimum-idle"));
        p.put("maximum-pool-size", env.getProperty("hikari.maximum-pool-size"));
        p.put("auto-commit", env.getProperty("hikari.auto-commit"));
        p.put("idle-timeout", env.getProperty("hikari.idle-timeout"));
        p.put("max-lifetime", env.getProperty("hikari.max-lifetime"));
        p.put("connection-timeout", env.getProperty("hikari.connection-timeout"));
        p.put("connection-test-query", env.getProperty("hikari.connection-test-query"));
        return p;
    }

}
