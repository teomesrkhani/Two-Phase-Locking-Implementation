package Operation;

public abstract class Operation {
    protected Integer recordId;
    protected Integer transactionId;

    public Operation() {
        this.recordId = null;
        this.transactionId = null;
    }

    public Operation(Integer recordId) {
        this.recordId = recordId;
        this.transactionId = null;
    }

    public Operation(Integer recordId, Integer transactionId) {
        this.recordId = recordId;
        this.transactionId = transactionId;
    }

    public Integer getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public static Operation parseOperation(String s) {
        throw new UnsupportedOperationException("parseOperation not implemented");
    }

    public abstract String toString(boolean showTransactionId);
}
