package ulric.li.mode.intf;

import org.json.JSONObject;

public interface IXJsonSerialization {
    JSONObject Serialization();

    void Deserialization(JSONObject jsonObject);
}
