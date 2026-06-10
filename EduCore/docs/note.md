When should you use @Transactional?

Use it when a method performs database operations that should be treated as a single unit of work.

Example:

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public void createBook(Book book) {
        bookRepository.save(book);
    }
}

Or:

@Transactional
public void transferMoney() {
withdraw();
deposit();
}

If one step fails, you want everything rolled back.


For pure business logic that doesn't need a transaction:

@Service
public class DiscountService {

    public double calculateDiscount(double price) {
        return price * 0.9;
    }
}

No database work, no transaction needed.