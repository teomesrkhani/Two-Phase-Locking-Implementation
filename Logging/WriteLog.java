package Logging;

public class WriteLog extends Log {
    private int recordId;
    private int prevValue;
    private int newValue;

    // In case of a Write task, store the following information:
    // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
    // - The Transaction Id
    // - The Record Id
    // - The old value stored in that record
    // - The new value stored in that record
    // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
    public WriteLog(int previousTimestamp, int currentTimestamp, int transactionId, int recordId, int prevValue, int newValue) {
        super(previousTimestamp, currentTimestamp, transactionId);
        this.recordId = recordId;
        this.prevValue = prevValue;
        this.newValue = newValue;
    }

    public int getRecordId() {
        return this.recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getPrevValue() {
        return this.prevValue;
    }

    public void setPrevValue(int oldValue) {
        this.prevValue = oldValue;
    }

    public int getNewValue() {
        return this.newValue;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        String transactionIdString = String.format("T%d", transactionId);
        return String.format("W: %4d, %4s, %4d, %4d, %4d, %4d", currentTimestamp, transactionIdString,
                recordId, prevValue, newValue, previousTimestamp);
    }
}
