package at.ac.uibk.swa.util;

public class StringGenerator {
    private final static String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String SMALL_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private final static String NUMBERS = "0123456789";

    public static String base(String alphabet, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(Math.random() * alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

    public static String email() {
        int lengthLocalPart = 20;
        int lengthDomainPart = 8;
        String localPart = StringGenerator.base(SMALL_LETTERS + CAPITAL_LETTERS + NUMBERS, lengthLocalPart);
        String domainPart = StringGenerator.base(SMALL_LETTERS, lengthDomainPart) + ".com";
        return  localPart + "@" + domainPart;
    }

    public static String password() {
        return StringGenerator.base(SMALL_LETTERS + CAPITAL_LETTERS + NUMBERS, 16);
    }
}
