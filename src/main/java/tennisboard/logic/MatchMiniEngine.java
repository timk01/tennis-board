package tennisboard.logic;

public class MatchMiniEngine {
    public static void main(String[] args) {
        Player first = new Player(1L, "Agasssi");
        Player second = new Player(2L, "Federerr");
        MatchScore matchScore = new MatchScore();
        Match match = new Match(
                first,
                second,
                matchScore,
                false
        );
        match.getMatchScore().increasePoint(Side.A);
        int currentPointA = match.getMatchScore().getCurrentPointA();
        System.out.println(currentPointA);
        System.out.println(currentPointA == 15);

    }
}
