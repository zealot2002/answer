package model;

public interface DataCallback<T>{
    void onCallback(boolean bResult, T t, Object tagData);
}
