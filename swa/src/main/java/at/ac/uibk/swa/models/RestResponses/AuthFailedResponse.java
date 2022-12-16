package at.ac.uibk.swa.models.RestResponses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class AuthFailedResponse extends RestResponse implements Serializable {

    private String message;
}
