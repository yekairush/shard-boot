package cloud.shard.sharding;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

/**
 * 
 * 
 * @author User
 *
 */
public class ShardPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

	private static final Logger logger = LoggerFactory.getLogger(ShardPreciseShardingAlgorithm.class);

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
		String postfix = "" + (shardingValue.getValue() / 2) % 10;
		logger.info("ShardPreciseShardingAlgorithm--->postfix " + postfix);
		for (String tableName : availableTargetNames) {
			if (tableName.endsWith(postfix)) {
				logger.info("ShardPreciseShardingAlgorithm--->tableName " + tableName);
				return tableName;
			}
		}
		throw new IllegalArgumentException();
	}

}
