package Operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbortOperation extends Operation {
    public static final Pattern ABORT_OPERATION_WITH_TRANSACTION_ID_PATTERN = Pattern.compile("T([0-9]+):A");

    public AbortOperation() {
        super();
    }

    public AbortOperation(int transactionId) {
        super();
        this.transactionId = transactionId;
    }

    public static AbortOperation parseOperation(String s) {
        s = s.trim();

        if (s.equals("A")){
            return new AbortOperation();
        }

        Matcher matcher = ABORT_OPERATION_WITH_TRANSACTION_ID_PATTERN.matcher(s);
        if (matcher.find()) {
            return new AbortOperation(Integer.parseInt(matcher.group(1)));
        }

        return null;
    }

    
    public String toString(boolean showTransactionId) {
        if (transactionId != null && showTransactionId) {
            return String.format("T%d:A", transactionId);
        }
        return "A";
    }

    @Override
    public String toString() {
        if (transactionId != null) {
            return String.format("T%d:A", transactionId);
        }
        return "A";
    }
}
