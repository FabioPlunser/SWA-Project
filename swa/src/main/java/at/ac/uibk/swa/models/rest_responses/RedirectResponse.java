package at.ac.uibk.swa.models.rest_responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpHeaders;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.MODULE)
public class RedirectResponse extends RestResponse {

    @NonNull
    @JsonIgnore
    private String redirectLocation;

    public RedirectResponse(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    @Override
    public RestResponseEntity toEntity() {
        if (this.redirectLocation != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", redirectLocation);
            return new RestResponseEntity(this, headers);
        } else {
            return super.toEntity();
        }
    }
}
