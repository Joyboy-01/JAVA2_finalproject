// CS209A/project/demo/controller/AnswerController.java

package CS209A.project.demo.controller;

import CS209A.project.demo.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/answers")
@CrossOrigin(origins = "*") // 允许所有来源，生产环境中请根据需要限制
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService service) {
        this.answerService = service;
    }

//    /**
//     * 获取最常见的 Java 编程相关话题及其频次
//     * @param topN 要返回的最常见话题数量，默认为5
//     * @return 一个 Map，其中键是话题名称，值是频次
//     */
//    @GetMapping("/api/java-topics")
//    public Map<String, Integer> getTopJavaTopics(@RequestParam(defaultValue = "5") int topN) {
//        return answerService.getTopJavaTopics(topN);
//    }

    /**
     * 另一个示例接口
     * @return 一个简单的字符串
     */
    @GetMapping("/api/another-endpoint")
    public String getAnotherData() {
        return "Hello from another endpoint!";
    }
    @GetMapping("/api/answer-quality")
    public Map<String, Map<String, Double>> getAnswerQualityData() {
        return answerService.getAllImpactData();
    }

}
