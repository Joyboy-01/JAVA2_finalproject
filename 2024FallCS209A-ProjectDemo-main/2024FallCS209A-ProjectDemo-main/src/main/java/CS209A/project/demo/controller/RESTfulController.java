package CS209A.project.demo.controller;

import CS209A.project.demo.entity.QuestionTag;
import CS209A.project.demo.service.ErrorAnalysisService;
import CS209A.project.demo.service.QuestionTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restful")
public class RESTfulController {
    private final QuestionTagService questionTagService;
    private final ErrorAnalysisService errorAnalysisService;
    @Autowired
    public RESTfulController(QuestionTagService service, ErrorAnalysisService errorAnalysisService) {
        this.questionTagService = service;
        this.errorAnalysisService = errorAnalysisService;
    }

    @GetMapping("/1")
    public Map<String, Integer> getTagFrequency(@RequestParam String tag) {
        // 获取所有问题的标签
        List<QuestionTag> questionTags = questionTagService.getQuestionTags();

        // 创建一个映射，统计指定标签的频率
        int tagCount = 0;

        // 遍历所有标签并统计指定标签的频率
        for (QuestionTag i : questionTags) {
            if (i.getId().getTag().equalsIgnoreCase(tag)) {
                tagCount++;
            }
        }

        // 如果标签的频率大于 0，则返回该标签和其频率；否则返回空的映射
        Map<String, Integer> result = new HashMap<>();
        if (tagCount > 0) {
            result.put(tag, tagCount);
        }
        return result;
    }


    @GetMapping("/2")
    public Map<String, Integer> getTopTags(@RequestParam(defaultValue = "5") int topN) {
        // 获取所有问题的标签
        List<QuestionTag> questionTags = questionTagService.getQuestionTags();

        // 创建一个映射，统计每个标签的频率
        Map<String, Integer> tagFrequency = new HashMap<>();

        // 遍历所有标签并统计频率
        for (QuestionTag tag : questionTags) {
            String tagName = tag.getId().getTag();  // 获取标签名称
            if (!"java".equals(tagName)) {
                tagFrequency.put(tagName, tagFrequency.getOrDefault(tagName, 0) + 1);
            }
        }
        // 按频率排序并返回前 N 个标签
        return tagFrequency.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))  // 按值降序排序
                .limit(topN)  // 获取前 topN 个
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  // 键是标签名称
                        Map.Entry::getValue,  // 值是频率
                        (e1, e2) -> e1,  // 如果有重复的键，选择第一个
                        LinkedHashMap::new  // 保证顺序
                ));
    }

    @GetMapping("/3")
    public Map<String, Integer> countErrorByName(@RequestParam String errorName) {
        return errorAnalysisService.countErrorByName(errorName);
    }

    @GetMapping("/4")
    public Map<String, Integer> geterrors(@RequestParam(defaultValue = "5") int topN) {
        return errorAnalysisService.countErrorsByType(topN);
    }

    @GetMapping("/5")
    public Map<String, Integer> countExceptionByName(@RequestParam String exceptionName) {
        return errorAnalysisService.countExceptionByName(exceptionName);
    }


    @GetMapping("/6")
    public Map<String, Integer> countExceptionByType(@RequestParam(defaultValue = "5") int topN) {
        return errorAnalysisService.countExceptionByType(topN);
    }
}
