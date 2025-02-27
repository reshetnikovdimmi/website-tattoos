package ru.tattoo.maxsim.util;


import lombok.Getter;

import java.util.ArrayList;

@Getter
public enum PageSize {

    IMG_9(9),
    IMG_12(12),
    IMG_15(15);

    private final int pageSize;

    PageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static ArrayList<Integer> getLisPageSize() {
        ArrayList<Integer> lisPageSize = new ArrayList<>();
        for (PageSize size : PageSize.values()) {
            lisPageSize.add(size.pageSize);
        }
        return lisPageSize;
    }
}
