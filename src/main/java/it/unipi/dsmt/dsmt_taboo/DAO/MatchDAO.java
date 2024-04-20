package it.unipi.dsmt.dsmt_taboo.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;
import it.unipi.dsmt.dsmt_taboo.exceptions.DatabaseNotReachableException;
import it.unipi.dsmt.dsmt_taboo.model.DTO.MatchDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.MatchResultRequestDTO;
import it.unipi.dsmt.dsmt_taboo.model.DTO.ServerResponseDTO;
import org.springframework.http.HttpStatus;


public class MatchDAO extends BaseDAO
{
    private static final int MYSQL_DUPLICATE_PK = 1062;
    public MatchDAO() {super();}

    public boolean addNewMatch(MatchDTO match)
    // Insert in BD a new Match
    {
        String insertQuery = "INSERT INTO " + DB_NAME + ".match " +
                "(Team1, Team2, ScoreTeam1, ScoreTeam2, Timestamp) " + "VALUES (?, ?, ?, ?, ?)";

        //Timestamp timestamp = Timestamp.valueOf(match.getMatchId());

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.
                                                    prepareStatement(insertQuery,
                                                    PreparedStatement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setString(1, match.getInviterTeam().toString());
            preparedStatement.setString(2, match.getRivalTeam().toString());
            preparedStatement.setInt(3, match.getScoreInviterTeam());
            preparedStatement.setInt(4, match.getScoreRivalTeam());
            //preparedStatement.setTimestamp(5, timestamp);
            preparedStatement.setString(5, match.getMatchId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("***************************************** Tupla realmente inserita");
                return true;
            }
            else
                return false;

        }
        catch (Exception ex)
        {
            if(ex.getClass() == DatabaseNotReachableException.class)
                System.out.println("addNewMatch: DatabaseNotReachableException");
            else if(ex instanceof SQLException)
            {
                SQLException e = (SQLException)ex;

                System.out.print("addNewMatch: Ex classe SQLException");
                if(e.getErrorCode() == MYSQL_DUPLICATE_PK) {
                    System.out.println("--> ritornato true");
                    return true;
                }
                System.out.println("--> ritornato false");
                return false;
                //System.out.println("addNewMatch: Dovrebbe essere duplicate entry -> " + ex.getMessage());

            }
            else
                System.out.println("addNewMatch Ex: " + ex.getMessage());
            return false;
        }
    }

    public List<MatchDTO> getMatches(String username)
    {
        List<MatchDTO> listMatches = null;

        String userMatchesQuery = "SELECT * FROM " + DB_NAME + ".match " +
                                  "WHERE Team1 LIKE ? OR Team2 LIKE ?";

        try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.
                                                       prepareStatement(userMatchesQuery,
                                                       PreparedStatement.RETURN_GENERATED_KEYS))
            {
                preparedStatement.setString(1, "%" + username + "%");
                preparedStatement.setString(2, "%" + username + "%");
                try (ResultSet resultSet = preparedStatement.executeQuery())
                {
                    listMatches = new ArrayList<>();
                    while (resultSet.next())
                    {
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
                    System.out.println("getMatches: OK");
                }
            }
        catch (Exception ex)
        {
            if(ex.getClass() == DatabaseNotReachableException.class)
                System.out.println("getMatches: DatabaseNotReachableException ");
            else
                System.out.println("getMatches: Query Ex: " + ex.getMessage());
            return null;
        }

        return listMatches;
    }


    public MatchResultRequestDTO getMatchResult(String matchId, String usernameRequester)
    {
        MatchResultRequestDTO mathResultToReturn = null;

        String getMatchQuery = "SELECT * FROM " + DB_NAME + ".match " +
                "WHERE (Timestamp = ?)"; //AND (Team1 LIKE ? OR Team2 LIKE ?)";

        int attempt = 0;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.
                     prepareStatement(getMatchQuery,
                             PreparedStatement.RETURN_GENERATED_KEYS))
        {
            //Timestamp timestamp = Timestamp.valueOf(matchId);
            //System.out.println("timestamp t string = " + timestamp.toString());
            preparedStatement.setString(1, matchId);
            //preparedStatement.setString(2, "%" + usernameRequester + "%");
            //preparedStatement.setString(3, "%" + usernameRequester + "%");
            do
            {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next())
                    {
                        String idMatch = resultSet.getString("Timestamp"); //Timestamp
                        Integer scoreInviterTeam = resultSet.getInt("ScoreTeam1");
                        Integer scoreRivalTeam = resultSet.getInt("ScoreTeam2");

                        System.out.println("MatchID= " + idMatch + "scoreInviterTeam= " + scoreInviterTeam +
                                " | scoreRivalTeam= " + scoreRivalTeam);

                        mathResultToReturn =
                                new MatchResultRequestDTO(idMatch, usernameRequester, scoreInviterTeam, scoreRivalTeam);
                        attempt = 10;
                    }
                    else
                    {
                        attempt++;
                        System.out.println("Else resultSet.nect(). Tentativo: " + attempt);
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    //throw new RuntimeException(e);
                }
            }
            while(attempt < 3);

        }
        catch (Exception ex)
        {
            if(ex.getClass() == DatabaseNotReachableException.class)
                System.out.println("getMatchResult: DatabaseNotReachableException");
            else
                System.out.println("getMatchResult: Query Ex: " + ex.getMessage());
        }
        return mathResultToReturn;
    }

}
