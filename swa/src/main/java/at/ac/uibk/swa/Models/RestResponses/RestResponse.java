package at.ac.uibk.swa.Models.RestResponses;

import lombok.Getter;

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
public class RestResponse implements Serializable {

    /**
     * Indicates the Success-State of an Operation to the Front-End.
     */
    private boolean success;

    public RestResponse(boolean successful) {
        this.success = successful;
    }
}
