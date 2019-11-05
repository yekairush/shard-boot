package cloud.shard.sharding;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

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

	/**
	 * 
	 */
	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
		logger.info("collection:" + JSON.toJSONString(availableTargetNames) + ",preciseShardingValue:" + JSON.toJSONString(shardingValue));
		//注意分片数shardingjdbc.actual-data-nodes=ds$->{0..1}.user_$->{[0, 1, 2]}
		//分库分表
		String postfix = "" + (shardingValue.getValue() / 2) % 3;
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
