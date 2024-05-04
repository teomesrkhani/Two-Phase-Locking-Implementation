package Operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadOperation extends Operation {
    public static final Pattern READ_OPERATION_PATTERN = Pattern.compile("R\\(([0-9]+)\\)");
    public static final Pattern READ_OPERATION_WITH_TRANSACTION_ID_PATTERN = Pattern.compile("T([0-9]+):R\\(([0-9]+)\\)");

    public ReadOperation(int recordId) {
        super(recordId);
    }

    public ReadOperation(int recordId, int transactionId) {
        super(recordId, transactionId);
    }

    public static ReadOperation parseOperation(String s) {
        s = s.trim();

        Matcher matcher = READ_OPERATION_WITH_TRANSACTION_ID_PATTERN.matcher(s);
        if (matcher.find()) {
            return new ReadOperation(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }

        matcher = READ_OPERATION_PATTERN.matcher(s);
        if (matcher.find()) {
            return new ReadOperation(Integer.parseInt(matcher.group(1)));
        }

        return null;
    }

    public String toString(boolean showTransactionId) {
        if (transactionId != null && showTransactionId) {
            return String.format("T%d:R(%d)", transactionId, recordId);
        }
        return String.format("R(%d)", recordId);
    }

    @Override
    public String toString() {
        if (transactionId != null) {
            return String.format("T%d:R(%d)", transactionId, recordId);
        }
        return String.format("R(%d)", recordId);
    }
}
