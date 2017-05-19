/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package yelp;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.toIntExact;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;



class DbConnection {

    static Connection sampleDBconn = null;
   
    Connection conn = null;
     
    public Connection getConnection(){
      String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver"; 
       String DB_URL = "jdbc:mysql://localhost/";
   
         Statement stmt = null;
             String user = "vishrutshah15";
                String password = "Rutva1526#";
                String port = "1521";
                String DBname = "SYSTEM";
                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                }
                catch(ClassNotFoundException e){
                    System.out.println("Connection failed!" + e);
                    return null;
                }
                try{
                    System.out.println("Connecting to database...");
                      conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", user, password);
                      System.out.println("Creating database...");
                     System.out.println("Database created successfully...");
                     return conn;
//                      
                     
                     
                } catch(SQLException se){
                    System.out.println("Connection failed!" + se);
                      se.printStackTrace();
                   }catch(Exception e){
                      
                      e.printStackTrace();
               
                 }
            return null;
        }
    }

public class populate {
    
   
    public static void main(String[] args) throws FileNotFoundException, ParseException, IOException, java.text.ParseException {
        try {
            
            
            DbConnection A1 = new DbConnection();
            Connection con = A1.getConnection();
            
            
            JSONParser jsonParser;
            jsonParser = new JSONParser();
            
            Object obj1 = jsonParser.parse(new FileReader("C:\\Users\\Sanjay Desai\\Desktop\\yelp_user.json"));
            Object obj2 = jsonParser.parse(new FileReader("C:\\Users\\Sanjay Desai\\Desktop\\yelp_business.json"));
            Object obj3 = jsonParser.parse(new FileReader("C:\\Users\\Sanjay Desai\\Desktop\\yelp_review.json"));
            Object obj4 = jsonParser.parse(new FileReader("C:\\Users\\Sanjay Desai\\Desktop\\yelp_checkin.json"));
            
      
            
            JSONArray jsonArray1;
            jsonArray1 = (JSONArray)obj1;
            
            JSONArray jsonArray2;
            jsonArray2 = (JSONArray)obj2;
            
            JSONArray jsonArray3;
            jsonArray3 = (JSONArray)obj3;
            
            JSONArray jsonArray4;
            jsonArray4 = (JSONArray)obj4;
            
            // yelp_user
            String yelping_since,name1,user_id,type1;
            Long review_count1,fans;
            Double average_stars;
            Statement stmt;
            
            stmt = con.createStatement();
            stmt.executeUpdate("Delete from N_User");
                    
            for(int i=0;i<(jsonArray1.size());i++)
           
            {
            JSONObject jsonObject = (JSONObject) jsonArray1.get(i);
            yelping_since = (String) jsonObject.get("yelping_since") + "-01"; 
            
            JSONArray friends = (JSONArray) jsonObject.get("friends");
            int friends_size = friends.size();

            
            review_count1 = (Long) jsonObject.get("review_count");
            name1 = (String) jsonObject.get("name");
            user_id = (String) jsonObject.get("user_id");
            fans = (Long) jsonObject.get("fans");
            average_stars = (Double) jsonObject.get("average_stars");
            type1 = (String) jsonObject.get("type");
            
            
            
            
            
            
                try (PreparedStatement pstmt1 = con.prepareStatement("Insert INTO N_User(yelping_since,friends_size,review_count,name,user_id,fans,average_stars,type) VALUES(?,?,?,?,?,?,?,?)")) {

                    SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );  
                    java.util.Date myDate = format.parse(yelping_since);  

                    pstmt1.setDate(1, new java.sql.Date(myDate.getTime()));
                    pstmt1.setInt(2,friends_size);
                    pstmt1.setLong(3,review_count1);
                    pstmt1.setString(4,name1);
                    pstmt1.setString(5,user_id);
                    pstmt1.setLong(6,fans);
                    pstmt1.setDouble(7,average_stars);
                    pstmt1.setString(8,type1);
                    pstmt1.executeUpdate();
                } catch (java.text.ParseException ex) {
                    Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }   
             
            
            //yelp_business
            
            String business_id,address,city,state,name,type_business;
            
            Double stars;
            
            
            for(int i=0;i<jsonArray2.size();i++)
            {
            JSONObject jsonObject = (JSONObject) jsonArray2.get(i);
            business_id = (String) jsonObject.get("business_id");
            address = (String) jsonObject.get("full_address");
            city = (String) jsonObject.get("city");
            state = (String) jsonObject.get("state");
            name = (String) jsonObject.get("name");
            stars = (Double) jsonObject.get("stars");
            type_business = (String) jsonObject.get("type");
            
            
            
            
                try (PreparedStatement pstmt2 = con.prepareStatement("Insert INTO N_Business(business_id,address,city,state,name,stars,type_business) VALUES(?,?,?,?,?,?,?)")) {
                    pstmt2.setString(1,business_id);
                    pstmt2.setString(2,address);
                    pstmt2.setString(3,city);
                    pstmt2.setString(4,state);
                    pstmt2.setString(5,name);
                    pstmt2.setDouble(6,stars);
                    pstmt2.setString(7,type_business);
                    pstmt2.executeUpdate();
                    pstmt2.close();
                }
           
            }
           
            //Category Table
            String[] categories ={"Active Life","Arts & Entertainment","Automotive","Car Rental","Cafes","Beauty & Spas","Convenience Stores",
           "Dentists","Doctors","Drugstores","Department Stores","Education","Event Planning & Services","Flowers & Gifts","Food","Health & Medical",
           "Home Services","Home & Garden","Hospitals","Hotels & travel","Hardware stores","Grocery","Medical Centers","Nurseries & Gardening","Nightlife",
           "Restaurants","Shopping","Transportation"};
            
            
            JSONArray category;
            String[] individual_category = new String[100];
            int count = 0, flag=0, m=0, n=0;
            String[] business_category = new String[50];
            String[] subcategory = new String[50];
            
            
            for(int i=0;i<jsonArray2.size();i++){
                JSONObject jsonObject3 = (JSONObject) jsonArray2.get(i);
                 String business_id2 = (String) jsonObject3.get("business_id");
                category = (JSONArray)jsonObject3.get("categories");
                for(int j=0;j<category.size();j++){
                    individual_category[j] = (String)category.get(j);
                    count = count + 1;
                }
                for(int k=0; k<count; k++){
                    for (String categorie : categories) {
                       
                        if (individual_category[k].equals(categorie)) {
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 1){
                        business_category[m] = individual_category[k];
                        m = m + 1;
                        flag = 0;
                    }
                    else{
                        subcategory[n] = individual_category[k];
                        n = n + 1;
                    }
                }
                for(int p=0;p<m;p++){
                    for(int q=0; q<n; q++){
                        try (PreparedStatement pstmt3 = con.prepareStatement("INSERT INTO N_Category(business_id,category,subcategory) VALUES(?,?,?)")) {
                            pstmt3.setString(1,business_id2);
                            pstmt3.setString(2, business_category[p]);
                            pstmt3.setString(3, subcategory[q]);
                            pstmt3.executeUpdate();
                           
                        }
                    }
                } 
                count = 0;
                m = 0;
                n = 0;
            }
            
            //yelp_review
            
            String user_id3,review_id,type3,business_id3,text,text1,review_date;
            Long stars3;
            int votes = 0;
            Integer no_votes;
            
           JSONObject votes_info;
           Set<String> keys;
            
            
            for(int i=0;i<jsonArray3.size();i++)
            {
            JSONObject jsonObject = (JSONObject) jsonArray3.get(i);
            
            votes_info = (JSONObject) jsonObject.get("votes");
                keys = votes_info.keySet();
                for(String r_key:keys){
                    votes = (int) (votes + (Long)votes_info.get(r_key));   
                }
                no_votes = toIntExact(votes);
            user_id3 = (String) jsonObject.get("user_id");
            
            review_id = (String) jsonObject.get("review_id");
            business_id3 = (String) jsonObject.get("business_id");
            review_date = (String) jsonObject.get("date");
            text1 = (String) jsonObject.get("text");
            text= text1.substring(0,Math.min(1000,text1.length()));
            stars3 = (Long) jsonObject.get("stars");
            type3 = (String) jsonObject.get("type");
            
                try (PreparedStatement pstmt4 = con.prepareStatement("Insert INTO N_Review(no_votes,user_id,review_id,business_id,review_date,text,stars,type) VALUES(?,?,?,?,?,?,?,?)")) {
                    SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );  
                    java.util.Date myDate = format.parse(review_date);  

                    
                    pstmt4.setInt(1,no_votes);
                    pstmt4.setString(2,user_id3);
                    pstmt4.setString(3,review_id);
                    pstmt4.setString(4,business_id3);
                    pstmt4.setDate(5, new java.sql.Date(myDate.getTime()));
                    pstmt4.setString(6,text);
                    pstmt4.setLong(7,stars3);
                    pstmt4.setString(8,type3);
                    pstmt4.executeUpdate();
                    pstmt4.close();
                }
                    
            }
            
            //Checkin_Info
            JSONObject checkin_info;
            String business_id4;
            Long check_in_count;
            Set<String> keys1;
            String[] timing = new String[10];
            int n1=0,time,hour;
            
            
            //Inserting into checkin_info
            for(int i=0;i<jsonArray4.size();i++){
                JSONObject jsonObject4 = (JSONObject) jsonArray4.get(i);
                checkin_info = (JSONObject) jsonObject4.get("checkin_info");
                business_id4 = (String)jsonObject4.get("business_id");
                keys1 = checkin_info.keySet();
                
                for(String key:keys1){
                    check_in_count = (Long) checkin_info.get(key);
                    for (String x: key.split("-")) {
                        timing[n1] = x;
                        n1 = n1+1;
                    }
                    n1=0;
                    hour = Integer.parseInt(timing[0]);
                    time = Integer.parseInt(timing[1]);
                    
                    try (PreparedStatement pstmt5 = con.prepareStatement("INSERT INTO check_info(business_id,hour,day,check_in_count)VALUES(?,?,?,?)")) { 
                        pstmt5.setString(1, business_id4);
                        pstmt5.setInt(2, hour);
                        pstmt5.setInt(3,time);
                        pstmt5.setLong(4,check_in_count);
                        pstmt5.executeUpdate();
                    }
                    }
                    
                } 
            
        
       
            
          
                con.close();
                
        } 
        catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            
        
        }
}
