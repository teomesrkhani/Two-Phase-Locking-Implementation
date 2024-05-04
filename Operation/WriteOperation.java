package Operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteOperation extends Operation {
    public static final Pattern WRITE_OPERATION_PATTERN = Pattern.compile("W\\(([0-9]+),[ ]*([0-9]+)\\)");
    public static final Pattern WRITE_OPERATION_WITH_TRANSACTION_ID_PATTERN = Pattern.compile("T([0-9]+):W\\(([0-9]+),[ ]*([0-9]+)\\)");

    private int value;

    public WriteOperation(int recordId, int value) {
        super(recordId);
        this.value = value;
    }

    public WriteOperation(int recordId, int value, int transactionId) {
        super(recordId, transactionId);
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static WriteOperation parseOperation(String s) {
        s = s.trim();

        Matcher matcher = WRITE_OPERATION_WITH_TRANSACTION_ID_PATTERN.matcher(s);
        if (matcher.find()) {
            return new WriteOperation(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(1)));
        }

        matcher = WRITE_OPERATION_PATTERN.matcher(s);
        if (matcher.find()) {
            return new WriteOperation(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }

        return null;
    }

    public String toString(boolean showTransactionId) {
        if (transactionId != null && showTransactionId) {
            return String.format("T%d:W(%d,%d)", transactionId, recordId, value);
        }
        return String.format("W(%d,%d)", recordId, value);
    }

    @Override
    public String toString() {
        if (transactionId != null) {
            return String.format("T%d:W(%d,%d)", transactionId, recordId, value);
        }
        return String.format("W(%d,%d)", recordId, value);
    }
}
