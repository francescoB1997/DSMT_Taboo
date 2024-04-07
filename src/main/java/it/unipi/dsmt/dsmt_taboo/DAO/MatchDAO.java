package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unipi.dsmt.dsmt_taboo.model.DTO.MatchDTO;


public class MatchDAO extends BaseDAO
{
    public MatchDAO() { }

    public boolean addNewMatch(MatchDTO match)
    // Insert in BD a new Match
    {
        String insertQuery = "INSERT INTO " + DB_NAME + ".match " +
                "(Team1, Team2, ScoreTeam1, ScoreTeam2, Timestamp) " + "VALUES (?, ?, ?, ?, ?)";

        Timestamp timestamp = Timestamp.valueOf(match.getMatchId());

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.
                                                    prepareStatement(insertQuery,
                                                    PreparedStatement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setString(1, match.getInviterTeam().toString());
            preparedStatement.setString(2, match.getRivalTeam().toString());
            preparedStatement.setInt(3, match.getScoreInviterTeam());
            preparedStatement.setInt(4, match.getScoreRivalTeam());
            preparedStatement.setTimestamp(5, timestamp);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0)
                return true;
            else
                return false;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<MatchDTO> getMatches(String username)
    {
        List<MatchDTO> listMatches = new ArrayList<>();

        String userMatchesQuery = "SELECT * FROM " + DB_NAME + ".match " +
                                  "WHERE Team1 LIKE ? OR Team2 LIKE ?";

        try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.
                                                       prepareStatement(userMatchesQuery,
                                                       PreparedStatement.RETURN_GENERATED_KEYS))
            {
                preparedStatement.setString(1, "%" + username + "%");
                preparedStatement.setString(2, "%" + username + "%");

                try (ResultSet resultSet = preparedStatement.executeQuery();)
                {
                    while (resultSet.next()) {
                        String idMatch = resultSet.getString("Timestamp"); //Timestamp
                        String team1 = resultSet.getString("Team1");
                        String team2 = resultSet.getString("Team2");
                        Integer scoreTeam1 = resultSet.getInt("ScoreTeam1");
                        Integer scoreTeam2 = resultSet.getInt("ScoreTeam2");

                        ArrayList<String> team1List =
                                new ArrayList<>(Arrays.asList(team1.split(",")));
                        ArrayList<String> team2List =
                                new ArrayList<>(Arrays.asList(team2.split(",")));

                        MatchDTO match = new MatchDTO(idMatch, team1List,
                                                      team2List, scoreTeam1,
                                                      scoreTeam2);
                        listMatches.add(match);
                    }
                }
            } catch (SQLException ex) {
                System.out.println("(User) searchMatchesInDB eccezione query:" + ex.getMessage());
                return null;
            }
        return listMatches;
    }

}
