package tennisboard.logic;

public enum Side {
    A("player A"),
    B("player B");

    private final String player;

    Side(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }
}
