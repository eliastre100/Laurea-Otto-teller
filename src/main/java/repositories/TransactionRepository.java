package repositories;

import annotations.Repository;
import models.Transaction;

@Repository(model = Transaction.class)
public class TransactionRepository extends RepositoryBase{
}
