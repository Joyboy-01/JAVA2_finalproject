package CS209A.project.demo.crawler;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("crawler")
public class StackExchangeAPI {
    private final WebClient webClient;
    private static final Logger logger = Logger.getLogger(StackExchangeAPI.class.getName());

    // 在这里你可以使用 API Key 进行请求
    private static final String API_KEY = "rl_2FaCveFp4VUfzsdDDLjs9qyE1"; // 你的 API Key

    public StackExchangeAPI(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.stackexchange.com/2.3").build();
    }

    public String fetchQuestions(int page) {
        String url = "/questions?order=desc&sort=activity&tagged=java&site=stackoverflow&pagesize=100&page=" + page + "&key=" + API_KEY;
        return makeApiCall(url);
    }

    public String fetchAnswersForQuestion(Long questionId) {
        String url = "/questions/" + questionId + "/answers?order=desc&sort=activity&site=stackoverflow&filter=withbody&key=" + API_KEY;
        return makeApiCall(url);
    }

    public String fetchCommentsForQuestion(Long questionId) {
        String url = "/questions/" + questionId + "/comments?order=desc&sort=creation&site=stackoverflow&filter=withbody&key=" + API_KEY;
        return makeApiCall(url);
    }

    public String fetchVotesForQuestion(Long questionId) {
        String url = "/questions/" + questionId + "/answers?order=desc&sort=creation&site=stackoverflow&key=" + API_KEY;
        return makeApiCall(url);
    }

    public String fetchVotes(Long postId) {
        String url = "/answers/" + postId + "?site=stackoverflow&key=" + API_KEY;
        return makeApiCall(url);
    }

    public String fetchUser(Long userId) {
        String url = "/users/" + userId + "?site=stackoverflow&key=" + API_KEY;
        return makeApiCall(url);
    }

    public String makeApiCall(String url) {
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(error -> Mono.error(new RuntimeException("API request failed: " + error))))
                    .bodyToMono(String.class)
                    .defaultIfEmpty("No content") // 如果响应体为空，返回默认值
                    .timeout(Duration.ofSeconds(10))
                    .onErrorResume(e -> {
                        logError(e, url);
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            logError(e, url);
            return null;
        }
    }

    // 增强的错误日志记录
    private void logError(Throwable e, String url) {
        if (e instanceof WebClientResponseException we) {
            logger.log(Level.SEVERE, "API call failed for URL: " + url + " with status: "
                    + we.getRawStatusCode() + " and message: " + we.getResponseBodyAsString());
        } else {
            logger.log(Level.SEVERE, "API call failed for URL: " + url + " with error: " + e.getMessage(), e);
        }
    }
}
