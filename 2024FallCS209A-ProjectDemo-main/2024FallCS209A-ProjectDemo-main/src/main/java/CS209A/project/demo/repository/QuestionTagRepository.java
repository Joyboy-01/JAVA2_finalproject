package CS209A.project.demo.repository;
import CS209A.project.demo.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository

public interface QuestionTagRepository extends JpaRepository<QuestionTag, QuestionTag.QuestionTagId> {}