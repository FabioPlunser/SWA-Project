package at.ac.uibk.swa.models.restResponses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Response Container for sending a List of items fetched on the server side.
 * Only if the success-Field is set to true then a List is sent.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
public class ListResponse<T extends Serializable> extends RestResponse implements Serializable {

    /**
     * Tell the Front-End that the received Response is a List.
     */
    private static final String type = "List";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> items;

    /**
     * Indicate a successful fetch of items.
     * If items is null, an empty list will be sent to prevent problems on the Front-End.
     *
     * @param items The Items to send with the request.
     */
    public ListResponse(List<T> items) {
        super(true);

        // Ensure that a List is always sent.
        this.items = Optional.ofNullable(items).orElse(new ArrayList<>());
    }
}
