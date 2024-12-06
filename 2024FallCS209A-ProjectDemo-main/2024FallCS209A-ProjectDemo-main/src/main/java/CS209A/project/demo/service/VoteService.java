package CS209A.project.demo.service;

import CS209A.project.demo.entity.Vote;
import CS209A.project.demo.repository.VoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VoteService {
    private final VoteRepository repo;
    public VoteService(VoteRepository repo) {
        this.repo = repo;
    }
    public List<Vote> findAll(){return repo.findAll();}
    public Vote save(Vote v){return repo.save(v);}
    public Vote findById(Long id){return repo.findById(id).orElse(null);}
    public void delete(Long id){repo.deleteById(id);}
}