package repositories;

import annotations.Repository;
import models.Transaction;

/**
 * Transaction repository. Used to retrieve transactions from database and create java instances
 * @see models.Account
 * @author Antoine FORET
 * @version 1.0
 */
@Repository(model = Transaction.class)
public class TransactionRepository extends RepositoryBase {
}
