package cloud.shard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cloud.shard.entity.User;

/**
 * 
 * @author User
 *
 */
@Mapper
public interface UserMapper {

	/**
	 * add user
	 * 
	 * @param user
	 */
	void addUser(User user);

	/**
	 * get user
	 * 
	 * @return
	 */
	List<User> getUsers();

}
