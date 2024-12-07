package CS209A.project.demo.controller;


import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.entity.User;
import CS209A.project.demo.service.AnswerService;
import CS209A.project.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    private final UserService userService;
    private final AnswerService answerService;

    @Autowired
    public TestController(UserService userService, AnswerService answerService) {
        this.userService = userService;
        this.answerService = answerService;
    }

    @GetMapping("/1")
    public List<Answer> getHighReputationAnswers() {
        List<User> highReputationUsers = userService.getHighReputationUsers(100000);
        return answerService.getHighReputationAnswers(highReputationUsers);
    }
}
