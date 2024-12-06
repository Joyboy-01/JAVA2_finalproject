package CS209A.project.demo.service;

import CS209A.project.demo.entity.Question;
import CS209A.project.demo.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository repo;
    public QuestionService(QuestionRepository repo) {
        this.repo = repo;
    }
    public List<Question> findAll() {return repo.findAll();}
    public Question save(Question q) {return repo.save(q);}
    public Question findById(Long id) {return repo.findById(id).orElse(null);}
    public void delete(Long id) {repo.deleteById(id);}
}