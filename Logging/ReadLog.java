package Logging;

public class ReadLog extends Log {
    private int recordId;
    private int value;

    // In case of a Read task, store the following information:
    // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
    // - The Transaction Id
    // - The Record Id
    // - The value read from that record
    // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
    public ReadLog(int previousTimestamp, int currentTimestamp, int transactionId, int recordId, int value) {
        super(previousTimestamp, currentTimestamp, transactionId);
        this.recordId = recordId;
        this.value = value;
    }

    public int getRecordId() {
        return this.recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    @Override
    public String toString() {
        String transactionIdString = String.format("T%d", transactionId);
        return String.format("R: %4d, %4s, %4d, %4d, %4d", currentTimestamp, transactionIdString,
                recordId, value, previousTimestamp);
    }
}
