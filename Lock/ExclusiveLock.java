package Lock;

public class ExclusiveLock extends Lock {
    public int transactionId;

    public ExclusiveLock(int transactionId, int recordId) {
        super(recordId);
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean isOwnedByTransaction(int transactionId) {
        return this.transactionId == transactionId;
    }
}
