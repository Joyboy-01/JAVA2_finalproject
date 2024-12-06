// CS209A/project/demo/service/AnswerService.java

package CS209A.project.demo.service;

import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    /**
     * 获取最常见的 Java 编程相关话题及其频次
     * @param topN 要返回的最常见话题数量
     * @return 一个 Map，其中键是话题名称，值是频次
     */
    public Map<String, Integer> getTopJavaTopics(int topN) {
        List<Answer> answers = answerRepository.findAll();

        return answers.stream()
                .filter(answer -> answer.getContent() != null && answer.getContent().contains("Java"))
                .map(answer -> {
                    String content = answer.getContent();
                    // 安全地提取内容的前20个字符作为话题
                    return content.length() > 20 ? content.substring(0, 20) : content;
                })
                .collect(Collectors.groupingBy(topic -> topic, Collectors.reducing(0, e -> 1, Integer::sum)))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 获取所有答案
     * @return 所有答案的列表
     */
    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }
}
