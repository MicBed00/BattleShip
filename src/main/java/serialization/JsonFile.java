package serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.*;
import exceptions.NullObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;

public class JsonFile<T> {
    private File file;
    private FileWriter fileWriter;
    private ObjectMapper objectMapper;
    private SequenceWriter sequenceWriter;


    public JsonFile(String filePath) throws IOException {
        this.file = new File(filePath);
        this.fileWriter = new FileWriter(file, true);


        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectMapper configure = objectMapper.configure(ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        this.sequenceWriter = objectMapper.writer().writeValuesAsArray(getFileWriter());
    }

    public File getFile() {
        return file;
    }
    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public SequenceWriter getSequenceWriter() {
        return sequenceWriter;
    }

    public void creatJson(T obj) throws IOException, NullObject{
        if(obj == null) {
            throw new NullObject("Object is null");
        } else {

            sequenceWriter.write(obj);
            sequenceWriter.close();
        }
    }

    public void closeJson() throws IOException {
       getSequenceWriter().close();
    }

}
