package CS209A.project.demo.controller;

import CS209A.project.demo.entity.QuestionTag;
import CS209A.project.demo.service.QuestionTagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/tags")
public class QuestionTagController {
    private final QuestionTagService service;
    public QuestionTagController(QuestionTagService service) {
        this.service = service;
    }

//    @GetMapping
//    public List<QuestionTag> all(){return service.findAll();}
    // 根据业务需求决定查询和删除的方式，此处略
}