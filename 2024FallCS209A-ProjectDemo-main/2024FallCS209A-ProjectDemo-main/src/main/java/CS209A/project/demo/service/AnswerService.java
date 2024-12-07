// CS209A/project/demo/service/AnswerService.java

package CS209A.project.demo.service;

import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import CS209A.project.demo.entity.User;

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
                    // 安全地提取内容地前20个字符作为话题
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


    // 获取高声誉用户的所有回答
    public List<Answer> getHighReputationAnswers(List<User> highReputationUsers) {
        // 将高声誉用户的 userId 提取到一个 Set 中，Set 可以帮助我们快速判断用户是否在高声誉用户列表中
        Set<Long> highReputationUserIds = highReputationUsers.stream()
                .map(User::getExternalUserId)  // 提取每个高声誉用户的 externaluserId
                .collect(Collectors.toSet());  // 将所有 userId 收集到一个 Set 中，避免重复

        // 从数据库中获取所有的回答，并过滤出属于高声誉用户的回答
        return answerRepository.findAll().stream()
                .filter(answer -> highReputationUserIds.contains(answer.getUserId()))  // 过滤：如果回答的用户在高声誉用户中，则保留
                .collect(Collectors.toList());  // 将符合条件的回答收集到一个 List 中返回
    }




    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }
}
