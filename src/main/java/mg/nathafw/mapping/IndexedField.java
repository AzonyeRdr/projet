package mg.nathafw.mapping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexedField {

    private static final Pattern INDEXED_FIELD_PATTERN = Pattern.compile("^(.+?)\\[(\\d+)](?:\\.(.+))?$");
    public final String prefix;
    public final int index;
    final String fieldName;

    public IndexedField(String prefix, int index, String fieldName) {
        this.prefix = prefix;
        this.index = index;
        this.fieldName = fieldName;
    }

    public static IndexedField parseIndexedField(String parameterName) {
        int openBracket = parameterName.indexOf('[');
        int closeBracket = parameterName.indexOf(']', openBracket + 1);
        if (openBracket <= 0 || closeBracket <= openBracket + 1) {
            return null;
        }

        String prefix = parameterName.substring(0, openBracket);
        String indexValue = parameterName.substring(openBracket + 1, closeBracket);
        String fieldName = null;

        if (closeBracket + 1 < parameterName.length() && parameterName.charAt(closeBracket + 1) == '.') {
            fieldName = parameterName.substring(closeBracket + 2);
        }

        if (prefix.isEmpty() || indexValue.isEmpty()) {
            return null;
        }

        try {
            return new IndexedField(prefix, Integer.parseInt(indexValue), fieldName);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}