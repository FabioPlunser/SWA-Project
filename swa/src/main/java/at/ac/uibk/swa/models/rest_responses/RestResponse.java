package at.ac.uibk.swa.models.rest_responses;

import at.ac.uibk.swa.util.SerializationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;

/**
 * Base Class for sending back JSON-Data from REST-Endpoints.
 * Contains a "success"-Field so the Front-End can determine
 * if an Operation was completed successfully.
 *
 * @author davirieser
 * @version 1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.MODULE)
public class RestResponse implements Serializable {

    public RestResponse(boolean success) {
        this(success, success ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    public RestResponse(boolean success, HttpStatus status) {
        this(success, status.value());
    }

    public RestResponse(boolean success, HttpStatusCode httpStatusCode) {
        this(success, httpStatusCode.value());
    }

    /**
     * Indicates the Success-State of an Operation to the Front-End.
     */
    @Setter(AccessLevel.PROTECTED)
    private boolean success;

    @JsonIgnore
    @Setter(AccessLevel.PROTECTED)
    private int statusCode;

    public int getStatusCode() {
        return this.statusCode;
    }

    @SuppressWarnings("unused")
    public String toResponse() throws JsonProcessingException {
        return SerializationUtil.serializeJSON(this);
    }
}
