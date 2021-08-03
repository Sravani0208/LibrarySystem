import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import net.proteanit.sql.DbUtils;

public class Main {

    public static class ex{
        public static int days=0;
    }

    public static void main(String[] args) {

        login();
        //create();
    }
    public static void login() {

        JFrame f=new JFrame("Login");//creating instance of JFrame
        JLabel l1,l2;
        l1=new JLabel("Username");  //Create label Username
        l1.setBounds(30,15, 100,30); //x axis, y axis, width, height

        l2=new JLabel("Password");  //Create label Password
        l2.setBounds(30,50, 100,30);

        JTextField F_user = new JTextField(); //Create text field for username
        F_user.setBounds(110, 15, 200, 30);

        JPasswordField F_pass=new JPasswordField(); //Create text field for password
        F_pass.setBounds(110, 50, 200, 30);

        JButton login_but=new JButton("Login");//creating instance of JButton for Login Button
        login_but.setBounds(130,90,80,25);//Dimensions for button
        login_but.addActionListener(new ActionListener() {  //Perform action

            public void actionPerformed(ActionEvent e){

                String username = F_user.getText(); //Store username entered by the user in the variable "username"
                String password = F_pass.getText(); //Store password entered by the user in the variable "password"

                if(username.equals("")) //If username is null
                {
                    JOptionPane.showMessageDialog(null,"Please enter username"); //Display dialog box with the message
                }
                else if(password.equals("")) //If password is null
                {
                    JOptionPane.showMessageDialog(null,"Please enter password"); //Display dialog box with the message
                }
                else { //If both the fields are present then to login the user, check wether the user exists already
                    //System.out.println("Login connect");
                    Connection connection=connect();  //Connect to the database
                    try
                    {
//                        Statement stmt = connection.createStatement();
//                        stmt.executeUpdate("USE LIBRARY"); //Use the database with the name "Library"
//                        String st = ("SELECT * FROM USERS WHERE USERNAME='"+username+"' AND PASSWORD='"+password+"'"); //Retreive username and passwords from users
//                        ResultSet rs = stmt.executeQuery(st); //Execute query

                        //edit
                        String st = ("SELECT * FROM USERS WHERE USERNAME='"+username+"' AND PASSWORD='"+password+"'");
                        Statement stmt = connection.prepareStatement(st,ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                        stmt.executeUpdate("USE LIBRARY"); //Use the database with the name "Library"
                        ResultSet rs = stmt.executeQuery(st);

                        //end edit


                        if(rs.next()==false) { //Move pointer below
                            System.out.print("No user");
                            JOptionPane.showMessageDialog(null,"Wrong Username/Password!"); //Display Message

                        }
                        else {
                            f.dispose();
                            rs.beforeFirst();  //Move the pointer above
                            while(rs.next())
                            {
                                String admin = rs.getString("ADMIN"); //user is admin
                                //System.out.println(admin);
                                String UID = rs.getString("UID"); //Get user ID of the user
                                if(admin.equals("1")) { //If boolean value 1
                                    admin_menu(); //redirect to admin menu
                                }
                                else{
                                    user_menu(UID); //redirect to user menu for that user ID
                                }
                            }
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        f.add(F_pass); //add password
        f.add(login_but);//adding button in JFrame
        f.add(F_user);  //add user
        f.add(l1);  // add label1 i.e. for username
        f.add(l2); // add label2 i.e. for password

        f.setSize(400,180);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);

    }

    public static Connection connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("Loaded driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=rootpassword");
            //System.out.println("Connected to MySQL");
            return con;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void create() {
        try {
            Connection connection=connect();
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            //iterate each catalog in the ResultSet
            while (resultSet.next()) {
                // Get the database name, which is at position 1
                String databaseName = resultSet.getString(1);
                if(databaseName.equals("library")) {
                    //System.out.print("yes");
                    Statement stmt = connection.createStatement();
                    //Drop database if it pre-exists to reset the complete database
                    String sql = "DROP DATABASE library";
                    stmt.executeUpdate(sql);
                }
            }
            Statement stmt = connection.createStatement();

            String sql = "CREATE DATABASE LIBRARY"; //Create Database
            stmt.executeUpdate(sql);
            stmt.executeUpdate("USE LIBRARY"); //Use Database
            //Create Users Table
            String sql1 = "CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(30), PASSWORD VARCHAR(30),AGE TINYINT, ADDRESS VARCHAR(100), PHONENO BIGINT, ADMIN BOOLEAN)";
            stmt.executeUpdate(sql1);
            //Insert into users table
            stmt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, AGE , ADDRESS ,PHONENO ,ADMIN ) VALUES('admin','admin',40,'123 ABCD',123456789,TRUE),('janki','janki',20,'123 Austin,texas',123456789,FALSE),('sravani','sravani',20,'089 Austin,texas',113089738,FALSE),('jyotsna','123',20,'90 Round Rock,texas',3313855867,FALSE),('Abhilash','123',11,'90 San-marcos,texas',3313886059,FALSE)");
            //Create Books table
            stmt.executeUpdate("CREATE TABLE BOOKS(BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, BNAME VARCHAR(50), TYPE VARCHAR(20),BEST_SELLER BOOLEAN,PRICE INT)");
            //Create Issued Table
            stmt.executeUpdate("CREATE TABLE ISSUED(IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UID INT, BID INT, ISSUED_DATE VARCHAR(20), RETURN_DATE VARCHAR(20), DUE_DATE VARCHAR(20),RENEWED BOOLEAN NOT NULL DEFAULT 0,RESERVED BOOLEAN NOT NULL DEFAULT 0, FINE FLOAT(5,2))");
            
           // stmt.executeUpdate("CREATE TABLE RESERVE_BOOK(BID INT NOT NULL PRIMARY KEY, BNAME VARCHAR(50),RESERVE_UID INT)");
          //Insert into books table
            stmt.executeUpdate("INSERT INTO BOOKS(BNAME, TYPE,BEST_SELLER, PRICE) VALUES ('War and Peace','Book',TRUE, 200),  ('The Guest Book','Book',FALSE, 300), ('The Perfect Murder','Audio/Video',FALSE, 150), ('Accidental Presidents','Book',FALSE, 250), ('The Wicked King','Book',FALSE, 350),('The King Kong','Book',FALSE, 150),('The Essence','Book',FALSE, 100),('Adv Software Project','Book',FALSE, 100),('Database Theory','Book',FALSE, 100),('software engg.','Reference Book',FALSE, 100),('Forbes','Magazine',FALSE, 100)");

            //stmt.executeUpdate("INSERT INTO BOOKS(BNAME, TYPE,BEST_SELLER, PRICE) VALUES ('War and Peace','Book',TRUE, 200),  ('The Guest Book','Reference Book',FALSE, 300), ('The Perfect Murder','Audio/Video',FALSE, 150), ('Accidental Presidents','Book',FALSE, 250), ('The Wicked King','Magezine',FALSE, 350)");
            stmt.executeUpdate("CREATE TABLE REQUESTED_BOOKS(BID INT NOT NULL PRIMARY KEY, BNAME VARCHAR(50),REQUESTED_UID INT)");
            //stmt.executeUpdate("INSERT INTO RESERVE_BOOK(BID INT NOT NULL PRIMARY KEY, BNAME VARCHAR(50),REQUESTED_UID INT)");
            resultSet.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void user_menu(String UID) {


        JFrame f=new JFrame("User Functions"); //Give dialog box name as User functions
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit user menu on closing the dialog box
        JButton view_but=new JButton("View Items");//creating instance of JButton
        view_but.setBounds(20,20,120,25);//x axis, y axis, width, height
        view_but.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e){

                                           JFrame f = new JFrame("Items Available"); //View books stored in database
                                           //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                                           Connection connection = connect();
                                           String sql="select * from BOOKS"; //Retreive data from database
                                           try {
                                               Statement stmt = connection.createStatement(); //connect to database
                                               stmt.executeUpdate("USE LIBRARY"); // use librabry
                                               stmt=connection.createStatement();
                                               ResultSet rs=stmt.executeQuery(sql);
                                               JTable book_list= new JTable(); //show data in table format
                                               book_list.setModel(DbUtils.resultSetToTableModel(rs));

                                               JScrollPane scrollPane = new JScrollPane(book_list); //enable scroll bar

                                               f.add(scrollPane); //add scroll bar
                                               f.setSize(800, 400); //set dimensions of view books frame
                                               f.setVisible(true);
                                               f.setLocationRelativeTo(null);
                                           } catch (SQLException e1) {
                                               // TODO Auto-generated catch block
                                               JOptionPane.showMessageDialog(null, e1);
                                           }

                                       }
                                   }
        );

        JButton my_book=new JButton("My Items");//creating instance of JButton
        my_book.setBounds(150,20,120,25);//x axis, y axis, width, height
        my_book.addActionListener(new ActionListener() { //Perform action
                                      public void actionPerformed(ActionEvent e){


                                          JFrame f = new JFrame("My Items"); //View books issued by user
                                          //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                          int UID_int = Integer.parseInt(UID); //Pass user ID

                                          //.iid,issued.uid,issued.bid,issued.issued_date,issued.return_date,issued,
                                          Connection connection = connect(); //connect to database
                                          //retrieve data
                                          String sql="select distinct issued.*,books.bname,books.type,books.price from issued,books " + "where ((issued.uid=" + UID_int + ") and (books.bid in (select bid from issued where issued.uid="+UID_int+"))) group by iid";
                                          String sql1 = "select bid from issued where uid="+UID_int;
                                          try {
                                              Statement stmt = connection.createStatement();
                                              //use database
                                              stmt.executeUpdate("USE LIBRARY");
                                              stmt=connection.createStatement();
                                              //store in array
                                              ArrayList books_list = new ArrayList();



                                              ResultSet rs=stmt.executeQuery(sql);
                                              JTable book_list= new JTable(); //store data in table format
                                              book_list.setModel(DbUtils.resultSetToTableModel(rs));
                                              //enable scroll bar
                                              JScrollPane scrollPane = new JScrollPane(book_list);

                                              f.add(scrollPane); //add scroll bar
                                              f.setSize(800, 400); //set dimensions of my books frame
                                              f.setVisible(true);
                                              f.setLocationRelativeTo(null);
                                          } catch (SQLException e1) {
                                              // TODO Auto-generated catch block
                                              JOptionPane.showMessageDialog(null, e1);
                                          }

                                      }
                                  }
        );

        JButton request_book=new JButton("Request Items"); //creating instance of JButton for adding books
        request_book.setBounds(280,20,120,25);

        request_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //set frame to request a book
                JFrame g = new JFrame("Enter Item Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // set labels
                JLabel l1,l2,l3;
                l1=new JLabel("BID");  //lebel 1 for book ID
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("Item Name");  //label 2 for book name
                l2.setBounds(30,53, 100,30);

                l3=new JLabel("Requested UID");  //label 3 for UID
                l3.setBounds(30,90, 100,30);

                //set text field for book id
                JTextField F_bid = new JTextField();
                F_bid.setBounds(130, 15, 200, 30);

                //set text field for book name
                JTextField F_bname=new JTextField();
                F_bname.setBounds(130, 53, 200, 30);

                //set text field for UID
                JTextField F_uid=new JTextField();
                F_uid.setBounds(130, 90, 200, 30);

                JButton create_but=new JButton("Submit");//creating instance of JButton to submit details
                create_but.setBounds(160,185,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){
                        // assign the book name, type, price
                        String bid = F_bid.getText();
                        String bname = F_bname.getText();
                        String uid = F_uid.getText();

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("UPDATE ISSUED SET RESERVED='"+1+"' WHERE BID="+bid);
                            stmt.executeUpdate("INSERT INTO REQUESTED_BOOKS(BID,BNAME,REQUESTED_UID) VALUES ('"+bid+"','"+bname+"',"+uid+")");
                            //stmt.executeUpdate("UPDATE REQUESTED_BOOKS SET BID='"+bid"', BNAME='"bname"',REQUESTED_UID="+uid);
                            JOptionPane.showMessageDialog(null,"Item requested!");
                            g.dispose();

                        }
                        

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });

                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(l3);
                g.add(F_bid);
                g.add(F_bname);
                g.add(F_uid);
                g.setSize(400,300);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);

            }
        });

        f.add(my_book); //add my books
        f.add(view_but); // add view books
        f.add(request_book);//add request book
        f.setSize(450,100);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);
    }

    public static void admin_menu() {


        JFrame f=new JFrame("Admin Functions"); //Give dialog box name as admin functions
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //


        JButton create_but=new JButton("Create/Reset");//creating instance of JButton to create or reset database
        create_but.setBounds(450,60,120,25);//x axis, y axis, width, height
        create_but.addActionListener(new ActionListener() { //Perform action
            public void actionPerformed(ActionEvent e){

                create(); //Call create function
                JOptionPane.showMessageDialog(null,"Database Created/Reset!"); //Open a dialog box and display the message

            }
        });


        JButton view_but=new JButton("View Items");//creating instance of JButton to view books
        view_but.setBounds(20,20,120,25);//x axis, y axis, width, height
        view_but.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e){

                                           JFrame f = new JFrame("Items Available");
                                           //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                                           Connection connection = connect(); //connect to database
                                           String sql="select * from BOOKS"; //select all books
                                           try {
                                               Statement stmt = connection.createStatement();
                                               stmt.executeUpdate("USE LIBRARY"); //use database
                                               stmt=connection.createStatement();
                                               ResultSet rs=stmt.executeQuery(sql);
                                               JTable book_list= new JTable(); //view data in table format
                                               book_list.setModel(DbUtils.resultSetToTableModel(rs));
                                               //mention scroll bar
                                               JScrollPane scrollPane = new JScrollPane(book_list);

                                               f.add(scrollPane); //add scrollpane
                                               f.setSize(800, 400); //set size for frame
                                               f.setVisible(true);
                                               f.setLocationRelativeTo(null);
                                           } catch (SQLException e1) {
                                               // TODO Auto-generated catch block
                                               JOptionPane.showMessageDialog(null, e1);
                                           }

                                       }
                                   }
        );

        JButton users_but=new JButton("View Users");//creating instance of JButton to view users
        users_but.setBounds(150,20,120,25);//x axis, y axis, width, height
        users_but.addActionListener(new ActionListener() { //Perform action on click button
                                        public void actionPerformed(ActionEvent e){

                                            JFrame f = new JFrame("Users List");
                                            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                                            Connection connection = connect();
                                            String sql="select * from users"; //retrieve all users
                                            try {
                                                Statement stmt = connection.createStatement();
                                                stmt.executeUpdate("USE LIBRARY"); //use database
                                                stmt=connection.createStatement();
                                                ResultSet rs=stmt.executeQuery(sql);
                                                JTable user_list= new JTable();
                                                user_list.setModel(DbUtils.resultSetToTableModel(rs));
                                                //mention scroll bar
                                                JScrollPane scrollPane = new JScrollPane(user_list);

                                                f.add(scrollPane); //add scrollpane
                                                f.setSize(800, 400); //set size for frame
                                                f.setVisible(true);
                                                f.setLocationRelativeTo(null);
                                            } catch (SQLException e1) {
                                                // TODO Auto-generated catch block
                                                JOptionPane.showMessageDialog(null, e1);
                                            }


                                        }
                                    }
        );

        JButton issued_but=new JButton("View Issued Items");//creating instance of JButton to view the issued books
        issued_but.setBounds(280,20,160,25);//x axis, y axis, width, height
        issued_but.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e){

                                             JFrame f = new JFrame("Users List");
                                             //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                                             Connection connection = connect();
                                             String sql="select * from issued";
                                             try {
                                                 Statement stmt = connection.createStatement();
                                                 stmt.executeUpdate("USE LIBRARY");
                                                 stmt=connection.createStatement();
                                                 ResultSet rs=stmt.executeQuery(sql);
                                                 JTable book_list= new JTable();
                                                 book_list.setModel(DbUtils.resultSetToTableModel(rs));

                                                 JScrollPane scrollPane = new JScrollPane(book_list);

                                                 f.add(scrollPane);
                                                 f.setSize(800, 400);
                                                 f.setVisible(true);
                                                 f.setLocationRelativeTo(null);
                                             } catch (SQLException e1) {
                                                 // TODO Auto-generated catch block
                                                 JOptionPane.showMessageDialog(null, e1);
                                             }

                                         }
                                     }
        );


        JButton add_user=new JButton("Add User"); //creating instance of JButton to add users
        add_user.setBounds(20,60,120,25); //set dimensions for button

        add_user.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter User Details"); //Frame to enter user details
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //Create label
                JLabel l1,l2,l3,l4,l5;
                l1=new JLabel("Username");  //label 1 for username
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("Password");  //label 2 for password
                l2.setBounds(30,50, 100,30);

                l3=new JLabel("Age");  //label 3 for username
                l3.setBounds(30,85, 100,30);

                l4=new JLabel("Address");  //label 4 for username
                l4.setBounds(30,120, 100,30);

                l5=new JLabel("PhoneNo");  //label 5 for username
                l5.setBounds(30,155, 100,30);
                //set text field for username
                JTextField F_user = new JTextField();
                F_user.setBounds(110, 15, 200, 30);

                //set text field for password
                JPasswordField F_pass=new JPasswordField();
                F_pass.setBounds(110, 50, 200, 30);

                //set text field for age
                JTextField F_age=new JTextField();
                F_age.setBounds(110, 85, 200, 30);

                //set text field for address
                JTextField F_address=new JTextField();
                F_address.setBounds(110, 120, 200, 30);

                //set text field for phoneNo
                JTextField F_phoneNo=new JTextField();
                F_phoneNo.setBounds(110, 155, 200, 30);

                //set radio button for admin
                JRadioButton a1 = new JRadioButton("Admin");
                a1.setBounds(55, 190, 200,30);
                //set radio button for user
                JRadioButton a2 = new JRadioButton("User");
                a2.setBounds(130, 190, 200,30);
                //add radio buttons
                ButtonGroup bg=new ButtonGroup();
                bg.add(a1);bg.add(a2);


                JButton create_but=new JButton("Create");//creating instance of JButton for Create
                create_but.setBounds(130,225,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String username = F_user.getText();
                        String password = F_pass.getText();
                        String age = F_age.getText();
                        String address = F_address.getText();
                        String phoneno = F_phoneNo.getText();
                        Boolean admin = false;

                        if(a1.isSelected()) {
                            admin=true;
                        }

                        Connection connection = connect();

                        try {
                            Integer newID = 0;
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                           // stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,AGE,ADDRESS,PHONENO,ADMIN) VALUES ('"+username+"','"+password+"',"+admin+"+)");
                            newID = stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,AGE,ADDRESS,PHONENO,ADMIN) VALUES ('"+username+"','"+password+"','"+age+"','"+address+"','"+phoneno+"',"+admin+")",Statement.RETURN_GENERATED_KEYS);
                            ResultSet rs = stmt.getGeneratedKeys();
                            int resultat0 =0;
                            if(rs.next()){
                                resultat0 = rs.getInt(1);
                            }
                            JOptionPane.showMessageDialog(null,"User added!\nYour User ID is: " + resultat0 );

                            g.dispose();

                        }

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });


                g.add(create_but);
                g.add(a2);
                g.add(a1);
                g.add(l1);
                g.add(l2);
                g.add(l3);
                g.add(l4);
                g.add(l5);
                g.add(F_user);
                g.add(F_pass);
                g.add(F_age);
                g.add(F_address);
                g.add(F_phoneNo);
                g.setSize(400,300);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);


            }
        });


        JButton add_book=new JButton("Add an Items"); //creating instance of JButton for adding books
        add_book.setBounds(150,60,120,25);

        add_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //set frame wot enter book details
                JFrame g = new JFrame("Enter Item Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // set labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("Item Name");  //lebel 1 for book name
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("Type");  //label 2 for type
                l2.setBounds(30,53, 100,30);

                l3=new JLabel("Best Seller");  //label 3 for price
                l3.setBounds(30,90, 100,30);

                l4=new JLabel("Price");  //label 4 for price
                l4.setBounds(30,128, 100,30);

                //set text field for book name
                JTextField F_bname = new JTextField();
                F_bname.setBounds(110, 15, 200, 30);

                //set text field for Type
                JTextField F_type=new JTextField();
                F_type.setBounds(110, 53, 200, 30);
                //set text field for best-seller
                JTextField F_bestSeller=new JTextField();
                F_bestSeller.setBounds(110, 90, 200, 30);
                //set text field for price
                JTextField F_price=new JTextField();
                F_price.setBounds(110, 128, 200, 30);


                JButton create_but=new JButton("Submit");//creating instance of JButton to submit details
                create_but.setBounds(130,180,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){
                        // assign the book name, type, price
                        String bname = F_bname.getText();
                        String type = F_type.getText();
                        String bestseller = F_bestSeller.getText();
                        String price = F_price.getText();
                        //convert price of integer to int
                        int price_int = Integer.parseInt(price);

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            stmt.executeUpdate("INSERT INTO BOOKS(BNAME,TYPE,BEST_SELLER,PRICE) VALUES ('"+bname+"','"+type+"','"+bestseller+"',"+price_int+")");
                            JOptionPane.showMessageDialog(null,"Item added!");
                            g.dispose();

                        }

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });

                g.add(l4);
                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(l3);
                g.add(F_bname);
                g.add(F_type);
                g.add(F_bestSeller);
                g.add(F_price);
                g.setSize(350,300);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);

            }
        });


        JButton issue_book=new JButton("Issue Items"); //creating instance of JButton to issue books
        issue_book.setBounds(450,20,120,25);

        issue_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //enter details
                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //create labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("Item ID(BID)");  // Label 1 for Book ID
                l1.setBounds(30,15, 100,30);


                l2=new JLabel("User ID(UID)");  //Label 2 for user ID
                l2.setBounds(30,53, 100,30);

//                l3=new JLabel("Period(days)");  //Label 3 for period
//                l3.setBounds(30,90, 100,30);

                l4=new JLabel("Issued Date (DD-MM-YYYY)");  //Label 4 for issue date
                l4.setBounds(30,90, 150,30);

                JTextField F_bid = new JTextField();
                F_bid.setBounds(200, 15, 200, 30);


                JTextField F_uid=new JTextField();
                F_uid.setBounds(200, 53, 200, 30);

//                JTextField F_period=new JTextField();
//                F_period.setBounds(110, 90, 200, 30);

                JTextField F_issue=new JTextField();
                F_issue.setBounds(200, 90, 200, 30);


                JButton create_but=new JButton("Submit");//creating instance of JButton
                create_but.setBounds(150,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String uid = F_uid.getText();
                        String bid = F_bid.getText();
                        //String period = F_period.getText();
                        String issued_date = F_issue.getText();


                        //int period_int = Integer.parseInt(period);
                        //Calendar c = Calendar.getInstance();
                        Date issued_Date1 = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            issued_Date1 = sdf.parse(issued_date);
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        Calendar c = Calendar.getInstance();
                        c.setTime(issued_Date1);
                        c.add(Calendar.DATE,14);
                        String due_Date = sdf.format(c.getTime());
                        //System.out.println(due_Date);

                        // check age
                        Connection connection = connect();
                        Statement stmt3 = null;
                        try {
                            stmt3 = connection.createStatement();
                            stmt3.executeUpdate("USE LIBRARY"); //use database
                            stmt3=connection.createStatement();

                            Integer age= 0;
                            ResultSet rs = stmt3.executeQuery("select AGE from USERS where UID =" +uid);

                            while (rs.next()) {
                                age = rs.getInt(1);

                            }
                            if(age <= 12){
                                ResultSet rs_items = stmt3.executeQuery("select count(*) from ISSUED where UID =" +uid);
                                Integer items= 0;

                                while (rs_items.next()) {
                                    items = rs_items.getInt(1);

                                }
                                if(items >= 5){
                                    JOptionPane.showMessageDialog(null,"Request Denied! \n you have already issued 5 items.");
                                     return;
                                }

                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        try {
                            Statement stmt1 = connection.createStatement();
                            stmt1.executeUpdate("USE LIBRARY"); //use database
                            stmt1=connection.createStatement();

                            String type= "";
                            ResultSet rs = stmt1.executeQuery("select TYPE from BOOKS where BID =" +bid);

                            while (rs.next()) {
                                type = rs.getString(1);

                            }
                            Statement stmt2 = connection.createStatement();
                            stmt2.executeUpdate("USE LIBRARY"); //use database
                            stmt2 = connection.createStatement();
                            Boolean bestseller= false;
                            ResultSet rs1 = stmt2.executeQuery("select BEST_SELLER from BOOKS where BID =" +bid);
                            while (rs1.next()) {
                                bestseller = rs1.getBoolean(1);

                            }

                            if(type.equals("Reference Book") || type.equals("Magezine")){
                                JOptionPane.showMessageDialog(null,"Reference Books and Magezines can not be iussed.") ;
                            }else if(type.equals("Book")){

                                if(!bestseller){
                                    c.add(Calendar.DATE,7);
                                    due_Date = sdf.format(c.getTime());
                                }

                                Statement stmt = connection.createStatement();
                                stmt.executeUpdate("USE LIBRARY");//use database
                                stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,DUE_DATE) VALUES ('"+uid+"','"+bid+"','"+issued_date+"','"+due_Date+"')");
                                JOptionPane.showMessageDialog(null,"Book Issued!\n Due date is :"+ due_Date);
                                g.dispose();
                            }else if(type.equals("Audio/Video")){
                                Statement stmt = connection.createStatement();
                                stmt.executeUpdate("USE LIBRARY");//use database
                                stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,DUE_DATE) VALUES ('"+uid+"','"+bid+"','"+issued_date+"','"+due_Date+"')");
                                JOptionPane.showMessageDialog(null,"Audio/Video Issued!\n Due date is :"+ due_Date);
                                g.dispose();
                            }

                        }

                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });


                //g.add(l3);
                g.add(l4);
                g.add(create_but);
                g.add(l1);
                g.add(l2);
                g.add(F_uid);
                g.add(F_bid);
               // g.add(F_period);
                g.add(F_issue);
                g.setSize(450,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);


            }
        });

        JButton renew_book=new JButton("Renew Item"); //creating instance of JButton to return books
        renew_book.setBounds(20,100,120,25);

        renew_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame g = new JFrame("Enter Details");

                JLabel l1;
                l1=new JLabel("Issue ID(IID)");  //Label 1 for Issue ID
                l1.setBounds(30,15, 100,30);


                JTextField F_iid = new JTextField();
                F_iid.setBounds(230, 15, 130, 30);


                JButton create_renew_but=new JButton("Renew");
                create_renew_but.setBounds(130,170,80,25);

                create_renew_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {

                        String iid = F_iid.getText();

                        Connection connection = connect();

                        Statement stmt = null;
                        try {
                            stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");

                            boolean renew = false;
                            boolean reserve = false;
                            String due_date = "",issue_date = "";
                            ResultSet rs = stmt.executeQuery("SELECT RENEWED,DUE_DATE,ISSUED_DATE,RESERVED FROM ISSUED WHERE IID="+iid);
                            while (rs.next()) {
                                renew = rs.getBoolean(1);
                                due_date = rs.getString(2);
                                issue_date = rs.getString(3);
                                reserve = rs.getBoolean(4);
                            }

                            Date date_1=new SimpleDateFormat("dd-MM-yyyy").parse(due_date);
                            Date date_2=new SimpleDateFormat("dd-MM-yyyy").parse(issue_date);
                            //subtract the dates and store in diff
                            long diff = date_1.getTime() - date_2.getTime();
                            //Convert diff from milliseconds to days
                            ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));


                            if(renew == false && reserve == false){
                                 stmt = connection.createStatement();
                                 stmt.executeUpdate("USE LIBRARY");
                                Date due_date1 = null;
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    due_date1 = sdf.parse(due_date);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                Calendar c = Calendar.getInstance();
                                c.setTime(due_date1);
                                c.add(Calendar.DATE,ex.days);
                                due_date = sdf.format(c.getTime());

                                 stmt.executeUpdate("UPDATE ISSUED SET DUE_DATE='"+due_date+"',RENEWED='"+1+"' WHERE IID="+iid);
                                 g.dispose();
                                JOptionPane.showMessageDialog(null,"Item Renewed!");
                            }
                            else if(renew == true){
                                JOptionPane.showMessageDialog(null,"Request Denied! \n Item has renewed already once!");
                            }else if (reserve == true){
                                JOptionPane.showMessageDialog(null,"Request Denied! \n Item has been requested by another user!");

                            }


                        } catch (SQLException | ParseException throwables) {
                            throwables.printStackTrace();
                        }


                      }
                    }
                );

                g.add(create_renew_but);
                g.add(l1);
                g.add(F_iid);
                g.setSize(400,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);

                }
            }
        );



        JButton return_book=new JButton("Return Item"); //creating instance of JButton to return books
        return_book.setBounds(280,60,160,25);

        return_book.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set labels
                JLabel l1,l2,l3,l4;
                l1=new JLabel("Issue ID(IID)");  //Label 1 for Issue ID
                l1.setBounds(30,15, 100,30);


                l4=new JLabel("Return Date(DD-MM-YYYY)");
                l4.setBounds(30,50, 150,30);

                JTextField F_iid = new JTextField();
                F_iid.setBounds(110, 15, 200, 30);


                JTextField F_return=new JTextField();
                F_return.setBounds(180, 50, 130, 30);


                JButton create_but=new JButton("Return");//creating instance of JButton to mention return date and calculcate fine
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height
                create_but.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e){

                        String iid = F_iid.getText();
                        String return_date = F_return.getText();

                        Connection connection = connect();

                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY");
                            //Intialize date1 with NULL value
                            String date1=null;
                            String date2=return_date; //Intialize date2 with return date

                            //select issue date
                            ResultSet rs = stmt.executeQuery("SELECT DUE_DATE FROM ISSUED WHERE IID="+iid);
                            while (rs.next()) {
                                date1 = rs.getString(1);

                            }

                            try {
                                Date date_1=new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                                Date date_2=new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                                //subtract the dates and store in diff
                                long diff = date_2.getTime() - date_1.getTime();
                                //Convert diff from milliseconds to days
                                ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));


                            } catch (ParseException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }


                            //update return date
                            stmt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='"+return_date+"' WHERE IID="+iid);
                            g.dispose();


//                            Connection connection1 = connect();
//                            Statement stmt1 = connection1.createStatement();
//                            stmt1.executeUpdate("USE LIBRARY");
//                            ResultSet rs1 = stmt1.executeQuery("SELECT DUE_DATE FROM ISSUED WHERE IID="+iid); //set period
//                            String dueDate=null;
//                            while (rs1.next()) {
//                                dueDate = rs1.getString(1);
//
//                            }
//                            Date due_date=new SimpleDateFormat("dd-MM-yyyy").parse(dueDate);


                            //int diff_int = Integer.parseInt(diff);
                            Connection connection1 = connect();
                            Statement stmt1 = connection1.createStatement();
                            stmt1.executeUpdate("USE LIBRARY");
                            if(ex.days > 0) { //If number of days are more than the period then calculcate fine

                                //System.out.println(ex.days);
                                double fine = (ex.days)*0.10; //fine for every day after the period is Rs 10.
                                //update fine in the system
                                stmt1.executeUpdate("UPDATE ISSUED SET FINE="+fine+" WHERE IID="+iid);
                                String fine_str = ("Fine: $ "+fine);
                                JOptionPane.showMessageDialog(null,fine_str);

                            }

                            JOptionPane.showMessageDialog(null,"Item Returned!");

                        }


                        catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }

                });
                g.add(l4);
                g.add(create_but);
                g.add(l1);
                g.add(F_iid);
                g.add(F_return);
                g.setSize(350,250);//400 width and 500 height
                g.setLayout(null);//using no layout managers
                g.setVisible(true);//making the frame visible
                g.setLocationRelativeTo(null);
            }
        });


        JButton view_request_but=new JButton("View Requested Items");//creating instance of JButton to view books
        view_request_but.setBounds(150,100,200,25);//x axis, y axis, width, height
        view_request_but.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){

                        JFrame f = new JFrame("Item Requested");
                        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        Connection connection = connect(); //connect to database
                        String sql="select * from REQUESTED_BOOKS"; //select all books
                        try {
                            Statement stmt = connection.createStatement();
                            stmt.executeUpdate("USE LIBRARY"); //use database
                            stmt=connection.createStatement();
                            ResultSet rs=stmt.executeQuery(sql);
                            JTable book_list= new JTable(); //view data in table format
                            book_list.setModel(DbUtils.resultSetToTableModel(rs));
                            //mention scroll bar
                            JScrollPane scrollPane = new JScrollPane(book_list);

                            f.add(scrollPane); //add scrollpane
                            f.setSize(800, 400); //set size for frame
                            f.setVisible(true);
                            f.setLocationRelativeTo(null);
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, e1);
                        }

                    }
        }
        );
        
        f.add(create_but);
        f.add(return_book);
        f.add(renew_book);
        f.add(issue_book);
        f.add(add_book);
        f.add(issued_but);
        f.add(users_but);
        f.add(view_but);
        f.add(add_user);
        f.add(view_request_but);
        f.setSize(600,200);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible
        f.setLocationRelativeTo(null);

    }
}
