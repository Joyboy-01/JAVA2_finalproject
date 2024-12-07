// CS209A/project/demo/service/AnswerService.java

package CS209A.project.demo.service;

import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.entity.Question;
import CS209A.project.demo.repository.AnswerRepository;
import CS209A.project.demo.repository.CommentRepository;
import CS209A.project.demo.repository.QuestionRepository;
import CS209A.project.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import CS209A.project.demo.entity.User;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private CommentRepository commentRepository;
    public Long getCommentCountForAnswer(Long answerId) {
        return commentRepository.countByAnswer_ExternalAnswerId(answerId);
    }
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
    // 分析时间间隔对答案质量的影响
    public Map<String, Long> analyzeTimeIntervalImpact() {
        return answerRepository.findAll().stream()
                .map(answer -> {
                    Long questionId = answer.getQuestionId();
                    return questionRepository.findById(questionId)
                            .map(question -> Duration.between(question.getCreationDate(), answer.getCreationDate()).toMinutes())
                            .map(minutes -> {
                                if (minutes < 10) return "0-10分钟";
                                else if (minutes < 30) return "10-30分钟";
                                else if (minutes < 60) return "30-60分钟";
                                else return "超过60分钟";
                            }).orElse(null); // 如果没有找到问题, 返回 null
                })
                .filter(Objects::nonNull) // 过滤掉 null
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); // 统计每个区间的数量
    }

    // 分析用户声誉对答案质量的影响
    public Map<String, Long> analyzeUserReputationImpact() {
        return answerRepository.findAll().stream()
                .map(answer -> userRepository.findById(answer.getUserId())
                        .map(user -> {
                            int reputation = user.getReputation();
                            if (reputation < 100) return "低声誉 (<100)";
                            else if (reputation < 500) return "中声誉 (100-499)";
                            else return "高声誉 (≥500)";
                        }).orElse(null)) // 如果没有找到用户, 返回 null
                .filter(Objects::nonNull) // 过滤掉 null
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); // 统计每种声誉的数量
    }

    // 分析内容长度对答案质量的影响
    public Map<String, Long> analyzeContentLengthImpact() {
        return answerRepository.findAll().stream()
                .map(answer -> {
                    int length = answer.getContentLength();
                    if (length < 100) return "短答案 (<100字)";
                    else if (length < 300) return "中等长度 (100-299字)";
                    else return "长答案 (≥300字)";
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); // 统计每个长度区间的数量
    }

    // 获取评论数量对答案质量的影响
    public Map<String, Long> analyzeCommentCountImpact() {
        return answerRepository.findAll().stream()
                .map(answer -> {
                    Long commentCount = getCommentCountForAnswer(answer.getAnswerId());
                    if (commentCount == 0) return "无评论";
                    else if (commentCount <= 2) return "少量评论 (1-2)";
                    else if (commentCount <= 5) return "中等评论 (3-5)";
                    else return "大量评论 (>5)";
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); // 统计每个评论数量区间的数量
    }

    // 获取答案得分对答案质量的影响
    public Map<String, Long> analyzeScoreImpact() {
        return answerRepository.findAll().stream()
                .map(answer -> {
                    Integer score = answer.getScore();
                    if (score == null) return "未评分";
                    else if (score < 0) return "负评分 (<0)";
                    else if (score == 0) return "中性评分 (0)";
                    else if (score <= 10) return "低评分 (1-10)";
                    else return "高评分 (>10)";
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); // 统计每个评分区间的数量
    }


    // 综合所有因素进行分析
    public Map<String, Map<String, Long>> getAllImpactData() {
        Map<String, Map<String, Long>> allData = new HashMap<>();
        allData.put("时间间隔", analyzeTimeIntervalImpact());
        allData.put("用户声誉", analyzeUserReputationImpact());
        allData.put("答案长度", analyzeContentLengthImpact());
        allData.put("答案得分", analyzeScoreImpact());
        allData.put("评论数量", analyzeCommentCountImpact());
        return allData;
    }
}
