package CS209A.project.demo.controller;

import CS209A.project.demo.entity.Vote;
import CS209A.project.demo.service.VoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private final VoteService service;
    public VoteController(VoteService service) {
        this.service = service;
    }

//    @GetMapping
//    public List<Vote> all(){return service.findAll();}
//    @GetMapping("/{id}")
//    public Vote get(@PathVariable Long id){return service.findById(id);}
//    @PostMapping
//    public Vote create(@RequestBody Vote v){return service.save(v);}
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable Long id){service.delete(id);}
}