package at.ac.uibk.swa.Models.RestResponses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseBuilder implements Serializable {

    public final static boolean DEFAULT_SUCCESS = false;

    private final boolean success;

    private List<KvpTuple> kvp = new ArrayList<>();

    private ResponseBuilder(boolean success) {
        this.success = success;
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder(DEFAULT_SUCCESS);
    }

    public static ResponseBuilder builder(boolean success) {
        return new ResponseBuilder(success);
    }

    public ResponseBuilder addMessage(String message) {
        this.kvp.add(new KvpTuple("message", message));
        return this;
    }

    @JsonComponent
    public static class ResponseSerializer {
        public static class Serialize extends JsonSerializer<ResponseBuilder> {
            @Override
            public void serialize(
                    ResponseBuilder response, JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider
            ) throws IOException {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeBooleanField("success", response.success);

                for (KvpTuple kvp: response.kvp) {
                    // TODO: ((int) 0) == null ?
                    if (kvp.value == null) {
                        jsonGenerator.writeNullField(kvp.key);
                        continue;
                    }
                    switch (kvp.type) {
                        case Boolean -> jsonGenerator.writeBooleanField(kvp.key, (boolean) kvp.value);
                        case Int -> jsonGenerator.writeNumberField(kvp.key, (int) kvp.value);
                        case Long -> jsonGenerator.writeNumberField(kvp.key, (long) kvp.value);
                        case Float -> jsonGenerator.writeNumberField(kvp.key, (float) kvp.value);
                        case Double -> jsonGenerator.writeNumberField(kvp.key, (double) kvp.value);
                        case Array -> {
                            jsonGenerator.writeStartArray(kvp.key);
                            switch (kvp.innerType) {
                                case Int -> jsonGenerator.writeArray((int[]) kvp.value, 0, ((int[]) kvp.value).length);
                                case Long -> jsonGenerator.writeArray((long[]) kvp.value, 0, ((long[]) kvp.value).length);
                                case Double -> jsonGenerator.writeArray((double[]) kvp.value, 0, ((double[]) kvp.value).length);
                                case String -> jsonGenerator.writeArray((String[]) kvp.value, 0, ((String[]) kvp.value).length);
                                default -> throw new JsonMappingException("Unknown JSON-Field-Type!");
                            }
                            jsonGenerator.writeEndArray();
                        }
                        case String -> jsonGenerator.writeStringField(kvp.key, (String) kvp.value);
                        default -> throw new JsonMappingException("Unknown JSON-Field-Type!");
                    }
                }

                jsonGenerator.writeEndObject();
            }
        }
    }

    private static class KvpTuple {
        private final String key;
        private final Object value;
        private final FieldType type;
        private final FieldType innerType;

        private KvpTuple(String key, Object value, FieldType type) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.innerType = null;
        }

        private KvpTuple(String key, Object value, FieldType type, FieldType inner) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.innerType = inner;
        }

        private KvpTuple(String key, boolean value) {
            this(key, value, FieldType.Boolean);
        }

        private KvpTuple(String key, int value) {
            this(key, value, FieldType.Int);
        }

        private KvpTuple(String key, long value) {
            this(key, value, FieldType.Long);
        }

        private KvpTuple(String key, float value) {
            this(key, value, FieldType.Float);
        }

        private KvpTuple(String key, double value) {
            this(key, value, FieldType.Double);
        }

        private KvpTuple(String key, String value) {
            this(key, value, FieldType.String);
        }

        private KvpTuple(String key, int[] value) {
            this(key, value, FieldType.Array, FieldType.Int);
        }

        private KvpTuple(String key, long[] value) {
            this(key, value, FieldType.Array, FieldType.Long);
        }

        private KvpTuple(String key, double[] value) {
            this(key, value, FieldType.Array, FieldType.Double);
        }

        private KvpTuple(String key, String[] value) {
            this(key, value, FieldType.Array, FieldType.String);
        }

        private enum FieldType {
            Boolean,
            Int,
            Long,
            Float,
            Double,
            String,
            Array,
        }
    }
}