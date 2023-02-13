import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

public class BookFormAdmin extends JDialog {
    private JTable BookTable;
    private JButton addBookButton;
    private JButton updateBookButton;
    private JButton DeleteButton;
    private JTextField BookNameField;
    private JCheckBox checkBox1;
    private JLabel descriptionLabel;
    private JLabel AvailabilityLabel;
    private JTextField DescriptionField;
    private JPanel AdminPanel;
    private JTextField holder;
    private JButton UploadButton;
    private JButton Logout;
    private JTextField DeleteField1;
    private JButton deleteUserButton;

    FileInputStream fis = null;
    File image = null;
    Connection con;

    public BookFormAdmin() {
        //initialize
        setTitle("Admin Book List");
        setContentPane(AdminPanel);
        setMinimumSize(new Dimension(1050,775));
        setLocationRelativeTo(null); // center it
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);



        //Display books on table
        show_book();



        // GET SELECTED ROW AND DISPLAY IT IN FIELDS
        BookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                int i = BookTable.getSelectedRow();
                TableModel model = BookTable.getModel();

                BookNameField.setText(model.getValueAt(i,1).toString());
                DescriptionField.setText(model.getValueAt(i,2).toString());
                holder.setText(model.getValueAt(i,4).toString());

                //UPLOAD IMAGE FIELD

            }
        });



        //ADD BOOK IS CLICKED
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addBookToDB();
            }
        });


        //UPDATE BOOK IS CLICKED
        updateBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!BookTable.getSelectionModel().isSelectionEmpty()) {//check if a row is selected

                    //check if a book name and a description is given
                    if (BookNameField.getText().isEmpty() || DescriptionField.getText().isEmpty()) {

                        JOptionPane.showMessageDialog(BookFormAdmin.this,
                                "Please fill all the fields to add a book",
                                "try again",
                                JOptionPane.ERROR_MESSAGE);

                    }else{

                        updateBook();
                    }

                }else {

                    JOptionPane.showMessageDialog(BookFormAdmin.this,
                            "Please select a row",
                            "Update failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        //DELETE BOOK IS CLICKED
        DeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("sala");
                deleteBook();
                System.out.println("mandra");


            }
        });




        //Upload Cover

        UploadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File f = chooser.getSelectedFile();


                //BookTable.getModel().getValueAt(i,3) =



                image = new File(f.getAbsolutePath());

                try{

                    fis = new FileInputStream(image);

                }catch (Exception c){//connection failed

                    c.printStackTrace();
                }

            }
        });


        //Logout button is pressed
        Logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                LoginForm open = new LoginForm();
                setVisible(false);

                dispose();

                open.setVisible(true);
            }
        });


        //Delete user
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(BookFormAdmin.this, "Would You Like to delete the specific user?", "Delete User", dialogButton);
                if(dialogResult == 0) {

                    if(!deleteUser(DeleteField1.getText())){// user found
                        //show success message
                        JOptionPane.showMessageDialog(BookFormAdmin.this,
                                "Deleted Successfully!" );

                    }else {

                        JOptionPane.showMessageDialog(BookFormAdmin.this,
                                "User not found!" );
                    }

                } else {

                    System.out.println("No Option");

                }
            }
        });
    }




    //DELETE USER
    private boolean deleteUser(String email){
        boolean found = true;

        try{//Connected

            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();


            Statement stmt = con.createStatement();

            //create a new user into database
            String sql = "SELECT * FROM user_info WHERE email=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,email);

            ResultSet resultSet = preparedStatement.executeQuery();

            //searching email in users table
            if(found && resultSet.next() && resultSet.getInt("user")==0 ){//resultSet.getInt("user")==0 so an admin cannot delete an admin

                    //UPDATE
                    Statement stmt2 = con.createStatement();
                    String sql2 = "SELECT * FROM books WHERE availability=?";
                    PreparedStatement preparedStatement2 = con.prepareStatement(sql2);
                    preparedStatement2.setString(1,email);
                    ResultSet books = preparedStatement2.executeQuery();

                    //searching for deleted user's email in books table ('availability')
                    if(books.next() ){

                        System.out.println("ssss");
                        String queryUpdate = "UPDATE books SET book_name=?, availability=?, book_info=? ,image=? where id="+books.getInt("id");
                        PreparedStatement preparedStatement3 = con.prepareStatement(queryUpdate);
                        preparedStatement3.setString(1,books.getString("book_name"));
                        preparedStatement3.setString(2,"Available"); // make book available again
                        preparedStatement3.setString(3,books.getString("book_info"));
                        preparedStatement3.setBytes(4,books.getBytes("image"));
                        preparedStatement3.executeUpdate();
                    }

                stmt2.close();

                String query = "DELETE FROM user_info where id=" + resultSet.getInt("id");
                PreparedStatement deleteUser = con.prepareStatement(query);
                deleteUser.executeUpdate();


                found = false;



                //Refresh table
                show_book();
            }

            stmt.close();
            con.close();

        }catch (Exception e){//connection failed

            e.printStackTrace();
        }

        return found;
    }




    //DELETE ROW
    private void deleteBook(){


        try{//Connected

            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();

            int i = BookTable.getSelectedRow();

            String value = (BookTable.getModel().getValueAt(i,0)).toString();// ID (CODE)

            Statement stmt = con.createStatement();

            //search for the specific book name
            String sql = "DELETE FROM books where id="+value;

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();

            //Refresh table
            show_book();

            //show success message
            JOptionPane.showMessageDialog(BookFormAdmin.this,
                    "Deleted Successfully!" );

            stmt.close();
            con.close();

        }catch (Exception e){//connection failed

            e.printStackTrace();
        }


    }



    // UPDATE ROW
    private  void updateBook() {


            try{//Connected

                //get connection
                GetConnection connection = new  GetConnection();
                con = connection.getConn();

                int i = BookTable.getSelectedRow();

                String value = (BookTable.getModel().getValueAt(i,0)).toString();// ID (CODE)


                Statement stmt = con.createStatement();

                //search for the specific book name
                String sql = "UPDATE books SET book_name=?, availability=?, book_info=? ,image=? where id="+value;

                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1,BookNameField.getText());
                preparedStatement.setString(2,holder.getText());
                preparedStatement.setString(3,DescriptionField.getText());

                if(fis != null) {// new image

                    preparedStatement.setBinaryStream(4,fis,(int) image.length());// image

                }else{

                    ArrayList<Books> list = booksArrayList();


                    preparedStatement.setBytes(4,list.get(i).getMyImage());

                }


                preparedStatement.executeUpdate();

                //Refresh table
                show_book();

                //show success message
                JOptionPane.showMessageDialog(BookFormAdmin.this,
                        "Updated Successfully!" );

                stmt.close();
                con.close();

            }catch (Exception e){//connection failed

                e.printStackTrace();
            }
    }


    //ADD BOOK FUNCTION
    private void addBookToDB() {

        //check if a book name and a description is given
        if(BookNameField.getText().isEmpty() || DescriptionField.getText().isEmpty()){

            JOptionPane.showMessageDialog(BookFormAdmin.this,
                    "Please fill all the fields to add a book",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);

        }else if (fis == null){ // check if image is uploaded

            JOptionPane.showMessageDialog(BookFormAdmin.this,
                    "Please choose an image",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);

        }else{

            try{//Connected

                //get connection
                GetConnection connection = new  GetConnection();
                con = connection.getConn();

                Statement stmt = con.createStatement();

                //create a new user into database
                String sql = "INSERT INTO books (book_name,availability,book_info,image)" + "VALUES(?,?,?,?)";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1,BookNameField.getText());
                preparedStatement.setString(2,holder.getText());
                preparedStatement.setString(3,DescriptionField.getText());
                preparedStatement.setBinaryStream(4,fis,(int) image.length());// image

                preparedStatement.executeUpdate();

                //Refresh table
                show_book();

                //show success message
                JOptionPane.showMessageDialog(BookFormAdmin.this,
                        "Inserted Successfully!" );

                stmt.close();
                con.close();

            }catch (Exception e){//connection failed

                e.printStackTrace();
            }

        }

    }






    public ArrayList<Books> booksArrayList(){

        ArrayList<Books> booksArrayList = new ArrayList<>();

        booksArrayList.clear();

        try{//Connected

            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();

            String query = "SELECT * FROM books";

            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(query);

            Books book;
            while(resultSet.next()){


                book = new Books(resultSet.getInt("id"), resultSet.getString("book_name"), resultSet.getString("book_Info"),resultSet.getString("availability"),resultSet.getBytes("image"));

                booksArrayList.add(book);
            }


            stmt.close();
            con.close();


        }catch (Exception e){//connection failed

            e.printStackTrace();
        }

        return booksArrayList;

    }




    public void show_book(){
        ArrayList<Books> list = booksArrayList();

        fis = null; //reset fis
        image = null;  //reset image path
        //DefaultTableModel model = (DefaultTableModel) BookTable.getModel();


        String[] columnName = {"Code","Book Name","Description","Image","Availability"};
        Object[][] rows = new Object[list.size()][5];
        for(int i = 0; i < list.size(); i++){
            rows[i][0] = list.get(i).getID();
            rows[i][1] = list.get(i).getBook_name();
            rows[i][2] = list.get(i).getAvailability();
            rows[i][4] = list.get(i).getBook_info();



            if(list.get(i).getMyImage() != null){

                ImageIcon image = new ImageIcon(new ImageIcon(list.get(i).getMyImage()).getImage()
                        .getScaledInstance(150, 120, Image.SCALE_SMOOTH) );

                rows[i][3] = image;
            }
            else{
                rows[i][3] = null;
            }

        }

        TheModel model = new TheModel(rows, columnName);
        BookTable.setModel(model);

        BookTable.setRowHeight(120);

        BookTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        BookTable.getColumnModel().getColumn(3).setPreferredWidth(150);


    }
}
