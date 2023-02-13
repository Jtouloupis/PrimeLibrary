import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;

public class ClientBookForm extends JDialog{
    private JTable BookTable;
    private JButton returnBookButton;
    private JButton takeBookButton;
    private JPanel ClientPanel;
    private JLabel emailField;
    private JButton logoutButton;
    private JLabel MyBook;
    public String email;
    public boolean canTake = true;
    private  Connection con;


    public ClientBookForm(String email1) {
        //initialize

        //super(parent);
        setTitle("Client Book List");
        setContentPane(ClientPanel);
        setMinimumSize(new Dimension(1050, 775));
        setLocationRelativeTo(null); // center it
        setModal(true);
        //setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        email = email1;

        //Set Labels
        emailField.setText("Email: "+ email1);
        MyBook.setText("I do not have a book yet");

        //Show book list
        show_book();



        //setVisible(true);

        //RETURN BUTTON PRESSED
        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(canTake){

                    JOptionPane.showMessageDialog(ClientBookForm.this,
                            "You dont have a book yet!",
                            "Return failed",
                            JOptionPane.ERROR_MESSAGE);


                }else {

                    updateBook("Available");//make book available again

                    MyBook.setText("I do not have a book yet");

                    JOptionPane.showMessageDialog(ClientBookForm.this,
                            "You returned the book successfully!" );
                }

            }
        });
        //show success message


        takeBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(BookTable.getSelectionModel().isSelectionEmpty()){//check if a row is selected

                    JOptionPane.showMessageDialog(ClientBookForm.this,
                            "Please select a row",
                            "Lend failed",
                            JOptionPane.ERROR_MESSAGE);

                }else
                    {
                    if(canTake){

                        updateBook(email);//tag the book with the users email

                        JOptionPane.showMessageDialog(ClientBookForm.this,
                                "You took the book successfully!" );


                    }else {

                        JOptionPane.showMessageDialog(ClientBookForm.this,
                                "You already have book, return the book you already have to take another!",
                                "Lend failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                }

        });



        //Logout button is pressed
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                LoginForm open = new LoginForm();
                setVisible(false);

                closeForm();


                open.setVisible(true);


            }
        });
    }

    public void closeForm(){

        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

    }


    // UPDATE ROW
    private  void updateBook(String status) {

        try{//Connected
            ArrayList<Books> list = booksArrayList();

            int i;

            if(canTake) {//take new book

                i  = BookTable.getSelectedRow();

               //i= keepRow;

            }else {//remove

                i= keepRow;

            }
            String value = (BookTable.getModel().getValueAt(i,0)).toString();// ID (CODE)

            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();

            Statement stmt = con.createStatement();

            //search for the specific book name
            String sql = "UPDATE books SET book_name=?, availability=?, book_info=? ,image=? where id="+value;

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,list.get(i).getBook_name());
            preparedStatement.setString(2,status);
            preparedStatement.setString(3,list.get(i).getBook_info());
            preparedStatement.setBytes(4,list.get(i).getMyImage());


            preparedStatement.executeUpdate();

            //Refresh table
            show_book();


            stmt.close();
            con.close();

        }catch (Exception e){//connection failed

            e.printStackTrace();
        }

    }

    public ArrayList<Books> booksArrayList(){

        ArrayList<Books> booksArrayList = new ArrayList<>();

        //booksArrayList.clear();

        try{//Connected


            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();


            //create a new user into database
            String query = "SELECT * FROM books";

            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);

            Books book;
            while(resultSet.next()){


                book = new Books(resultSet.getInt("id"), resultSet.getString("book_name"), resultSet.getString("availability"), resultSet.getString("book_Info"),resultSet.getBytes("image"));

                booksArrayList.add(book);
            }


            stmt.close();
            con.close();


        }catch (Exception e){//connection failed

            e.printStackTrace();
        }

        return booksArrayList;

    }

    public int keepRow;
    public void show_book(){
        ArrayList<Books> list = booksArrayList();

        canTake = true;//initialize

        //list.size();

        String[] columnName = {"Code","Book Name","Description","Image","Availability"};
        Object[][] rows = new Object[list.size()][5];

        for(int i = 0; i < list.size(); i++) {

            rows[i][0] = list.get(i).getID();
            rows[i][1] = list.get(i).getBook_name();
            rows[i][2] = list.get(i).getBook_info();

            //Set the non available books with "Not available" tag
            if (!list.get(i).getAvailability().equals("Available")) {

                rows[i][4] = "Not Available";

                //Check if client already has the specific book
                if(list.get(i).getAvailability().equals(email)){

                    keepRow = i;
                    MyBook.setText("MyBook: "+ list.get(i).getBook_name());
                    canTake = false; //cant take another book

                }

            }else {

                rows[i][4] = list.get(i).getAvailability();

            }



                if (list.get(i).getMyImage() != null) {

                ImageIcon image = new ImageIcon(new ImageIcon(list.get(i).getMyImage()).getImage()
                        .getScaledInstance(150, 120, Image.SCALE_SMOOTH));

                rows[i][3] = image;
            } else {
                rows[i][3] = null;
            }



            TheModel model = new TheModel(rows, columnName);
            BookTable.setModel(model);

            BookTable.setRowHeight(120);

            BookTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            BookTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        }



    }





}


