package CS209A.project.demo.controller;

import CS209A.project.demo.entity.ErrorEntity;
import CS209A.project.demo.service.ErrorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/errors")
public class ErrorController {
    private final ErrorService service;
    public ErrorController(ErrorService service) {
        this.service = service;
    }

    @GetMapping
    public List<ErrorEntity> all(){return service.findAll();}
    @GetMapping("/{id}")
    public ErrorEntity get(@PathVariable Long id){return service.findById(id);}
    @PostMapping
    public ErrorEntity create(@RequestBody ErrorEntity e){return service.save(e);}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){service.delete(id);}
}
