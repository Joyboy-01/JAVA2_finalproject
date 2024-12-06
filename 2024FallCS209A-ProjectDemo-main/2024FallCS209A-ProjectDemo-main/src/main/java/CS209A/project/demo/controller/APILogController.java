package CS209A.project.demo.controller;

import CS209A.project.demo.entity.APILog;
import CS209A.project.demo.service.APILogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apilogs")
public class APILogController {
    private final APILogService service;
    public APILogController(APILogService service) {
        this.service = service;
    }

    @GetMapping
    public List<APILog> all(){return service.findAll();}
    @GetMapping("/{id}")
    public APILog get(@PathVariable Long id){return service.findById(id);}
    @PostMapping
    public APILog create(@RequestBody APILog l){return service.save(l);}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){service.delete(id);}
}