package CS209A.project.demo.controller;

import CS209A.project.demo.entity.Question;
import CS209A.project.demo.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService service;
    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Question> all(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Question get(@PathVariable Long id){
        return service.findById(id);
    }

    @PostMapping
    public Question create(@RequestBody Question q){
        return service.save(q);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}