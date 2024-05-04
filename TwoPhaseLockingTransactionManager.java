import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Lock.ExclusiveLock;
import Lock.Lock;
import Lock.LockTable;
import Lock.SharedLock;
import Lock.WaitForGraph;
import Logging.LogTable;
import Operation.AbortOperation;
import Operation.CommitOperation;
import Operation.Operation;
import Operation.ReadOperation;
import Operation.WriteOperation;
import Transaction.Transaction;

public class TwoPhaseLockingTransactionManager {
    public static ArrayList<Operation> roundRobinScheduling(List<Transaction> transactions) {
        HashSet<Integer> recordIds = new HashSet<Integer>();
        for (Transaction transaction : transactions) {
            recordIds.addAll(transaction.getRecordIds());
        }
        LockTable lockTable = new LockTable(recordIds);
        LogTable logTable = new LogTable();
        WaitForGraph waitForGraph = new WaitForGraph();

        int numCommitted = 0;
        // Keep track of whether the transaction is committed
        Map<Integer, Boolean> transactionCommitStatusMap = new HashMap<Integer, Boolean>();
        // Keep track of which operation we are on for each transaction
        Map<Integer, Integer> transactionOperationIndexMap = new HashMap<Integer, Integer>();

        // Transactions are processed using the “Round-Robin” method
        int currentRound = 1;
        ArrayList<Operation> schedule = new ArrayList<Operation>();
        while (numCommitted < transactions.size()) { // Keep going until all transactions have been committed
            System.out.print(String.format("(Round %d) ->", currentRound));

            for (Transaction transaction : transactions) {
                int transactionId = transaction.getId();

                // Ignore transactions that are already committed
                if (transactionCommitStatusMap.getOrDefault(transactionId, false)) {
                    continue;
                }

                // Find the next operation for the transaction
                int operationIndex = transactionOperationIndexMap.getOrDefault(transactionId, 0);
                Operation operation = null;
                try {
                    operation = transaction.getOperation(operationIndex);
                } catch (IndexOutOfBoundsException exception) {
                    // The transaction has no operations left, but it's not committed.
                    // We add the missing commit operation.
                    operation = new CommitOperation(transactionId);
                }

                // Check if operation can be executed and then execute it
                if (operation instanceof ReadOperation || operation instanceof WriteOperation) {
                    if (lockTable.isRecordLockedByTransaction(transactionId, operation.getRecordId())
                            || lockTable.makeLock(transactionId, operation) != null) {
                        schedule.add(operation);
                        logTable.addOperation(operation, transactionId);
                        transactionOperationIndexMap.put(transactionId, operationIndex + 1);
                        System.out.print(String.format(" %s;", operation));
                    } else { // Operation cannot be executed, might have a deadlock
                        Lock lock = lockTable.getLockOnRecord(operation.getRecordId());
                        Set<Integer> lockOwners = new HashSet<Integer>();
                        if (lock instanceof SharedLock) {
                            lockOwners.addAll(((SharedLock)lock).getTransactionIds());
                        } else if (lock instanceof ExclusiveLock) {
                            lockOwners.add(((ExclusiveLock)lock).getTransactionId());
                        }

                        for (int ownerId: lockOwners) {
                            waitForGraph.addEdge(transactionId, ownerId);
                        }
                        
                        Set<Integer> cycle = waitForGraph.findCycle(); 
                        while(cycle.size() > 0) { // Resolve deadlock
                            ArrayList<Integer> sortedTransactionIds = new ArrayList<Integer>(cycle);
                            sortedTransactionIds.sort((a, b) -> -a.compareTo(b));

                            // Abort lowest priority transaction
                            int lowestPriorityTransactionId = sortedTransactionIds.get(0);
                            logTable.addOperation(new AbortOperation(lowestPriorityTransactionId), lowestPriorityTransactionId);
                            lockTable.releaseTransactionLocks(lowestPriorityTransactionId);
                            
                            transactionOperationIndexMap.put(lowestPriorityTransactionId, 0);
                            schedule.removeAll(schedule.stream().filter(op -> op.getTransactionId() == lowestPriorityTransactionId).toList());

                            waitForGraph.removeTransactionEdges(lowestPriorityTransactionId);
                            cycle = waitForGraph.findCycle(); 
                        }
                    }
                } else {
                    transactionCommitStatusMap.put(transactionId, true);
                    lockTable.releaseTransactionLocks(transactionId);
                    numCommitted += 1;
                    schedule.add(operation);
                    logTable.addOperation(operation, transactionId);
                    System.out.print(String.format(" %s;", operation));
                }
            }

            System.out.println();
            System.out.println(logTable);
            System.out.println(lockTable);
            currentRound += 1;
        }

        return schedule;
    }

    public static void printSchedule(ArrayList<Operation> schedule) {
        for (Operation operation : schedule) {
            System.out.print(String.format(" %s;", operation));
        }
        System.out.println();
    }

    public static List<Transaction> loadTransactions(String filePath) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = Transaction.parseTransaction(line);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static void main(String[] args) {
        List<Transaction> transactions = loadTransactions("sample_transaction.txt");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
        System.out.println();

        ArrayList<Operation> schedule = roundRobinScheduling(transactions);
        System.out.print("Complete schedule: ");
        printSchedule(schedule);
    }
}
