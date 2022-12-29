package at.ac.uibk.swa.models.rest_responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

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
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class RestResponse implements Serializable {

    /**
     * Indicates the Success-State of an Operation to the Front-End.
     */
    @Setter(AccessLevel.PROTECTED)
    private boolean success = false;

    @SuppressWarnings("unused")
    public String toResponse() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
