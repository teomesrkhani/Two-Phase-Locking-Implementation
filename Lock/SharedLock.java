package Lock;

import java.util.HashSet;

public class SharedLock extends Lock {
    private HashSet<Integer> transactionIds;

    public SharedLock(int recordId) {
        super(recordId);
        this.transactionIds = new HashSet<Integer>();
    }

    public HashSet<Integer> getTransactionIds() {
        return this.transactionIds;
    }

    public void setTransactionIds(HashSet<Integer> transactionIds) {
        this.transactionIds = transactionIds;
    }

    public boolean addTransactionId(int transactionId) {
        return transactionIds.add(transactionId);
    }

    public boolean removeTransactionId(int transactionId) {
        return transactionIds.remove(transactionId);
    }

    @Override
    public boolean isOwnedByTransaction(int transactionId) {
        return transactionIds.contains(transactionId);
    }
}
