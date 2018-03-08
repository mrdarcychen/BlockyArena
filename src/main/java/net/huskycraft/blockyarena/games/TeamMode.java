package net.huskycraft.blockyarena.games;

/**
 * A TeamMode represents the mode of a Team based on the number of gamers on a Team.
 */
public enum TeamMode {
    SOLO(1), DOUBLES(2);

    private int capacity; // the capacity of each team in this mode

    TeamMode(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
