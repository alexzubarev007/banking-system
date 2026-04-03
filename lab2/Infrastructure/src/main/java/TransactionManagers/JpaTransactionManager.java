package TransactionManagers;

import TransactionManager.TransactionManager;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaTransactionManager implements TransactionManager {
    private final EntityManager entityManager;
    @Override
    public void begin() {
        entityManager.getTransaction().begin();
    }

    @Override
    public void commit() {
        entityManager.getTransaction().commit();
    }

    @Override
    public void rollback() {
        entityManager.getTransaction().rollback();
    }
}