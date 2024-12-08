package CS209A.project.demo.controller;

import CS209A.project.demo.entity.QuestionTag;
import CS209A.project.demo.service.QuestionTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
public class QuestionTagController {
    private final QuestionTagService questionTagService;
    @Autowired
    public QuestionTagController(QuestionTagService service) {
        this.questionTagService = service;
    }

    @GetMapping("/getTopTags")
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
//    @GetMapping
//    public List<QuestionTag> all(){return service.findAll();}
    // 根据业务需求决定查询和删除的方式，此处略
}