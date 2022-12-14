package at.ac.uibk.swa.util;

import java.util.UUID;

public class UUIDConversionUtil {
    public static UUID tryConvertUUID(String maybeToken) {
        try {
            return UUID.fromString(maybeToken);
        } catch (Exception e) {
            return null;
        }
    }
}
