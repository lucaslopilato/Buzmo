package BuzMo.Database;

import BuzMo.Logger.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * Created by Lucas Lopilato on 11/26/2016.
 * This class will attempt to write all tables to the database
 * based on the BuzMo Schema
 *
 * This is a helper class that is only used to initialize the database
 * Will not be used in later operation
 */
class CreateDatabase {
    private Logger log;
    private Connection connection;

     CreateDatabase(Logger log, Connection connection) throws DatabaseException{
        this.log = log;
        this.connection = connection;

         //Drop all tables before adding
         dropAllTables();

         //Import schema based on BuzMo Schematic
         writeAllTables();


        log.Log("Wrote BuzMo Schema to Connected Database");
    }

    //Attempt to write all tables in the BuzMo Schema to the database
    private void writeAllTables() throws DatabaseException{
        String users = "CREATE TABLE Users( " +
                "name VARCHAR(20)," +
                "phone_number CHAR(10)," +
                "email_address VARCHAR(20)," +
                "password VARCHAR(10)," +
                "screenname VARCHAR(20)," +
                "isManager BOOLEAN," +
                "PRIMARY KEY(email_address))";

        writeTable("Users", users);

        String CircleOfFriends = "CREATE TABLE CircleOfFriends(" +
                "user VARCHAR(20)," +
                "friend VARCHAR(20)," +
                "FOREIGN KEY(user) REFERENCES Users(email_address)," +
                "FOREIGN KEY(friend) REFERENCES users(email_address)," +
                "PRIMARY KEY (user,friend))";

        writeTable("CircleOfFriends", CircleOfFriends);


        String UserTopicWords = "CREATE TABLE UserTopicWords(" +
                "user VARCHAR(20)," +
                "word VARCHAR(20)," +
                "FOREIGN KEY(user) REFERENCES Users(email_address)," +
                "PRIMARY KEY (user,word))";

        writeTable("UserTopicWords", UserTopicWords);


        String Messages = "CREATE TABLE Messages(" +
                "message_id INTEGER," +
                "timestamp VARCHAR(20)," +
                "sender VARCHAR(20)," +
                "PRIMARY KEY(message_id))";

        writeTable("Messages", Messages);

        String MessageTopicWords = "CREATE TABLE MessageTopicWords(" +
                "message_id INTEGER," +
                "word VARCHAR(20)," +
                "FOREIGN KEY(message_id) REFERENCES Messages(message_id)," +
                "PRIMARY KEY(message_id, word))";

        writeTable("MessageTopicWords", MessageTopicWords);

        String MessageReceivers = "CREATE TABLE MessageReceivers(" +
                "message_id INTEGER," +
                "recipient VARCHAR(20)," +
                "FOREIGN KEY(message_id) REFERENCES Messages(message_id)," +
                "FOREIGN KEY(recipient) REFERENCES Users(email_address)," +
                "PRIMARY KEY(message_id, recipient))";

        writeTable("MessageReceivers", MessageReceivers);

        String ChatGroups = "CREATE TABLE ChatGroups(" +
                "owner VARCHAR(20)," +
                "group_name VARCHAR(20)," +
                "duration INTEGER," +
                "PRIMARY KEY (group_name)," +
                "FOREIGN KEY(owner) REFERENCES Users(email_address))";

        writeTable("ChatGroups", ChatGroups);

        String ChatGroupMembers = "CREATE TABLE ChatGroupMembers(" +
                "group_name VARCHAR(20)," +
                "member VARCHAR(20)," +
                "FOREIGN KEY(group_name) REFERENCES ChatGroups(group_name)," +
                "FOREIGN KEY(member) REFERENCES Users(email_address)," +
                "PRIMARY KEY(group_name, member))";

        writeTable("ChatGroupMembers", ChatGroupMembers);

    }


    //Attempt to write the Table tableName with SQL statement
    //If the table already exists, will log the occurrence and fail silently
    //Otherwise throws database exception
    private void writeTable(String tableName, String sql) throws DatabaseException{
        try{
            Statement st = connection.createStatement();
            st.execute(sql);
            log.Log("wrote "+tableName+" to the database");
        }
        catch(SQLException e){
            if(e.getMessage().compareTo("Table '" + tableName.toLowerCase()+ "' already exists") == 0){
                log.Log(e.getMessage());
            }
            else{
                throw new DatabaseException("Error writing tables to the database",e);
            }
        }
    }

    //Make an attempt to drop all tables. If tables
    //Are not present, fail silently
    private void dropAllTables(){
        Vector<String> tables = new Vector<>();
        tables.add("users");
        tables.add("circleoffriends");
        tables.add("usertopicwords");
        tables.add("messages");
        tables.add("messagetopicwords");
        tables.add("messagereceivers");
        tables.add("chatgroups");
        tables.add("chatgroupmembers");

        //Try to drop each table and fail silently if not found
        for(String s : tables) {
            dropTable(s);
        }

        log.Log("All BuzMo Tables dropped");
    }

    //Attempt to drop table, fail silently if error occurs
    private void dropTable(String name){
        try{
            Statement st = connection.createStatement();
            st.execute("DROP TABLE "+name);
            log.Log("Successfully dropped table "+name);
        }
        catch(SQLException sql){
            log.Log("Error dropping table: "+ sql.getMessage());
        }
    }



}
