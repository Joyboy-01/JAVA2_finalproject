package CS209A.project.demo.service;
import CS209A.project.demo.entity.User;
import CS209A.project.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    public List<User> findAll() { return repository.findAll(); }
    public User save(User user) { return repository.save(user); }
    public User findById(Long id) { return repository.findById(id).orElse(null); }
    public void delete(Long id) { repository.deleteById(id); }
}