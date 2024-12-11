package CS209A.project.demo.controller;



import CS209A.project.demo.service.ErrorAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/er")
public class ErrorController {
    private final ErrorAnalysisService errorAnalysisService;
    @Autowired
    public ErrorController(ErrorAnalysisService errorAnalysisService) {
        this.errorAnalysisService = errorAnalysisService;
    }
    @GetMapping("/errors")
    public Map<String, Integer> geterrors(@RequestParam(defaultValue = "5") int topN) {
        return errorAnalysisService.countErrorsByType(topN);
    }
    @GetMapping("/exceptions")
    public Map<String, Integer> getexceptions(@RequestParam(defaultValue = "5") int topN) {
        return errorAnalysisService.countExceptionByType(topN);
    }
}
