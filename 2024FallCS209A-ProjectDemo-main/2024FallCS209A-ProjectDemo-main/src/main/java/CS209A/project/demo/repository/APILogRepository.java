package CS209A.project.demo.repository;
import CS209A.project.demo.entity.APILog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface APILogRepository extends JpaRepository<APILog, Long> {}