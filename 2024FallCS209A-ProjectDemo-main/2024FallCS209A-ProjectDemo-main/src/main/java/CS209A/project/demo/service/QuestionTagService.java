package CS209A.project.demo.service;

import CS209A.project.demo.entity.QuestionTag;
import CS209A.project.demo.repository.QuestionTagRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionTagService {
    private final QuestionTagRepository repo;
    public QuestionTagService(QuestionTagRepository repo) {
        this.repo = repo;
    }
    public List<QuestionTag> findAll(){return repo.findAll();}
    public QuestionTag save(QuestionTag t){return repo.save(t);}
    public QuestionTag findById(QuestionTag.QuestionTagId id){return repo.findById(id).orElse(null);}
    public void delete(QuestionTag.QuestionTagId id){repo.deleteById(id);}
}