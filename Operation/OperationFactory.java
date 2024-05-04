package Operation;

public class OperationFactory {
    public static Operation createOperation(String s) {
        Operation op = ReadOperation.parseOperation(s);
        if (op != null) {
            return op;
        } 

        op = WriteOperation.parseOperation(s);
        if (op != null) {
            return op;
        }

        op = CommitOperation.parseOperation(s);
        if (op != null) {
            return op;
        }

        op = AbortOperation.parseOperation(s);
        if (op != null) {
            return op;
        }

        System.err.println(String.format("Error occurred when parsing operations. Invalid operation: %s", s));
        return null;
    }
}
