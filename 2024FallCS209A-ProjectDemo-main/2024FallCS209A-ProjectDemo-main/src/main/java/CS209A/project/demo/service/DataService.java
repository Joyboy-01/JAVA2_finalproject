package CS209A.project.demo.service;

import CS209A.project.demo.repository.ThreadRepository;
import CS209A.project.demo.model.StackOverflowThread;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DataService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ThreadRepository threadRepository;

    public DataService(ThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public void collectData() {
        String url = "https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&tagged=java&site=stackoverflow&pagesize=100";
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            for (int page = 1; page <= 10; page++) { // 收集 1000 个线程的数据，每页 100 个
                String pagedUrl = url + "&page=" + page;
                String response = restTemplate.getForObject(pagedUrl, String.class);
                JsonNode itemsNode = objectMapper.readTree(response).path("items");

                for (JsonNode item : itemsNode) {
                    StackOverflowThread thread = new StackOverflowThread();
                    thread.setTitle(item.path("title").asText());
                    thread.setBody(item.path("body").asText(""));
                    thread.setTags(String.join(",", new ObjectMapper().convertValue(item.path("tags"), List.class)));
                    thread.setVoteCount(item.path("score").asInt());
                    thread.setAnswerCount(item.path("answer_count").asInt());
                    thread.setCreatedAt(item.path("creation_date").asText());

                    threadRepository.save(thread);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Long> analyzeTopicFrequency(int limit) {
        return threadRepository.findAll().stream()
                .flatMap(thread -> Arrays.stream(thread.getTags().split(",")))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<Map<String, Object>> getMostVotedThreads(int limit) {
        return threadRepository.findAll().stream()
                .sorted((t1, t2) -> Integer.compare(t2.getVoteCount(), t1.getVoteCount()))
                .limit(limit)
                .map(thread -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", thread.getTitle());
                    map.put("voteCount", thread.getVoteCount());
                    map.put("answerCount", thread.getAnswerCount());
                    return map;
                })
                .collect(Collectors.toList());
    }
}