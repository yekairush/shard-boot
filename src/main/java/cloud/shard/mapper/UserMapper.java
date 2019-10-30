package cloud.shard.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cloud.shard.entity.User;

@Mapper
public interface UserMapper {

    void addUser(User user);

    List<User> getUsers();

}
