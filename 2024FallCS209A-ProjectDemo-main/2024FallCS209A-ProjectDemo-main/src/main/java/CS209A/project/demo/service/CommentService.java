package CS209A.project.demo.service;

import CS209A.project.demo.entity.Comment;
import CS209A.project.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository repo;
    public CommentService(CommentRepository repo) {
        this.repo = repo;
    }
    public List<Comment> findAll(){return repo.findAll();}
    public Comment save(Comment c){return repo.save(c);}
    public Comment findById(Long id){return repo.findById(id).orElse(null);}
    public void delete(Long id){repo.deleteById(id);}
}