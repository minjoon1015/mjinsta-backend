	package back_end.springboot;

	import org.springframework.amqp.rabbit.core.RabbitTemplate;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.boot.ApplicationRunner;
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.context.annotation.Bean;

	@ImportAutoConfiguration({
		org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class
	})
	
	@SpringBootApplication
	public class SpringbootApplication {
		
		@Autowired(required = false) 
		private RabbitTemplate rabbitTemplate; 

		public static void main(String[] args) {
			SpringApplication.run(SpringbootApplication.class, args);
		}
		
		@Bean
		public ApplicationRunner rabbitConnectionTester() {
			return args -> {
				if (rabbitTemplate != null) {
					try {
						rabbitTemplate.execute(channel -> {
							return null; // 실행 후 반환할 값은 없음
						});
					} catch (Exception e) {
						
					}
				} else {
					
				}
			};
		}
	}