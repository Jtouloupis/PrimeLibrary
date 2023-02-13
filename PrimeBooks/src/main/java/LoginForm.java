import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JDialog{
    private JTextField emailField1;
    private JButton loginButton;
    private JPasswordField passwordField1;
    private JPanel loginPanel;
    private JLabel CreateAnAccount;
    private Connection con;


    public LoginForm(){
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,475));
        setLocationRelativeTo(null); // center it
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String email = emailField1.getText();
                String password = String.valueOf(passwordField1.getPassword());




                if(getAuthenticatedUser(email,password) == 1){//go to BookFormAdmin

                    BookFormAdmin admin = new BookFormAdmin();
                    dispose();
                    setVisible(false);
                    admin.setVisible(true);

                }else if(getAuthenticatedUser(email,password) == 0){ //go to Client User


                    ClientBookForm client = new ClientBookForm(email);
                    dispose();
                    setVisible(false);
                    client.setVisible(true);


                }else {

                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Passwords or Email is Invalid",
                            "try again",
                            JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        CreateAnAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                RegistrationForm reg = new RegistrationForm();
                LoginForm log = new LoginForm();

                log.dispose();
                setVisible(false);

                reg.setVisible(true);

            }
        });

    }




    private int getAuthenticatedUser(String email, String password){
        int whichUser = -1;


        try{//Connected

            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();

            Statement stmt = con.createStatement();

            //create a new user into database
            String sql = "SELECT * FROM user_info WHERE email=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,email);
            //preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                Encryptor encryptor = new Encryptor();

                //compare hash password with the given password
                if(encryptor.encryptString(password,email,resultSet.getString("salt")).equals(resultSet.getString("password"))){

                    whichUser = resultSet.getInt("user");// get admin(1) or client (0)

                }

            }

            stmt.close();
            con.close();


        }catch (Exception e){//connection failed

            e.printStackTrace();
        }


        return whichUser;
    }




    public static void main(String[] args){
        LoginForm myForm = new LoginForm();
        myForm.setVisible(true);


    }

}
