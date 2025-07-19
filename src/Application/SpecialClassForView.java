package Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SpecialClassForView {
    private static String URL = Credentials.getUrl();
    private static String USER = Credentials.getUser();
    private static String PASSWORD = Credentials.getPassword();

    public static ArrayList<ModelPlayers> viewScores() {

        String updateQuery = "select *from players order by score desc";
        ArrayList<ModelPlayers> playersList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                ModelPlayers player = new ModelPlayers();
                player.setPlayerName(rs.getString("name"));
                player.setDateTime(rs.getString("registration_time"));
                player.setScore(rs.getInt("score"));
                playersList.add(player);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return playersList;
    }
}
