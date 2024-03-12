package book.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book fairyTale = new Book();
            fairyTale.setId(1L);
            fairyTale.setAuthor("Shevchenko");

            bookService.save(fairyTale);

            System.out.println(bookService.findAll());
        };
    }
}
