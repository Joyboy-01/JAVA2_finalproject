package CS209A.project.demo.service;

import CS209A.project.demo.entity.ErrorEntity;
import CS209A.project.demo.repository.ErrorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ErrorService {
    private final ErrorRepository repo;
    public ErrorService(ErrorRepository repo) {
        this.repo = repo;
    }
    public List<ErrorEntity> findAll(){return repo.findAll();}
    public ErrorEntity save(ErrorEntity e){return repo.save(e);}
    public ErrorEntity findById(Long id){return repo.findById(id).orElse(null);}
    public void delete(Long id){repo.deleteById(id);}
}