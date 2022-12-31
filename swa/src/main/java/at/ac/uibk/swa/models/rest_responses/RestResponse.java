package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.util.SerializationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;

/**
 * Base Class for sending back JSON-Data from REST-Endpoints.
 * Contains a "success"-Field so the Front-End can determine
 * if an Operation was completed successfully.
 * Also contains a "statusCode"-Field which the {@link org.springframework.http.ResponseEntity}
 * can use to set custom Error Codes.
 *
 * @author davirieser
 * @version 1.1
 */
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.MODULE)
public abstract class RestResponse implements Serializable {

    //region Constructors
    public RestResponse(boolean success) {
        this(success, success ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    public RestResponse(boolean success, int statusCode) {
        this(success, HttpStatusCode.valueOf(statusCode));
    }

    public RestResponse(boolean success, HttpStatus status) {
        this(success, status.value());
    }
    //endregion

    /**
     * Indicates the Success-State of an Operation to the Front-End.
     */
    @Setter(AccessLevel.PROTECTED)
    private boolean success = false;

    //region Status Code
    @JsonIgnore
    @Builder.Default
    private HttpStatusCode statusCode = HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value());

    public static HttpStatusCode successToStatusCode(boolean success) {
        return HttpStatusCode.valueOf((success ? HttpStatus.OK : HttpStatus.NO_CONTENT).value());
    }
    //endregion

    //region Response Conversions
    @SuppressWarnings("unused")
    public String toResponse() {
        return SerializationUtil.serializeJSON(this);
    }

    public RestResponseEntity toEntity() {
        return new RestResponseEntity(this);
    }
    //endregion

    //region Builder Customization
    public abstract static class RestResponseBuilder<
            C extends RestResponse,
            B extends RestResponseBuilder<C, B>>
    {
        public RestResponseBuilder success(boolean success) {
            this.success = success;

            // If the Status Code was not set yet, set it using the success-Flag.
            if (this.statusCode$set) this.statusCode(successToStatusCode(success));

            return this;
        }

        public RestResponseEntity toEntity() {
            return new RestResponseEntity(this.build());
        }
    }
    //endregion
}
