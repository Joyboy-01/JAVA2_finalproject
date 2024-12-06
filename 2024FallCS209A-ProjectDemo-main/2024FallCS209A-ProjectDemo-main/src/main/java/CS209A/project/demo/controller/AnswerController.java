package CS209A.project.demo.controller;

import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.service.AnswerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerController {
    private final AnswerService service;
    public AnswerController(AnswerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Answer> all(){return service.findAll();}
    @GetMapping("/{id}")
    public Answer get(@PathVariable Long id){return service.findById(id);}
    @PostMapping
    public Answer create(@RequestBody Answer a){return service.save(a);}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){service.delete(id);}
}
