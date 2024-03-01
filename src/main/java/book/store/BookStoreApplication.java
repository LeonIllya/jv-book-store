package book.store;

import book.store.model.Book;
import book.store.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

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
