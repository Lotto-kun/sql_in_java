import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.sql.*;
import java.util.TreeSet;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Marker EXCEPTIONS_MARKER = MarkerFactory.getMarker("EXCEPTIONS");
    private static final String URL = "jdbc:mysql://localhost:3306/skillbox";
    private static final String USER = "root";
    private static final String PASS = "TempPass11";
    private static TreeSet<String> courseSet = new TreeSet<>();
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet courseNameSet = statement.executeQuery("SELECT name FROM courses");
            while (courseNameSet.next()) {
                courseSet.add(courseNameSet.getString("name"));
            }
            courseNameSet.close();
            System.out.println("Среднее количество покупок курсов в период продаж");
            for (String name : courseSet) {
                ResultSet avgOrder = statement.executeQuery("SELECT course_name, COUNT(*)/(1 + " +
                        "(SELECT MONTH(subscription_date) " +
                        "FROM purchaselist " +
                        "WHERE course_name = '" + name + "' " +
                        "ORDER BY subscription_date DESC " +
                        "LIMIT 1) " +
                        "- " +
                        "(SELECT MONTH(subscription_date) " +
                        "FROM purchaselist " +
                        "WHERE course_name = '" + name + "' " +
                        "ORDER BY subscription_date " +
                        "LIMIT 1)" +
                        ") subscription_count " +
                        "FROM purchaselist " +
                        "WHERE course_name = '" + name + "' " +
                        "GROUP BY course_name");
                while (avgOrder.next()) {
                    System.out.println(avgOrder.getString(1) + " - " + avgOrder.getString(2));
                }
            }


        } catch (SQLException e) {
            LOGGER.error(EXCEPTIONS_MARKER, "Ошибка при работе с ДБ", e);
            e.printStackTrace();
        }


    }
}
