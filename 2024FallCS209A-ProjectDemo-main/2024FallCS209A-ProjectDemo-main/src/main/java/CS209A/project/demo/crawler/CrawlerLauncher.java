package CS209A.project.demo.crawler;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
@SpringBootApplication
@ComponentScan(basePackages = "CS209A.project.demo")
public class CrawlerLauncher implements CommandLineRunner {

    private final StackOverflowCrawler stackOverflowCrawler;
    private final JdbcTemplate jdbcTemplate;  // 用 JdbcTemplate 来执行 SQL 操作

    @Autowired
    public CrawlerLauncher(StackOverflowCrawler stackOverflowCrawler, JdbcTemplate jdbcTemplate) {
        this.stackOverflowCrawler = stackOverflowCrawler;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional  // 确保删除操作和爬取操作在同一事务中
    public void run(String... args) throws Exception {
        // 清空数据库中的相关数据
        clearDatabase();

        // 开始爬取数据
        stackOverflowCrawler.collectData();
    }

    // 删除数据库中的所有表的数据
    private void clearDatabase() {
        // 清空每个表的数据
        deleteFromTable("answers");
        deleteFromTable("apilogs");
        deleteFromTable("comments");
        deleteFromTable("errors");
        deleteFromTable("question_tags");
        deleteFromTable("questions");
        deleteFromTable("stack_overflow_thread");
        deleteFromTable("users");
        deleteFromTable("votes");
        // 你可以根据实际需要继续添加其他表格的删除操作...

        // 删除完所有表的数据后输出日志
        System.out.println("所有表数据已被删除！");
    }

    // 通用的删除表数据的方法
    private void deleteFromTable(String tableName) {
        String sql = "DELETE FROM " + tableName;
        jdbcTemplate.execute(sql);  // 执行删除操作
    }

    public static void main(String[] args) {
        SpringApplication.run(CrawlerLauncher.class, args);
    }
}