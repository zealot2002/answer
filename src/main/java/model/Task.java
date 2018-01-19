package model;

public class Task implements Runnable {
    public int index;

    public Task(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void run() {

    }
}
