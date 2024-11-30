package CS209A.project.demo;

import CS209A.project.demo.service.DataService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

	private final DataService dataService;

	public DemoApplication(DataService dataService) {
		this.dataService = dataService;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// 收集 Stack Overflow 数据并存储到数据库
//		dataService.collectData();
	}
}
