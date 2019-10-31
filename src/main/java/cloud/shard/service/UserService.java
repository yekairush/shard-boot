package cloud.shard.service;

import java.util.List;

import cloud.shard.entity.User;

/**
 * 
 * @author User
 *
 */
public interface UserService {

	/**
	 * 
	 * @param user
	 */
	public void addUser(User user);

	/**
	 * 
	 * @return
	 */
	public List<User> getUsers();

}
