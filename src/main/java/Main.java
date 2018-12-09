import models.Account;
import models.Transaction;
import repositories.AccountRepository;
import repositories.TransactionRepository;

import java.util.List;

public class Main {

    public static void main(String[] attr) {
        AccountRepository accountRepository = new AccountRepository();
        List<Account> accounts = accountRepository.findAll();
        System.out.println(accounts);

        /*TransactionRepository repositoy = new TransactionRepository();

        Transaction transaction = repositoy.find(1);
        transaction.destroy();*/
    }
}
