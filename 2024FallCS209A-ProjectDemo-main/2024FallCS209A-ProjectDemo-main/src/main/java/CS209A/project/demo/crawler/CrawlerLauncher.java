package CS209A.project.demo.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@ComponentScan(basePackages = "CS209A.project.demo")
@Profile("crawler")
public class CrawlerLauncher implements CommandLineRunner {

    private final StackOverflowCrawler stackOverflowCrawler;

    @Autowired
    public CrawlerLauncher(StackOverflowCrawler stackOverflowCrawler) {
        this.stackOverflowCrawler = stackOverflowCrawler;
    }

    @Override
    public void run(String... args) throws Exception {
        stackOverflowCrawler.collectData();
    }

    public static void main(String[] args) {
        SpringApplication.run(CrawlerLauncher.class, args);
    }
}
