package cloud.shard.service;



import java.util.List;

import cloud.shard.entity.User;

public interface UserService {
    
    void addUser(User user);
    
    List<User> getUsers();

}
