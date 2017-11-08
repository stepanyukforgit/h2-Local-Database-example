import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:file:~/irenstask";
    private static Connection dbConnection;

    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION);
            dbConnection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {

        System.out.println("Hello in our library!!!");
        System.out.println("This is a simple library where you can store you favorite books.");
        help();

        try {
            createTable();

            Scanner scanner = new Scanner(System.in);

            while (true) {

                System.out.print("Enter a command: ");

                String[] input = scanner.nextLine().split("\\s+");
                String command = input[0];
                input[0] = "";

                switch (command) {
                    case "add":
                        addBook(String.join(" ", input));
                        break;

                    case "edit":
                        int idEdit = Integer.valueOf(input[1]);
                        input[1] = "";
                        editBook(idEdit, String.join(" ", input));
                        break;

                    case "delete":
                        deleteBook(Integer.valueOf(input[1]));
                        break;

                    case "list":
                        listBook();
                        break;

                    case "help":
                        help();
                        break;

                    case "exit":
                        scanner.close();
                        System.out.println("Bye!)");
                        return;

                    default:
                        System.out.println("--------unknown command----------\n");
                        break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnection.close();
        }

    }

    private static void help() {
        System.out.println("Available commands:");
        System.out.println("1. add {book name}");
        System.out.println("2. delete {book id}");
        System.out.println("3. edit {book id} {book name}");
        System.out.println("4. list");
        System.out.println("5. help");
        System.out.println("6. exit\n");
    }

    private static void createTable() throws SQLException {

        Statement stmt = dbConnection.createStatement();

        try {
            stmt.execute("CREATE TABLE book(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255))");
            stmt.close();
            dbConnection.commit();
        } catch (SQLException e) {
            if (e.getErrorCode() != 42101) {
                e.printStackTrace();
            }
        }
    }

    private static void addBook(String book) throws SQLException {

        PreparedStatement insertPreparedStatement;
        String INSERT_QUERY = "INSERT INTO book" + "(name) VALUES" + "(?)";

        try {
            insertPreparedStatement = dbConnection.prepareStatement(INSERT_QUERY);
            insertPreparedStatement.setString(1, book);
            insertPreparedStatement.executeUpdate();
            insertPreparedStatement.close();
            dbConnection.commit();

            System.out.println("-------- " + book + " was added----------\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void editBook(int id, String book) throws SQLException {

        PreparedStatement editPreparedStatement;
        String EDIT_QUERY = "UPDATE book SET name=(?) WHERE id=(?)";

        try {
            editPreparedStatement = dbConnection.prepareStatement(EDIT_QUERY);
            editPreparedStatement.setString(1, book);
            editPreparedStatement.setInt(2, id);
            editPreparedStatement.executeUpdate();
            editPreparedStatement.close();
            dbConnection.commit();

            System.out.println("-------- " + book + " was edited----------\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteBook(int id) throws SQLException {

        PreparedStatement deletePreparedStatement;

        String DELETE_QUERY = "DELETE FROM book WHERE id=(?)";

        try {
            deletePreparedStatement = dbConnection.prepareStatement(DELETE_QUERY);
            deletePreparedStatement.setInt(1, id);
            deletePreparedStatement.executeUpdate();
            deletePreparedStatement.close();
            dbConnection.commit();

            System.out.println("-------- book with id " + id + " was deleted----------\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listBook() throws SQLException {

        PreparedStatement selectPreparedStatement;

        String SelectQuery = "SELECT * FROM book ORDER BY name";

        try {

            selectPreparedStatement = dbConnection.prepareStatement(SelectQuery);
            ResultSet rs = selectPreparedStatement.executeQuery();
            System.out.println("List of books:");
            while (rs.next()) {
                System.out.println("Id-" + rs.getInt("id") + " Book-" + rs.getString("name"));
            }
            selectPreparedStatement.close();
            dbConnection.commit();
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
