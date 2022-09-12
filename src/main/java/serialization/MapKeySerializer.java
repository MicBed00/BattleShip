package serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import control.Shot;

import java.io.IOException;
import java.io.StringWriter;

public class MapKeySerializer extends JsonSerializer<Shot> {
    @Override
    public void serialize(Shot shot, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//        jsonGenerator.writeString(shot.toString());
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter();

        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, shot);
    }
}
