package CS209A.project.demo.service;

import CS209A.project.demo.repository.ThreadRepository;
import CS209A.project.demo.model.StackOverflowThread;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DataService {
    private final WebClient webClient;  // 使用 WebClient
    private final ThreadRepository threadRepository;

    public DataService(ThreadRepository threadRepository, WebClient webClient) {
        this.threadRepository = threadRepository;
        this.webClient = webClient;
    }

    @Transactional
    public void collectData() {
        ObjectMapper objectMapper = new ObjectMapper();
        String url = "https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&tagged=java&site=stackoverflow&pagesize=100";

        try {
            for (int page = 1; page <= 10; page++) { // Fetch 1000 threads data
                Thread.sleep(1000); // Request interval 1 second
                String pagedUrl = url + "&page=" + page;

                // Use WebClient to make the OAuth2 request
                String response = webClient.get()
                        .uri(pagedUrl)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();  // block() 用于同步等待响应

                System.out.println("Response: " + response); // Check response

                // Handle empty or invalid response
                if (response == null || response.isEmpty()) {
                    System.out.println("No data received for page: " + page);
                    continue;
                }

                JsonNode itemsNode = objectMapper.readTree(response).path("items");

                if (itemsNode.isEmpty()) {
                    System.out.println("No items found on page: " + page);
                    continue;
                }

                // Process threads data
                for (JsonNode item : itemsNode) {
                    StackOverflowThread thread = new StackOverflowThread();
                    thread.setTitle(item.path("title").asText());
                    thread.setBody(item.path("body").asText("")); // Default to empty string
                    thread.setTags(String.join(",", new ObjectMapper().convertValue(item.path("tags"), List.class)));
                    thread.setVoteCount(item.path("score").asInt());
                    thread.setAnswerCount(item.path("answer_count").asInt());
                    thread.setCreatedAt(item.path("creation_date").asText());

                    // Save thread to database
                    threadRepository.save(thread);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
