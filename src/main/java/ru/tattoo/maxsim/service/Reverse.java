package ru.tattoo.maxsim.service;

import java.util.Collections;
import java.util.List;

//todo: кажется, что не тянет это на интерфейс. Можно сделать утильным классом наверное
public interface Reverse {
    default <T> List<T> reverse(List<T> o){
        Collections.reverse(o);
        return o;
    }
}
