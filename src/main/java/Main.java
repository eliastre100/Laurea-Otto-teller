import models.Account;
import repositories.AccountRepository;

import java.util.List;

public class Main {

    public static void main(String[] attr) {
        AccountRepository repository = new AccountRepository();
        List<Account> cars = repository.findAll();
    }

}
