package CS209A.project.demo.service;

import CS209A.project.demo.entity.APILog;
import CS209A.project.demo.repository.APILogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class APILogService {
    private final APILogRepository repo;
    public APILogService(APILogRepository repo) {
        this.repo = repo;
    }
    public List<APILog> findAll(){return repo.findAll();}
    public APILog save(APILog l){return repo.save(l);}
    public APILog findById(Long id){return repo.findById(id).orElse(null);}
    public void delete(Long id){repo.deleteById(id);}
}