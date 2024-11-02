import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotelmanagement";
    private static final String username = "root";
    private static final String password = "Admin@123";
    private static final String query = "select * from reservation";

    public static void main(String[] args) {


        //Step1:Loading Driver:
        //Class.forName("com.mysql.jdbc.cj.Driver");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());

        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1.Reserve a room");
                System.out.println("2.View Reservations");
                System.out.println("3.Get Room Number");
                System.out.println("4.Update Reservations");
                System.out.println("5.Delete Reservations");
                System.out.println("0.Exit");
                System.out.print("Choose an Option: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);

                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid Choice , Try Again!!!");
                }
            }
        } catch (SQLException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {

            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();
            System.out.println("Enter Room Number: ");
            int room_no = scanner.nextInt();
            System.out.println("Enter contact Number: ");
            String contact_no = scanner.next();


            String sql = "INSERT INTO reservation(guest_name, room_number, contact_number)" + "VALUES('" + guestName + "'," + room_no + ",'" + contact_no + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation Successful!!!");
                } else {
                    System.out.println("Reservation Failed!!!");
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }


    private static void viewReservation(Connection connection) throws SQLException {

        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date from reservation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservation: ");
            System.out.println("+---------------+--------+----------------+------------------+--------------------------+");
            System.out.println("|Reservation ID|Guest| Room Number | Contact Number   |Reservation Date |");
            System.out.println("+--------------+-----+-------------+------------------+-----------------+------------------+");
            while (resultSet.next()) {

                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("|%-14d|%-15s | %-13d|%-20s |%-19s| \n ",
                        reservationID, guestName, roomNumber, contactNumber, reservationDate);
            }


            System.out.println("+------------------------------------------+------------------+-----------------+");


        }
    }


    private static void getRoomNumber(Connection connection, Scanner scanner) {


        try {

            System.out.println("Enter Reservation ID :");
            int reservationId = scanner.nextInt();
            System.out.println("Enter Guest Name: ");
            String guestName = scanner.next();
            //String sql = "SELECT FROM reservation" + " WHERE reservation_id= " + reservationId +
            //     "AND guest_name=" + guestName + "'";

            String sql = "SELECT * FROM reservation WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";


            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room Number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);

                } else {
                    System.out.println("Reservation Not found for this given ID ad Guest Name.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void updateReservation(Connection connection, Scanner scanner) {

        try {
            System.out.println("Enter Reservation ID to update :");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("reservation Not found for the given Id");
                return;
            }
            System.out.println("Enter new Guest Name :");
            String newGuestName = scanner.next();
            System.out.println("Enter New Room No Name ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new Contact Number");
            String newContactNumber = scanner.next();
//
//            String sql = "UPDATE reservations SET guest_name= ' " + newGuestName + "'," +
//                    "room_number=" + newRoomNumber + "," +
//                    "contact_number" + newContactNumber + "' " +
//                    "WHERE reservation_id= " + reservationId;
            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;


            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation Updated successfully!!!");
                } else {
                    System.out.println("Failed to update Reservation!!!");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void deleteReservation(Connection connection, Scanner scanner) {

        try {

            System.out.println("Enter reservation ID to delete :");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {

                System.out.println("Reservation not found for this ID");
                return;
            }
            String sql = "DELETE FROM reservation WHERE reservation_id=" + reservationId;
            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation Deleted Successfully!!!");
                } else {
                    System.out.println("Reservation Deletion Failed!!!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {

        try {
            String sql = "SELECT reservation_id from reservation WHERE reservation_id=" + reservationId;
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                return resultSet.next();

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 7;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;

        }
        System.out.println();

        System.out.println("Thank You For Using Hotel Management System!!!");
    }
}






