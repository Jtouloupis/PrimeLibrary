import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Random;

public class RegistrationForm extends JDialog {
    private JPasswordField passwordField2;
    private JPasswordField passwordField1;
    private JTextField emailField;
    private JButton cancelButton;
    private JButton registerButton;
    private JPanel registerPanel;
    public static String email;
    private Connection con;


    public RegistrationForm(){

        setTitle("Create a new accoung");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450,475));
        setLocationRelativeTo(null); // center it
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        email = null;


        //Register button is clicked
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                  registerUser();
            }
        });


        //Cancel Button is clicked
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LoginForm log = new LoginForm();
                dispose();

                log.setVisible(true);

            }
        });


    }




    private void registerUser() {

        String email = emailField.getText();
        String password = String.valueOf(passwordField1.getPassword());
        String passwordConf = String.valueOf(passwordField2.getPassword());;


        //check if fields are empty

        if(email.isEmpty() || password.isEmpty() || passwordConf.isEmpty()){//empty field exists
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Check if passwords match

        if(!password.equals((passwordConf))){//not matched

            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }




        if(userExist(email) || email.equals("Available")){ //if email already exists          or      if email == "Available" (chaos will prevail in client form)

            JOptionPane.showMessageDialog(this,
                    "Email already exists",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);

        }else{//ADD USER TO DATABASE
            Encryptor encryptor = new Encryptor();

            String salt = giveSalt();

            try {

                //pass variables and create SHA-256 hash with salts
                adduUserToDatabase(email,encryptor.encryptString(password,email,salt),salt);



            } catch (NoSuchAlgorithmException e) {

                System.out.println("Registration canceled");

                e.printStackTrace();

            }


        }

    }

    //give a random string
    public String giveSalt() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }



    //check if email already exists
    private boolean userExist(String email){

        boolean emailExists = false;

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

            //search table
            if(resultSet.next()){

                emailExists = true;

            }

            stmt.close();
            con.close();

        }catch (Exception e){//connection failed

            e.printStackTrace();
        }

        return emailExists;
    }



    private void adduUserToDatabase(String email, String password,String salt) {

        try{//Connected

            //get connection
            GetConnection connection = new  GetConnection();
            con = connection.getConn();

            Statement stmt = con.createStatement();

            //create a new user into database
            String sql = "INSERT INTO user_info (email,password,salt)" + "VALUES(?,?,?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            preparedStatement.setString(3,salt);

            preparedStatement.executeUpdate();

            stmt.close();
            con.close();


            System.out.println("Successful registration of :"+ email);

            ClientBookForm c = new ClientBookForm(email);
            dispose();

            c.setVisible(true);

        }catch (Exception e){//connection failed


            System.out.println("Registration canceled");

            e.printStackTrace();
        }


    }


    public static void main(String[] args){
       RegistrationForm myForm = new RegistrationForm();
       myForm.setVisible(true);


    }

}
