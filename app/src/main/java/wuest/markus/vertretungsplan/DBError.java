package wuest.markus.vertretungsplan;

//This Class is designed to throw DBErrors for exceptions in DBHandler!

public class DBError extends Exception{
    public DBError(String message){
        super(message);
    }

    //Valid messages are:
    public static final String DBEMPTY = "DB is empty";
    public static final String TABLEEMPTY = "Selected Table is empty";
    public static final String INVALIDDATEFORMAT = "Date incorrect";
    public static final String DBNOTAVALIABLE = "Not Avaliable";
    public static final String UNKNOWN = "Unknown Error";
    public static final String NOTFOUND = "Object was not found";
}
