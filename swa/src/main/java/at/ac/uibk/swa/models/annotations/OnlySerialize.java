package at.ac.uibk.swa.models.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JsonProperty(access = JsonProperty.Access.READ_ONLY)
public @interface OnlySerialize {
}
