package com.laoa.myrecipe.models;

import java.io.File;

public interface ChangeView {
    void setNext();
    void setPrevious();
    void removeItemAt(int pos);
    void addViewPath(File path);

    int getPathPos(File path);
    int getPos();
}
