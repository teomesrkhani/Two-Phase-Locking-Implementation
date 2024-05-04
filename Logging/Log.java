package Logging;

public abstract class Log {
    protected int currentTimestamp;
    protected int previousTimestamp;
    protected int transactionId;

    public int getCurrentTimestamp() {
        return this.currentTimestamp;
    }

    public void setCurrentTimestamp(int currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public int getPreviousTimestamp() {
        return this.previousTimestamp;
    }

    public void setPreviousTimestamp(int previousTimestamp) {
        this.previousTimestamp = previousTimestamp;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Log(int previousTimestamp, int currentTimestamp, int transactionId) {
        this.previousTimestamp = previousTimestamp;
        this.currentTimestamp = currentTimestamp;
        this.transactionId = transactionId;
    }
}
