package ru.tattoo.maxsim.service;

import java.util.Collections;
import java.util.List;

public interface Reverse {
    default <T> List<T> reverse(List<T> o){
        Collections.reverse(o);
        return o;
    }
}
