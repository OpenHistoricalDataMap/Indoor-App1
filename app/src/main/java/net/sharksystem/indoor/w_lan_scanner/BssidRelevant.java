package net.sharksystem.indoor.w_lan_scanner;

/**
 * Created by Christoph Bose on 11/18/16.
 */

public class BssidRelevant {
    private int lvl;
    private String name;
    private int counter;

    public BssidRelevant(String name, int lvl)
    {
        this.counter = 1;
        this.name = name;
        this.lvl = lvl;
    }

    public String getName() {
        return name;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void incrementCounter() {
        this.counter++;
    }

    public void computeAverage() {
        this.lvl = this.lvl/this.counter;
        counter = 1;
    }
}
