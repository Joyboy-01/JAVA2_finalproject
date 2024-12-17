package CS209A.project.demo.service;

import CS209A.project.demo.entity.Answer;
import CS209A.project.demo.entity.Comment;
import CS209A.project.demo.entity.Question;
import CS209A.project.demo.repository.AnswerRepository;
import CS209A.project.demo.repository.CommentRepository;
import CS209A.project.demo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ErrorAnalysisService {

    private static final String ERROR_REGEX = "\\b\\w+Error\\b";
    private static final String EXCEPTION_REGEX = "\\b\\w+Exception\\b";// 匹配以 Exception 或 Error 结尾的文本

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CommentRepository commentRepository;

    // 统计每种错误或异常的数量
    public Map<String, Integer> countErrorsByType(@RequestParam(defaultValue = "5") int topN) {
        // 获取所有答案、问题和评论
        List<Answer> answers = answerRepository.findAll();
        List<Question> questions = questionRepository.findAll();
        List<Comment> comments = commentRepository.findAll();

        // 用于存储每种错误类型及其出现次数
        Map<String, Integer> errorCounts = new HashMap<>();
        Pattern pattern = Pattern.compile(ERROR_REGEX, Pattern.CASE_INSENSITIVE);

        // 统计Answer的content中的错误
        answers.forEach(answer -> countErrorTypes(answer.getContent(), pattern, errorCounts));

        // 统计Question的title中的错误
        questions.forEach(question -> countErrorTypes(question.getTitle(), pattern, errorCounts));

        // 统计Comment的response中的错误
        comments.forEach(comment -> countErrorTypes(comment.getContent(), pattern, errorCounts));

        return errorCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // 按照出现次数降序排序
                .limit(topN) // 取前N个
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<String, Integer> countExceptionByType(@RequestParam(defaultValue = "5") int topN) {
        // 获取所有答案、问题和评论
        List<Answer> answers = answerRepository.findAll();
        List<Question> questions = questionRepository.findAll();
        List<Comment> comments = commentRepository.findAll();

        // 用于存储每种错误类型及其出现次数
        Map<String, Integer> errorCounts = new HashMap<>();
        Pattern pattern = Pattern.compile(EXCEPTION_REGEX, Pattern.CASE_INSENSITIVE);

        // 统计Answer的content中的错误
        answers.forEach(answer -> countErrorTypes(answer.getContent(), pattern, errorCounts));

        // 统计Question的title中的错误
        questions.forEach(question -> countErrorTypes(question.getTitle(), pattern, errorCounts));

        // 统计Comment的response中的错误
        comments.forEach(comment -> countErrorTypes(comment.getContent(), pattern, errorCounts));

        return errorCounts.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // 按照出现次数降序排序
                .limit(topN) // 取前N个
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<String, Integer> countErrorByName(@RequestParam String errorName) {
        // 获取所有答案、问题和评论
        List<Answer> answers = answerRepository.findAll();
        List<Question> questions = questionRepository.findAll();
        List<Comment> comments = commentRepository.findAll();

        // 用于存储每种错误类型及其出现次数
        Map<String, Integer> errorCounts = new HashMap<>();
        Pattern pattern = Pattern.compile(ERROR_REGEX, Pattern.CASE_INSENSITIVE);

        // 统计Answer的content中的错误
        answers.forEach(answer -> countErrorTypes(answer.getContent(), pattern, errorCounts));

        // 统计Question的title中的错误
        questions.forEach(question -> countErrorTypes(question.getTitle(), pattern, errorCounts));

        // 统计Comment的response中的错误
        comments.forEach(comment -> countErrorTypes(comment.getContent(), pattern, errorCounts));

        // 返回指定错误类型的出现次数，如果没有找到则返回0
        Map<String, Integer> result = new HashMap<>();
        result.put(errorName, errorCounts.getOrDefault(errorName, 0));

        return result;
    }


    public Map<String, Integer> countExceptionByName(@RequestParam String errorName) {
        // 获取所有答案、问题和评论
        List<Answer> answers = answerRepository.findAll();
        List<Question> questions = questionRepository.findAll();
        List<Comment> comments = commentRepository.findAll();

        // 用于存储每种错误类型及其出现次数
        Map<String, Integer> errorCounts = new HashMap<>();
        Pattern pattern = Pattern.compile(EXCEPTION_REGEX, Pattern.CASE_INSENSITIVE);

        // 统计Answer的content中的错误
        answers.forEach(answer -> countErrorTypes(answer.getContent(), pattern, errorCounts));

        // 统计Question的title中的错误
        questions.forEach(question -> countErrorTypes(question.getTitle(), pattern, errorCounts));

        // 统计Comment的response中的错误
        comments.forEach(comment -> countErrorTypes(comment.getContent(), pattern, errorCounts));

        // 返回指定错误类型的出现次数，如果没有找到则返回0
        Map<String, Integer> result = new HashMap<>();
        result.put(errorName, errorCounts.getOrDefault(errorName, 0));

        return result;
    }




    // 判断文本中是否包含错误，并更新错误计数
    private void countErrorTypes(String text, Pattern pattern, Map<String, Integer> errorCounts) {
        if (text == null || text.isEmpty()) {
            return;
        }

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            // 获取异常类型（例如 NullPointerException, IOException）
            String errorType = matcher.group(0);
            // 更新错误类型的计数
            errorCounts.put(errorType, errorCounts.getOrDefault(errorType, 0) + 1);
        }
    }
}
