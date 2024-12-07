package CS209A.project.demo.service;
import CS209A.project.demo.entity.User;
import CS209A.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getHighReputationUsers(int reputationThreshold) {
        // 1. 调用 userRepository.findAll() 获取所有用户
        return userRepository.findAll().stream()  // 将返回的用户列表转换为一个 Stream 流
                .filter(user -> user.getReputation() > reputationThreshold)  // 2. 使用 filter 筛选出声誉大于给定阈值的用户
                .collect(Collectors.toList());  // 3. 将筛选后的结果收集到一个 List 中并返回
    }






//    public UserService(UserRepository repository) {
//        this.repository = repository;
//    }
//    public List<User> findAll() { return repository.findAll(); }
//    public User save(User user) { return repository.save(user); }
//    public User findById(Long id) { return repository.findById(id).orElse(null); }
//    public void delete(Long id) { repository.deleteById(id); }
}