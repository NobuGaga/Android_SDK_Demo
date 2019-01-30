/**
 * Created by Administrator on 2018/1/10.
 *
 */

package com.greatefunhy.jpzc.ewan;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

public class JsonData extends JSONObject {

    public JsonData() {
        super();
    }

    public JsonData(Map copyFrom) {
        super(copyFrom);
    }

    public JsonData(JSONTokener readFrom) throws JSONException {
        super(readFrom);
    }

    public JsonData(String json) throws JSONException {
        super(json);
    }

    public JsonData(JSONObject copyFrom, String[] names) throws JSONException {
        super(copyFrom, names);
    }

    public void setData(String key, Object data) {
        try {
            if (null == data)
                data = "";
            if (data instanceof Integer) {
                int value = ((Integer)data).intValue();
                put(key, value);
            } else if (data instanceof String) {
                String value = (String) data;
                put(key, value);
            } else if (data instanceof Double) {
                double value = ((Double) data).doubleValue();
                put(key, value);
            } else if (data instanceof Float) {
                float value = ((Float) data).floatValue();
                put(key, value);
            } else if (data instanceof Long) {
                long value = ((Long) data).longValue();
                put(key, value);
            } else if (data instanceof Boolean) {
                boolean value = ((Boolean) data).booleanValue();
                put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getDataInt(String key) {
        try {
            Object data = get(key);
            if (data instanceof Integer) {
                int value = ((Integer) data).intValue();
                return value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public String getDataString(String key) {
        try {
            Object data = get(key);
            if (data instanceof String) {
                String value = (String) data;
                return value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public double getDataDouble(String key) {
        try {
            Object data = get(key);
            if (data instanceof Double) {
                double value = ((Double) data).doubleValue();
                return value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public float getDataFloat(String key) {
        try {
            Object data = get(key);
            if (data instanceof Float) {
                float value = ((Float) data).floatValue();
                return value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public long getDataLoog(String key) {
        try {
            Object data = get(key);
            if (data instanceof Long) {
                long value = ((Long) data).longValue();
                return value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public boolean getDataBoolean(String key) {
        try {
            Object data = get(key);
            if (data instanceof Boolean) {
                boolean value = ((Boolean) data).booleanValue();
                return value;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
