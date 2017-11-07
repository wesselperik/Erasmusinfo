package com.wesselperik.erasmusinfo;

import java.util.ArrayList;

/**
 * Created by Wessel on 7-3-2016.
 */
public class Row {
    ArrayList<Page> mPagesRow = new ArrayList<Page>();

    public void addPages(Page page) {
        mPagesRow.add(page);
    }

    public Page getPages(int index) {
        return mPagesRow.get(index);
    }

    public void removePages() {
        mPagesRow.clear();
    }

    public int size(){
        return mPagesRow.size();
    }
}
