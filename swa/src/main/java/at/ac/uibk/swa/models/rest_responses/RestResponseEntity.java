package at.ac.uibk.swa.models.rest_responses;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Getter
public class RestResponseEntity extends ResponseEntity<RestResponse> {
    public RestResponseEntity(@NonNull RestResponse body) {
        super(body, body.getStatusCode());
        super.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }
}
