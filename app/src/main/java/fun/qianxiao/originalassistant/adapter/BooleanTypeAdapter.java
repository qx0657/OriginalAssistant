package fun.qianxiao.originalassistant.adapter;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * BooleanTypeAdapter
 *
 * @Author QianXiao
 * @Date 2023/5/14
 */
public class BooleanTypeAdapter extends TypeAdapter<Boolean> {
    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return in.nextInt() != 0;
            case STRING:
                return toBoolean(in.nextString());
            default:
                throw new JsonParseException("parse to boolean error, peek: " + peek);
        }
    }

    private Boolean toBoolean(String nextString) {
        if (!TextUtils.isEmpty(nextString)) {
            if ("true".equalsIgnoreCase(nextString)) {
                return true;
            }
            return !"0".equalsIgnoreCase(nextString);
        }
        return false;
    }
}
