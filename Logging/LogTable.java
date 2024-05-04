package Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Operation.AbortOperation;
import Operation.CommitOperation;
import Operation.Operation;
import Operation.ReadOperation;
import Operation.WriteOperation;

public class LogTable {
    List<Operation> schedule;
    Map<Integer, Integer> recordValueMap;
    Map<Integer, Integer> transactionTimestampMap;
    List<Log> logEntries;

    public LogTable() {
        schedule = new ArrayList<Operation>();
        recordValueMap = new HashMap<Integer, Integer>();
        transactionTimestampMap = new HashMap<Integer, Integer>();
        logEntries = new ArrayList<Log>();
    }

    public void addOperation(Operation operation, int transactionId) {
        schedule.add(operation);

        if (operation instanceof WriteOperation) {
            // In case of a Write task, store the following information:
            // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
            // - The Transaction Id
            // - The Record Id
            // - The old value stored in that record
            // - The new value stored in that record
            // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
            WriteOperation op = (WriteOperation) operation;

            int recordId = op.getRecordId();
            int currentTimestamp = logEntries.size();
            logEntries.add(new WriteLog(transactionTimestampMap.getOrDefault(transactionId, -1), currentTimestamp, transactionId, recordId, recordValueMap.getOrDefault(recordId, recordId), op.getValue()));
            
            recordValueMap.put(recordId, op.getValue());
            transactionTimestampMap.put(transactionId, currentTimestamp);
        } else if (operation instanceof ReadOperation) {
            // In case of a Read task, store the following information:
            // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
            // - The Transaction Id
            // - The Record Id
            // - The value read from that record
            // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
            int currentTimestamp = logEntries.size();
            int recordId = operation.getRecordId();
            logEntries.add(new ReadLog(transactionTimestampMap.getOrDefault(transactionId, -1), currentTimestamp, transactionId, recordId, recordValueMap.getOrDefault(recordId, recordId)));
            
            transactionTimestampMap.put(transactionId, currentTimestamp);
        } else if (operation instanceof CommitOperation) {
            // In case of a Commit, store the following information:
            // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
            // - The Transaction Id
            // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
            int currentTimestamp = logEntries.size();
            logEntries.add(new CommitLog(transactionTimestampMap.getOrDefault(transactionId, -1), currentTimestamp, transactionId));
            transactionTimestampMap.put(transactionId, currentTimestamp);
        } else if (operation instanceof AbortOperation) {
            // In case of an Abort, store the following information:
            // - Timestamp; can be a simple counter starting from 0 and incremented by 1 for each log entry
            // - The Transaction Id
            // - The timestamp of the previous log entry for this transaction (to be used for Roll-back)
            int currentTimestamp = logEntries.size();
            logEntries.add(new AbortLog(transactionTimestampMap.getOrDefault(transactionId, -1), currentTimestamp, transactionId));
            transactionTimestampMap.put(transactionId, currentTimestamp);

            // Perform roll-back
            for (Log log : logEntries) {
                if (log.getTransactionId() == transactionId && log instanceof WriteLog) {
                    WriteLog writeLog = (WriteLog) log;
                    recordValueMap.put(writeLog.getRecordId(), writeLog.getPrevValue());
                }
            }

            transactionTimestampMap.put(transactionId, currentTimestamp);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Log log : logEntries) {
            stringBuilder.append(log);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
