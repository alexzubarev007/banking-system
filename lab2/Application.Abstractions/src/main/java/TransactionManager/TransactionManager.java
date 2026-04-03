package TransactionManager;

public interface TransactionManager {
    void begin();
    void commit();
    void rollback();
}