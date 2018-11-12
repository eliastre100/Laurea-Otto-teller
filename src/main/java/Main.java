import models.Account;
import repositories.AccountRepository;

import java.util.List;

public class Main {

    public static void main(String[] attr) {
        AccountRepository repository = new AccountRepository();
        Account account = (Account) repository.findAll().get(0);

        System.out.println(account);
        account.balance += 10;
        account.save();
        System.out.println(account);
    }

}
