package CS209A.project.demo.crawler;

import CS209A.project.demo.entity.*;
import CS209A.project.demo.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
@Profile("crawler")
@Component
public class StackOverflowCrawler {

    private final StackExchangeAPI stackExchangeAPI;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;
    private final QuestionTagRepository questionTagRepository;
    private final CommentRepository commentRepository;
    private final APILogRepository apiLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_PAGES = 10;

    @Autowired
    public StackOverflowCrawler(StackExchangeAPI stackExchangeAPI,
                                UserRepository userRepository,
                                QuestionRepository questionRepository,
                                AnswerRepository answerRepository,
                                VoteRepository voteRepository,
                                QuestionTagRepository questionTagRepository,
                                CommentRepository commentRepository,
                                APILogRepository apiLogRepository) {
        this.stackExchangeAPI = stackExchangeAPI;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
        this.questionTagRepository = questionTagRepository;
        this.commentRepository = commentRepository;
        this.apiLogRepository = apiLogRepository;
    }

    @PostConstruct
    @Transactional
    public void collectData() {
        try {
            for (int page = 1; page <= MAX_PAGES; page++) {
                // 获取问题数据
                String questionsResponse = stackExchangeAPI.fetchQuestions(page);
                logApiCall("fetchQuestions", questionsResponse);
                processQuestions(questionsResponse);

                // 控制爬取速度，避免被限流
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, null);
        }
    }

    private void processQuestions(String response) throws Exception {
        if (response == null || response.isEmpty()) {
            System.out.println("Received empty or null response.");
            return; // 退出方法
        }
        JsonNode root = objectMapper.readTree(response);
        JsonNode items = root.path("items");
        if (items.isArray()) {
            Iterator<JsonNode> iterator = items.elements();
            while (iterator.hasNext()) {
                JsonNode questionNode = iterator.next();
                processSingleQuestion(questionNode);
            }
        }
    }

    private void processSingleQuestion(JsonNode questionNode) {
        try {
            // 解析问题基本信息
            Long questionId = questionNode.path("question_id").asLong();
            String title = questionNode.path("title").asText();
            String content = questionNode.path("body").asText(""); // 确保有默认值
            long creationDateEpoch = questionNode.path("creation_date").asLong();
            LocalDateTime creationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationDateEpoch), ZoneId.systemDefault());
            Long userId = null;

            // 解析问题作者信息
            JsonNode ownerNode = questionNode.path("owner");
            if (ownerNode.has("user_id")) {
                userId = ownerNode.path("user_id").asLong();
                String userResponse = stackExchangeAPI.fetchUser(userId);
                logApiCall("fetchUser", userResponse);
                processUser(userResponse);
            }

            // 保存问题信息

            Question question = questionRepository.findById(questionId).orElse(new Question());
//            question.setQuestionId(questionId);
            question.setExternalQuestionId(questionId);
            question.setTitle(title);
            question.setContent(content);
            question.setCreationDate(creationDate);
            question.setUserId(userId);
            questionRepository.save(question);

            // 解析并保存标签信息
            JsonNode tagsNode = questionNode.path("tags");
            if (tagsNode.isArray()) {
                for (JsonNode tag : tagsNode) {
                    QuestionTag.QuestionTagId tagId = new QuestionTag.QuestionTagId();
                    tagId.setQuestionId(questionId);
                    tagId.setTag(tag.asText());

                    QuestionTag questionTag = questionTagRepository.findById(tagId).orElse(new QuestionTag());
                    questionTag.setId(tagId);
                    questionTagRepository.save(questionTag);
                }
            }

            // 解析并保存答案
            String answersResponse = stackExchangeAPI.fetchAnswersForQuestion(questionId);
            logApiCall("fetchAnswersForQuestion", answersResponse);
            processAnswers(questionId, answersResponse);

            // 解析并保存评论
            String commentsResponse = stackExchangeAPI.fetchCommentsForQuestion(questionId);
            logApiCall("fetchCommentsForQuestion", commentsResponse);
            processComments(questionId, commentsResponse);

            // 解析并保存投票
            String votesResponse = stackExchangeAPI.fetchVotesForQuestion(questionId);
            logApiCall("fetchVotesForQuestion", votesResponse);
            processVotes(questionId, votesResponse);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, questionNode);
        }
    }

    private void processAnswers(Long questionId, String response) throws Exception {
        if (response == null || response.isEmpty()) {
            System.out.println("Received empty or null response.");
            return; // 退出方法
        }
        JsonNode root = objectMapper.readTree(response);
        JsonNode items = root.path("items");
        if (items.isArray()) {
            Iterator<JsonNode> iterator = items.elements();
            while (iterator.hasNext()) {
                JsonNode answerNode = iterator.next();
                processSingleAnswer(questionId, answerNode);
            }
        }
    }

    private void processSingleAnswer(Long questionId, JsonNode answerNode) {
        try {
            Long answerId = answerNode.path("answer_id").asLong();
            String content = answerNode.path("body").asText("");
            long creationDateEpoch = answerNode.path("creation_date").asLong();
            LocalDateTime creationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationDateEpoch), ZoneId.systemDefault());
            Long userId = null;
            Boolean isAccepted = answerNode.path("is_accepted").asBoolean(false);
            int score = answerNode.path("score").asInt(0);

            // 解析回答者信息
            JsonNode ownerNode = answerNode.path("owner");
            if (ownerNode.has("user_id")) {
                userId = ownerNode.path("user_id").asLong();
                String userResponse = stackExchangeAPI.fetchUser(userId);
                logApiCall("fetchUser", userResponse);
                processUser(userResponse);
            }

            // 保存回答信息
//            System.out.println(answerId);
//            System.out.println(questionId);
            Answer answer = answerRepository.findById(answerId).orElse(new Answer());
//            answer.setAnswerId(answerId);
            answer.setExternalAnswerId(answerId);
            answer.setQuestionId(questionId);
            answer.setContent(content);
            answer.setCreationDate(creationDate);
            answer.setUserId(userId);
            answer.setAccepted(isAccepted);
            answer.setScore(score);
            answerRepository.save(answer);

            // 解析并保存投票信息
            String votesResponse = stackExchangeAPI.fetchVotes(answerId);
            logApiCall("fetchVotes", votesResponse);
            processVotes(answerId, votesResponse);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, answerNode);
        }
    }

    private void processComments(Long postId, String response) throws Exception {
        if (response == null || response.isEmpty()) {
            System.out.println("Received empty or null response.");
            return; // 退出方法
        }
        JsonNode root = objectMapper.readTree(response);
        JsonNode items = root.path("items");
        if (items.isArray()) {
            Iterator<JsonNode> iterator = items.elements();
            while (iterator.hasNext()) {
                JsonNode commentNode = iterator.next();
                processSingleComment(postId, commentNode);
            }
        }
    }

    private void processSingleComment(Long postId, JsonNode commentNode) {
        try {
            Long commentId = commentNode.path("comment_id").asLong();
            String content = commentNode.path("body").asText("");
            long creationDateEpoch = commentNode.path("creation_date").asLong();
            LocalDateTime creationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(creationDateEpoch), ZoneId.systemDefault());
            Long userId = null;

            // 解析评论者信息
            JsonNode ownerNode = commentNode.path("owner");
            if (ownerNode.has("user_id")) {
                userId = ownerNode.path("user_id").asLong();
                String userResponse = stackExchangeAPI.fetchUser(userId);
                logApiCall("fetchUser", userResponse);
                processUser(userResponse);
            }

            // 保存评论信息
            Comment comment = commentRepository.findById(commentId).orElse(new Comment());
            comment.setCommentId(commentId);
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(content);
            comment.setCreationDate(creationDate);
            commentRepository.save(comment);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, commentNode);
        }
    }

    private void processVotes(Long postId, String response) throws Exception {
        if (response == null || response.isEmpty()) {
            System.out.println("Received empty or null response.");
            return; // 退出方法
        }
        JsonNode root = objectMapper.readTree(response);
        JsonNode items = root.path("items");
        if (items.isArray()) {
            Iterator<JsonNode> iterator = items.elements();
            while (iterator.hasNext()) {
                JsonNode voteNode = iterator.next();
                processSingleVote(postId, voteNode);
            }
        }
    }

    private void processSingleVote(Long postId, JsonNode voteNode) {
        try {
            Long voteId = voteNode.path("vote_id").asLong();
            String voteType = voteNode.path("vote_type").asText("");
            long voteDateEpoch = voteNode.path("creation_date").asLong();
            LocalDateTime voteDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(voteDateEpoch), ZoneId.systemDefault());
            Long userId = null;

            // 解析投票者信息（如果有）
            JsonNode userNode = voteNode.path("user");
            if (userNode.has("user_id")) {
                userId = userNode.path("user_id").asLong();
                String userResponse = stackExchangeAPI.fetchUser(userId);

                if (userResponse != null) {
                    logApiCall("fetchUser", userResponse);
                    processUser(userResponse);
                }
            }

            // 保存投票信息
            Vote vote = voteRepository.findById(voteId).orElse(new Vote());
            vote.setVoteId(voteId);
            vote.setPostId(postId);
            vote.setUserId(userId);
            vote.setVoteType(voteType);
            vote.setVoteDate(voteDate);
            voteRepository.save(vote);

            // 记录 API 调用日志
            processAPILog(userId, "Processed Vote for Post ID: " + postId);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, voteNode);
        }
    }
    private void processUser(String response) {
        if (response == null || response.isEmpty()) {
            // 处理空响应的情况
            System.out.println("Userrrrrrrr null response.");
            System.out.println("User: " + response);

            return; // 退出方法
        }
        try {
            JsonNode root = objectMapper.readTree(response);

            JsonNode items = root.path("items");
            if (items.isArray() && items.size() > 0) {
                JsonNode userNode = items.get(0);
                Long userId = userNode.path("user_id").asLong();
                String username = userNode.path("display_name").asText("");
                int reputation = userNode.path("reputation").asInt(0);
                long userCreationDateEpoch = userNode.path("creation_date").asLong();
                LocalDateTime userCreationDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(userCreationDateEpoch), ZoneId.systemDefault());

                // 保存用户信息
                User user = userRepository.findById(userId).orElse(new User());
                user.setUserId(userId);
                user.setUsername(username);
                user.setReputation(reputation);
                user.setCreationDate(userCreationDate);
                userRepository.save(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, null);
        }
    }

    private void processAPILog(Long userId, String action) {
        try {
            APILog apiLog = new APILog();
            apiLog.setUserId(userId);
            apiLog.setAction(action);
            apiLog.setTimestamp(LocalDateTime.now());
            apiLogRepository.save(apiLog);
        } catch (Exception e) {
            e.printStackTrace();
            // 记录错误日志
            handleError(e, null);
        }
    }

    private void logApiCall(String action, String response) {
        try {
            APILog apiLog = new APILog();
            apiLog.setAction(action);
            apiLog.setResponse(response);
            apiLog.setTimestamp(LocalDateTime.now());
            apiLogRepository.save(apiLog);
        } catch (Exception e) {
            e.printStackTrace();
            handleError(e, null);
        }
    }

    private void handleError(Exception e, JsonNode node) {
        e.printStackTrace();
        System.err.println(e);
    }
}
