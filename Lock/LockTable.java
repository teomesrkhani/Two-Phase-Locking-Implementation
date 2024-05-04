package Lock;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import Operation.Operation;
import Operation.ReadOperation;
import Operation.WriteOperation;

import java.util.List;

public class LockTable {
    Map<Integer, Lock> recordLockMap; // Keep track of the lock that is on a record
    SortedSet<Integer> recordIds;

    public LockTable() {
        this.recordLockMap = new HashMap<Integer, Lock>();
        this.recordIds = new TreeSet<Integer>();
    }

    public LockTable(Set<Integer> recordIds) {
        this.recordLockMap = new HashMap<Integer, Lock>();
        this.recordIds = new TreeSet<Integer>(recordIds);
    }

    public Lock getLockOnRecord(int recordId) {
        return recordLockMap.get(recordId);
    }

    public boolean isSharedLockOnRecord(int recordId) {
        Lock lock = recordLockMap.get(recordId);
        return lock != null && lock instanceof SharedLock;
    }

    public boolean isExclusiveLockOnRecord(int recordId) {
        Lock lock = recordLockMap.get(recordId);
        return lock != null && lock instanceof ExclusiveLock;
    }

    public boolean isRecordLockedByTransaction(int transactionId, int recordId) {
        Lock lock = recordLockMap.get(recordId);
        return lock != null && lock.isOwnedByTransaction(transactionId);
    }

    public List<Lock> getTransactionLocks(int transactionId) {
        List<Lock> transactionLocks = new ArrayList<Lock>();
        for (Lock lock : recordLockMap.values()) {
            if (lock.isOwnedByTransaction(transactionId)) {
                transactionLocks.add(lock);
            }
        }
        return transactionLocks;
    }

    public void releaseTransactionLocks(int transactionId) {
        for (Lock lock : getTransactionLocks(transactionId)) {
            if (lock instanceof SharedLock) {
                SharedLock sharedLock = (SharedLock) lock;
                sharedLock.removeTransactionId(transactionId);
                if (sharedLock.getTransactionIds().size() == 0) {
                    recordLockMap.remove(sharedLock.recordId);
                }
            } else if (lock instanceof ExclusiveLock) {
                recordLockMap.remove(lock.recordId);
            }
        }
    }

    public Lock makeLock(int transactionId, Operation operation) {
        if (operation instanceof ReadOperation) {
            Lock lock = recordLockMap.get(operation.getRecordId());
            if (lock != null && lock instanceof ExclusiveLock) {
                ExclusiveLock exclusiveLock = (ExclusiveLock) lock;
                if (exclusiveLock.getTransactionId() != transactionId) {
                    return null; // Cannot read if there is an exclusive lock already that is not owned by the transaction
                } else {
                    return exclusiveLock; // Can read because the transaction owns the exclusive lock on the record
                }
            }

            return makeSharedLock(transactionId, operation.getRecordId());
        } else if (operation instanceof WriteOperation) {
            return makeExclusiveLock(transactionId, operation.getRecordId());
        } 
        return null;
    }

    private Lock makeSharedLock(int transactionId, int recordId) {
        recordIds.add(recordId);
        Lock lock = recordLockMap.get(recordId);
        if (lock == null) { // No lock on record, can make a shared lock
            SharedLock sharedLock = new SharedLock(recordId);
            sharedLock.addTransactionId(transactionId);
            recordLockMap.put(recordId, sharedLock);
            return sharedLock;
        } else if (lock != null && lock instanceof SharedLock) {
            // There is a shared lock on the record, add the current transaction as an owner of the lock
            SharedLock sharedLock = (SharedLock) lock;
            sharedLock.addTransactionId(transactionId);
            return sharedLock;
        }
        return null;
    }

    private Lock makeExclusiveLock(int transactionId, int recordId) {
        recordIds.add(recordId);
        Lock lock = recordLockMap.get(recordId);
        if (lock == null) { // No lock on record, can make an exclusive lock
            ExclusiveLock exclusiveLock = new ExclusiveLock(transactionId, recordId);
            recordLockMap.put(recordId, exclusiveLock);
            return exclusiveLock;
        } else if (lock != null && lock instanceof SharedLock) {
            SharedLock sharedLock = (SharedLock) lock;
            // A shared lock is on the record, but the only owner is the current transaction
            // We can upgrade the lock to an exclusive lock 
            if (sharedLock.getTransactionIds().size() == 1 && sharedLock.isOwnedByTransaction(transactionId)) {
                ExclusiveLock exclusiveLock = new ExclusiveLock(transactionId, recordId);
                recordLockMap.put(recordId, exclusiveLock);
                return exclusiveLock;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-".repeat(54) + "\n");
        stringBuilder.append(String.format("|%10s|%20s|%20s|\n", "Record ID", "Lock Type", "Transaction ID(s)"));
        for (int recordId : recordIds) {
            Lock lock = recordLockMap.get(recordId);

            String lockType = "None";
            String transactionIdsString = "";
            if (lock instanceof SharedLock) {
                lockType = "Shared Lock";
                transactionIdsString = ((SharedLock) lock).getTransactionIds().stream().map(id -> String.valueOf(id))
                        .collect(Collectors.joining(", "));
            } else if (lock instanceof ExclusiveLock) {
                lockType = "Exclusive Lock";
                transactionIdsString = Integer.toString(((ExclusiveLock) lock).transactionId);
            }

            stringBuilder.append(String.format("|%10s|%20s|%20s|\n", recordId, lockType, transactionIdsString));
        }
        stringBuilder.append("-".repeat(54) + "\n");
        return stringBuilder.toString();
    }
}
