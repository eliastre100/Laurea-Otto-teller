package repositories;

import annotations.Repository;
import models.Account;

/**
 * Account repository. Used to retrieve accounts from database and create java instances
 * @see models.Account
 * @author Antoine FORET
 * @version 1.0
 */
@Repository(model = Account.class)
public class AccountRepository extends RepositoryBase {
}
