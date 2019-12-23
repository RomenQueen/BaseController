package com.rq.demo.contact;

import java.io.Serializable;

public interface EaseUser extends Serializable {
    String getUsername();

    String getInitialLetter();

    boolean match(String str);
}
