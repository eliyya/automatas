package com.compiler.nodes;

import java.util.HashMap;

public interface Node {
    String toJSON();
    @Override
    String toString();
    HashMap<String, Object> toHashMap();
}
