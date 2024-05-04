package Operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitOperation extends Operation {
    public static final Pattern COMMIT_OPERATION_WITH_TRANSACTION_ID_PATTERN = Pattern.compile("T([0-9]+):C");

    public CommitOperation() {
        super();
    }

    public CommitOperation(int transactionId) {
        super();
        this.transactionId = transactionId;
    }

    public static CommitOperation parseOperation(String s) {
        s = s.trim();

        if (s.equals("C")){
            return new CommitOperation();
        }

        Matcher matcher = COMMIT_OPERATION_WITH_TRANSACTION_ID_PATTERN.matcher(s);
        if (matcher.find()) {
            return new CommitOperation(Integer.parseInt(matcher.group(1)));
        }

        return null;
    }

    
    public String toString(boolean showTransactionId) {
        if (transactionId != null && showTransactionId) {
            return String.format("T%d:C", transactionId);
        }
        return "C";
    }

    @Override
    public String toString() {
        if (transactionId != null) {
            return String.format("T%d:C", transactionId);
        }
        return "C";
    }
}
