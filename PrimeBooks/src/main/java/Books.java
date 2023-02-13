public class Books {


    private int id;
    private String book_name;
    private String availability;
    private String book_info;
    private byte[] Image;

    public Books(){}

    public Books(int Id, String book_name, String availability, String book_info ,byte[] image){

        this.id = Id;
        this.book_name = book_name;
        this.availability = availability;
        this.book_info = book_info;
        this.Image = image;

    }

    public int getID(){
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getBook_name(){
        return book_name;
    }

    public void setBook_name(String book_name){
        this.book_name = book_name;
    }

    public String getAvailability(){
        return availability;
    }

    public void setAvailability(String availability){
        this.availability = availability;
    }

    public String getBook_info(){
        return book_info;
    }

    public void setBook_info(String book_info){
        this.book_info = book_info;
    }


    public byte[] getMyImage(){
        return Image;
    }
}

