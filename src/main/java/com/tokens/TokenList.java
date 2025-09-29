package com.tokens;

import java.util.ArrayList;

public class TokenList extends ArrayList<Token> {

    public boolean hasNext() {
        return !isEmpty();
    }

    public Token next() {
        return remove(0);
    }
    
}
