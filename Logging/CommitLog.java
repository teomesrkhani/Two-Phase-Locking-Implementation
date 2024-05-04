package Logging;

public class CommitLog extends Log {
    // In case of a Commit, store the following information:
    // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
    // - The Transaction Id
    // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
    public CommitLog(int previousTimestamp, int currentTimestamp, int transactionId) {
        super(previousTimestamp, currentTimestamp, transactionId);
    }

    @Override
    public String toString() {
        String transactionIdString = String.format("T%d", transactionId);
        return String.format("C: %4d, %4s, %4d", currentTimestamp, transactionIdString,
                    previousTimestamp);
    }
}
