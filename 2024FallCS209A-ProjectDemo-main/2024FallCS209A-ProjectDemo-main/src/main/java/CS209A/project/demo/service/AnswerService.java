package CS209A.project.demo.service;

import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.repository.AnswerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository repo;
    public AnswerService(AnswerRepository repo) {
        this.repo = repo;
    }
    public List<Answer> findAll(){return repo.findAll();}
    public Answer save(Answer a){return repo.save(a);}
    public Answer findById(Long id){return repo.findById(id).orElse(null);}
    public void delete(Long id){repo.deleteById(id);}
}