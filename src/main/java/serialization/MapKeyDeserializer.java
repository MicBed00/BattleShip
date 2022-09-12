package serialization;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import control.Shot;

import java.io.IOException;

public class MapKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Shot shot = mapper.readValue(s, Shot.class);
        return shot;
    }
}
