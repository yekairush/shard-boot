package cloud.shard.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.shard.entity.User;
import cloud.shard.mapper.UserMapper;
import cloud.shard.service.UserService;

/**
 * 
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	/**
	 * user dao
	 */
	@Autowired
	private UserMapper userMapper;

	@Override
	public void addUser(User user) {
		logger.debug("addUser-------------" + user);
		userMapper.addUser(user);
	}

	@Override
	public List<User> getUsers() {
		List<User> list = userMapper.getUsers();
		logger.debug("getUsers-------------" + list);
		return list;
	}

}
