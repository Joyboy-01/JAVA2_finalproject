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


    public Map<String, Double> analyzeTimeIntervalAcceptanceRate() {
        Map<Long, LocalDateTime> questionCreationDates = questionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Question::getExternalQuestionId,
                        Question::getCreationDate,
                        (existing, replacement) -> existing)
                );

        Map<String, Long> totalCounts = new HashMap<>();
        Map<String, Long> acceptedCounts = new HashMap<>();

        answerRepository.findAll().forEach(answer -> {
            Long questionId = answer.getQuestionId();
            LocalDateTime questionCreationDate = questionCreationDates.get(questionId);
            if (questionCreationDate != null) {
                long minutes = Duration.between(questionCreationDate, answer.getCreationDate()).toMinutes();
                String category;
                if (minutes < 10) category = "0-10分钟";
                else if (minutes < 30) category = "10-30分钟";
                else if (minutes < 60) category = "30-60分钟";
                else category = "超过60分钟";

                totalCounts.put(category, totalCounts.getOrDefault(category, 0L) + 1);
                if (Boolean.TRUE.equals(answer.getAccepted())) {
                    acceptedCounts.put(category, acceptedCounts.getOrDefault(category, 0L) + 1);
                }
            }
        });

        Map<String, Double> acceptanceRates = new HashMap<>();
        for (String category : totalCounts.keySet()) {
            long total = totalCounts.get(category);
            long accepted = acceptedCounts.getOrDefault(category, 0L);
            double rate = total > 0 ? (double) accepted / total : 0.0;
            acceptanceRates.put(category, rate);
        }

        return acceptanceRates;
    }


    public Map<String, Double> analyzeUserReputationAcceptanceRate() {
        Map<Long, Integer> userReputations = userRepository.findAll().stream()
                .collect(Collectors.toMap(
                        User::getExternalUserId,
                        User::getReputation,
                        (existing, replacement) -> Math.max(existing, replacement)));

        Map<String, Long> totalCounts = new HashMap<>();
        Map<String, Long> acceptedCounts = new HashMap<>();

        answerRepository.findAll().forEach(answer -> {
            Long userId = answer.getUserId();
            if (userId != null) {
                Integer reputation = userReputations.get(userId);
                String category;
                if (reputation == null) {
                    category = "未知声誉";
                } else if (reputation < 100) {
                    category = "低声誉 (<100)";
                } else if (reputation < 500) {
                    category = "中声誉 (100-499)";
                } else {
                    category = "高声誉 (≥500)";
                }

                totalCounts.put(category, totalCounts.getOrDefault(category, 0L) + 1);
                if (Boolean.TRUE.equals(answer.getAccepted())) {
                    acceptedCounts.put(category, acceptedCounts.getOrDefault(category, 0L) + 1);
                }
            }
        });

        return calculateAcceptanceRate(totalCounts, acceptedCounts);
    }

    private Map<String, Double> calculateAcceptanceRate(Map<String, Long> totalCounts, Map<String, Long> acceptedCounts) {
        Map<String, Double> acceptanceRates = new HashMap<>();
        for (String category : totalCounts.keySet()) {
            long total = totalCounts.get(category);
            long accepted = acceptedCounts.getOrDefault(category, 0L);
            double rate = total > 0 ? (double) accepted / total : 0.0;
            acceptanceRates.put(category, rate);
        }
        return acceptanceRates;
    }

    // 分析内容长度对答案质量的影响
    public Map<String, Double> analyzeContentLengthAcceptanceRate() {
        Map<String, Long> totalCounts = new HashMap<>();
        Map<String, Long> acceptedCounts = new HashMap<>();

        answerRepository.findAll().forEach(answer -> {
            int length = answer.getContentLength();
            String category;
            if (length < 100) {
                category = "短答案 (<100字)";
            } else if (length < 300) {
                category = "中等长度 (100-299字)";
            } else {
                category = "长答案 (≥300字)";
            }

            totalCounts.put(category, totalCounts.getOrDefault(category, 0L) + 1);
            if (Boolean.TRUE.equals(answer.getAccepted())) {
                acceptedCounts.put(category, acceptedCounts.getOrDefault(category, 0L) + 1);
            }
        });

        return calculateAcceptanceRate(totalCounts, acceptedCounts);
    }


    // 获取评论数量对答案质量的影响
    public Map<String, Double> analyzeCommentCountAcceptanceRate() {
        Map<String, Long> totalCounts = new HashMap<>();
        Map<String, Long> acceptedCounts = new HashMap<>();

        answerRepository.findAll().forEach(answer -> {
            Long commentCount = getCommentCountForAnswer(answer.getExternalAnswerId());
            String category;
            if (commentCount == 0) category = "无评论";
            else if (commentCount <= 2) category = "少量评论 (1-2)";
            else if (commentCount <= 5) category = "中等评论 (3-5)";
            else category = "大量评论 (>5)";

            totalCounts.put(category, totalCounts.getOrDefault(category, 0L) + 1);
            if (Boolean.TRUE.equals(answer.getAccepted())) {
                acceptedCounts.put(category, acceptedCounts.getOrDefault(category, 0L) + 1);
            }
        });

        return calculateAcceptanceRate(totalCounts, acceptedCounts);
    }


    // 获取答案得分对答案质量的影响
    public Map<String, Double> analyzeScoreAcceptanceRate() {
        Map<String, Long> totalCounts = new HashMap<>();
        Map<String, Long> acceptedCounts = new HashMap<>();

        answerRepository.findAll().forEach(answer -> {
            Integer score = answer.getScore();
            String category;
            if (score == null) category = "未评分";
            else if (score < 0) category = "负评分 (<0)";
            else if (score == 0) category = "中性评分 (0)";
            else if (score <= 10) category = "低评分 (1-10)";
            else category = "高评分 (>10)";

            totalCounts.put(category, totalCounts.getOrDefault(category, 0L) + 1);
            if (Boolean.TRUE.equals(answer.getAccepted())) {
                acceptedCounts.put(category, acceptedCounts.getOrDefault(category, 0L) + 1);
            }
        });

        return calculateAcceptanceRate(totalCounts, acceptedCounts);
    }


    public Map<String, Map<String, Double>> getAllImpactData() {
        Map<String, Map<String, Double>> allData = new HashMap<>();
        allData.put("时间间隔", analyzeTimeIntervalAcceptanceRate());
        allData.put("用户声誉", analyzeUserReputationAcceptanceRate());
        allData.put("答案长度", analyzeContentLengthAcceptanceRate());
        allData.put("答案得分", analyzeScoreAcceptanceRate());
        allData.put("评论数量", analyzeCommentCountAcceptanceRate());
        return allData;
    }
}
