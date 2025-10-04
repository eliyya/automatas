package com.tokens;

import java.util.HashMap;

public interface Node {
    String toJSON();
    @Override
    String toString();
    HashMap<String, Object> toHashMap();
}
