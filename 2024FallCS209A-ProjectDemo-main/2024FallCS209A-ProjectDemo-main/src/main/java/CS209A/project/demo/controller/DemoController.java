package CS209A.project.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import CS209A.project.demo.service.DataService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final DataService dataService;

    public DemoController(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * In this demo, you can visit localhost:9091 or localhost:9091/demo to see the result.
     * @return the name of the view to be rendered
     * You can find the static HTML file in src/main/resources/templates/demo.html
     */
    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }

    @GetMapping("/topic-frequency")
    public Map<String, Long> getTopicFrequency(@RequestParam(defaultValue = "10") int limit) {
        return dataService.analyzeTopicFrequency(limit);
    }

    @GetMapping("/most-voted-threads")
    public List<Map<String, Object>> getMostVotedThreads(@RequestParam(defaultValue = "5") int limit) {
        return dataService.getMostVotedThreads(limit);
    }

    @GetMapping("/collect-data")
    public String collectData() {
        dataService.collectData();
        return "Data collection started!";
    }

}