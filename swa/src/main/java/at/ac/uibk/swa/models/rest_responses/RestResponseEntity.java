package at.ac.uibk.swa.models.rest_responses;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

@Getter
public class RestResponseEntity extends ResponseEntity<RestResponse> {
    public RestResponseEntity(@NonNull RestResponse body) {
        super(body, body.getStatusCode());
    }

    public RestResponseEntity(@Nullable RestResponse body, @Nullable MultiValueMap<String, String> headers) {
        super(body, headers, body.getStatusCode());
    }
}
