package Transaction;
import java.util.List;

import Operation.Operation;
import Operation.OperationFactory;

import java.util.ArrayList;
import java.util.HashSet;

public class Transaction {
    private int id;
    private List<Operation> operations;
    private HashSet<Integer> recordIds;

    public Transaction(int id) {
        this.id = id;
        this.operations = new ArrayList<Operation>();
        this.recordIds = new HashSet<Integer>();
    }

    public boolean addOperation(Operation op) {
        this.operations.add(op);
        op.setTransactionId(this.id);
        if (op.getRecordId() != null) {
            this.recordIds.add(op.getRecordId());
        }
        return true;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashSet<Integer> getRecordIds() {
        return this.recordIds;
    }

    public void setRecordIds(HashSet<Integer> recordIds) {
        this.recordIds = recordIds;
    }

    public List<Operation> getOperations() {
        return this.operations;
    }

    public Operation getOperation(int index) throws IndexOutOfBoundsException {
        return this.operations.get(index);
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public String toString() {
        if (operations.size() == 0) {
            return String.format("T%d: no operations", id);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("T%d: ", id));
        for (int i = 0; i < operations.size(); i++) {
            stringBuilder.append(operations.get(i).toString(false));
            if (i != operations.size() - 1) {
                stringBuilder.append("; ");
            }
        }
        return stringBuilder.toString();
    }

    public static Transaction parseTransaction(String s) {
        String[] parts = s.split(":");
        if (parts.length != 2) {
            System.err.println(String.format("Error occurred when parsing transaction. Invalid transaction: %s", s));
            return null;
        }

        if (!parts[0].matches("T[0-9]+")) {
            System.err.println(
                    String.format("Error occurred when parsing transaction. Invalid transaction id: %s", parts[0]));
            return null;
        }

        int id;
        try {
            id = Integer.parseInt(parts[0].substring(1));
        } catch (NumberFormatException exception) {
            return null;
        }
        Transaction result = new Transaction(id);

        String[] operationStrings = parts[1].split(";");
        for (String operationString : operationStrings) {
            operationString = operationString.trim();
            if (operationString.length() == 0) {
                continue;
            }

            Operation operation = OperationFactory.createOperation(operationString);
            if (operation == null) {
                System.err.println(String.format("Error occurred when parsing transaction T%d. Invalid operation: %s",
                        id, operationString));
                return null;
            }
            result.addOperation(operation);
        }

        return result;
    }
}
