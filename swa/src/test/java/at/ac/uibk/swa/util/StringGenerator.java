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
        int length_local_part = 20;
        int length_domain_part = 8;
        String local_part = StringGenerator.base(SMALL_LETTERS + CAPITAL_LETTERS + NUMBERS, length_local_part);
        String domain_part = StringGenerator.base(SMALL_LETTERS, length_domain_part) + ".com";
        return  local_part + "@" + domain_part;
    }

    public static String password() {
        return StringGenerator.base(SMALL_LETTERS + CAPITAL_LETTERS + NUMBERS, 16);
    }
}
