package CS209A.project.demo.repository;
import CS209A.project.demo.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {}
