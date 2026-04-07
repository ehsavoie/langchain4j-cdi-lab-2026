
package org.acme;

import jakarta.json.Json;
import jakarta.json.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.wildfly.wasm.api.WasmArgumentSerializer;

public class DiceRollJsonSerializer implements WasmArgumentSerializer{

    @Override
    public byte[] serialize(Object[] args) {
         try (StringWriter out = new StringWriter();
                JsonWriter json = Json.createWriter(out)) {
            json.writeObject(Json.createObjectBuilder().add("numFaces", (int)args[0]).add("numDice", (int)args[1]).build());
            return out.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
