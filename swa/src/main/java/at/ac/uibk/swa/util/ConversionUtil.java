package at.ac.uibk.swa.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

/**
 * Helper Class for Conversion Method that should have slightly modified behaviour.
 *
 * @author David Rieser
 */
// All your Constructors are belong to us!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConversionUtil {

    /**
     * Helper Method for converting a String into a UUID without throwing an Exception.
     *
     * @param maybeToken The String that should be converted into a Token.
     * @return The Token if it could be converted, null otherwise.
     */
    public static UUID tryConvertUUID(String maybeToken) {
        try {
            return UUID.fromString(maybeToken);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Helper Method for converting a String into a UUID without throwing an Exception.
     *
     * @param maybeToken The String that should be converted into a Token.
     * @return The Token if it could be converted, an empty Optional otherwise.
     */
    public static Optional<UUID> tryConvertUUIDOptional(String maybeToken) {
        return Optional.ofNullable(tryConvertUUID(maybeToken));
    }
}
