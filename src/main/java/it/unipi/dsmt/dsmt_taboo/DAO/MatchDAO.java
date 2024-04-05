package it.unipi.dsmt.dsmt_taboo.DAO;

import it.unipi.dsmt.dsmt_taboo.model.DTO.MatchDTO;

import java.sql.*;

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
                     prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    public void getAllUserMatches(String username)
    // retrieve all matches of this username
    {

    }
}
