package Lock;

public abstract class Lock {
    protected int recordId;

    public Lock() {
    }

    public Lock(int recordId) {
        this.recordId = recordId;
    }

    public int getRecordId() {
        return this.recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public abstract boolean isOwnedByTransaction(int transactionId);
}
