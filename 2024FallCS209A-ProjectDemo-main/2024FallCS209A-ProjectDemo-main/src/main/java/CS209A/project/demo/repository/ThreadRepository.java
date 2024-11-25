package CS209A.project.demo.repository;

import CS209A.project.demo.model.StackOverflowThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<StackOverflowThread, Long> {
}