package CS209A.project.demo.controller;

import CS209A.project.demo.entity.User;
import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.entity.Comment;
import CS209A.project.demo.entity.QuestionTag;
import CS209A.project.demo.entity.Vote;
import CS209A.project.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/P2")
public class P2Controller {
    private final UserService userService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final VoteService voteService;
    private final QuestionTagService questionTagService;

    @Autowired
    public P2Controller(UserService userService, AnswerService answerService, CommentService commentService, VoteService voteService, QuestionTagService questionTagService) {
        this.userService = userService;
        this.answerService = answerService;
        this.commentService = commentService;
        this.voteService = voteService;
        this.questionTagService = questionTagService;
    }

    @GetMapping("/getTopParticipatedTopics")
    public Map<String, Integer> getTopParticipatedTopics() {
        int topN = 5;

        //获取高声誉用户
        List<User> highReputationUsers = userService.getHighReputationUsers(10000);

        // 获取所有高声誉用户的活动
        List<Answer> highReputationAnswers = answerService.getHighReputationAnswers(highReputationUsers);
        List<Comment> highReputationComments = commentService.getHighReputationComments(highReputationUsers);
        List<Vote> highReputationVotes = voteService.getHighReputationVotes(highReputationUsers);

        // 获取每个问题的标签
        List<QuestionTag> questionTags = questionTagService.getQuestionTags();

        // 创建一个映射，统计每个话题的参与数量
        Map<String, Integer> topicParticipationCount = new HashMap<>();

        // 处理高声誉用户的回答
        for (Answer answer : highReputationAnswers) {
            Long questionId = answer.getQuestionId();
            String answerContent = answer.getContent();

            // 获取该问题的标签（话题）
            List<String> tags = questionTags.stream()
                    .filter(tag -> tag.getId().getQuestionId().equals(questionId))
                    .map(QuestionTag::getId)
                    .map(QuestionTag.QuestionTagId::getTag)
                    .collect(Collectors.toList());

            // 统计标签的参与
            for (String tag : tags) {
                topicParticipationCount.put(tag, topicParticipationCount.getOrDefault(tag, 0) + 1);
            }
        }

        // 处理高声誉用户的评论
        for (Comment comment : highReputationComments) {
            Long postId = comment.getPostId();

            // 假设评论关联到问题或答案的标签
            List<String> tags = questionTags.stream()
                    .filter(tag -> tag.getId().getQuestionId().equals(postId))  // 如果postId是问题ID
                    .map(QuestionTag::getId)
                    .map(QuestionTag.QuestionTagId::getTag)
                    .collect(Collectors.toList());

            for (String tag : tags) {
                topicParticipationCount.put(tag, topicParticipationCount.getOrDefault(tag, 0) + 1);
            }
        }

        // 处理高声誉用户的投票
        for (Vote vote : highReputationVotes) {
            Long postId = vote.getPostId();

            // 假设投票也与问题或答案的标签相关
            List<String> tags = questionTags.stream()
                    .filter(tag -> tag.getId().getQuestionId().equals(postId))  // 如果postId是问题ID
                    .map(QuestionTag::getId)
                    .map(QuestionTag.QuestionTagId::getTag)
                    .collect(Collectors.toList());

            for (String tag : tags) {
                topicParticipationCount.put(tag, topicParticipationCount.getOrDefault(tag, 0) + 1);
            }
        }
        return topicParticipationCount.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("java"))  // 过滤掉键为 "java" 的项
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  // 获取键
                        Map.Entry::getValue,  //值是参与度
                        (e1, e2) -> e1,  // 如果有重复的键，选择第一个
                        LinkedHashMap::new  // 保持插入顺序
                ));
    }

}
