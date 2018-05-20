import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.membershipsystem.Data.*;
public class ThreadedDataObjectServer{  
	public static void main(String[] args ) {  	
      try {  
    	  ServerSocket s = new ServerSocket(3210);
			System.out.println("Server now online at port 3210");
         for (;;)
         {  Socket incoming = s.accept( );
			System.out.println("Got something!");
            new ThreadedDataObjectHandler(incoming).start();    
	   	 }   
      }
      catch (Exception e){  
    	  System.out.println(e);
      } 
		System.out.println("Server shutting down.");
   } 
}

class ThreadedDataObjectHandler extends Thread {  
	
	
	public ThreadedDataObjectHandler(Socket i) 
   { 
   		incoming = i;
   }
   
   public void run()
   {  try 
      {
		boolean sendToClient=true;
		boolean done = false; 	

		in = new ObjectInputStream(incoming.getInputStream());
		out = new ObjectOutputStream(incoming.getOutputStream());
		dbconn = new dbConnection();


	  myObject = (DataObject)in.readObject();
	  if(myObject.getMessage().equals("Admin Info Request")){
		  myObject = getAdminInfo(myObject);
		  out.writeObject(myObject);
		  
	  }
	  if(myObject.getMessage().equals("New User")){

		  myObject.setMessage("Send Credentials");
		  out.writeObject(myObject);
		  
		  myUser = (UserObject) in.readObject();
		  if(myUser.getMessage().equals("Credentials Sent")){

		      System.out.println("New User Request: " + myUser.getUsername());
		      myUser = createUser(myUser);
		      if(myUser.getStatus()!=3){
		    	  out.writeObject(myUser);
		      }
		      username = myUser.getUsername();
		  }

	  }else{
		  String[] res = myObject.getMessage().split(",");
		  username = res[0];
	      password = res[1];
	      System.out.println("Received: "+username+" "+password);  
	      myUser = new UserObject();
	      myUser.setUsername(username);
	      myUser.setPassword(password);
	      myUser.setStatus(1);
	  }
	  
      myUser = checkCredentials(myUser);
      int found = myUser.getStatus();
      System.out.println("Found "+found);
      if(found == 3 || found == 4){
    	  System.out.println(username);
			System.out.println(incoming);
			//streams.put(clientUsername, OUT);
//			OUT.writeUnshared(myUser);
//			OUT.flush();
			out.writeObject(myUser);
			System.out.println(username + " has successfully logged in.");
			while(!done) {
				//Maintain connection for requests and processing
				System.out.println("Waiting for a command from " + username);
			    System.out.println("---------------------------------------");

				myUser = (UserObject) in.readObject();

				//int userID = myUser.getUserID();
			    username = myUser.getUsername();
			    //String name = myUser.getName();
			    password = myUser.getPassword();
			    //int status = myUser.getStatus();
			    String operation = myUser.getOperation();
//			   // String message = myUser.getMessage();
				System.out.printf("Request coming in from %s for: %s\n", username, operation);
//
				UserObject userOut = copyUserObject(myUser);
				//userOut.setUserID(userID);
				//userOut.setUsername(username);
				//userOut.setPassword(password);
				//userOut.setOperation(operation);
//				//userOut.setMessage(message);
				sendToClient=true;

				
				if (operation.equals("Updated")) {
					//printAll(userOut);
					userOut = updateInfo(userOut);
					//userOut.setOperation("Text");
					//userOut = setExample(userOut, "UserOut 1");
				}
				else if (operation.equals("Search")){
					userOut = getSearch(userOut);
				}
				else if(operation.contains("Delete")){
					userOut = deleteUser(userOut);
					done = true;
				}
				else if (operation.equals("Log Out")) {
					//logOut(userOut, username);
					System.out.println("Logging out");
					userOut.setOperation("Logged Out");
					done = true;
					sendToClient = true;
					//streams.remove(username);
					//continue;
				} else {
					System.out.printf("Empty object came from %s", username);
				}
				if(sendToClient){
	            out.writeUnshared(userOut);
				out.flush();
				System.out.println("Response sent out.");
				}
			}
			System.out.printf("%s has logged out\n", username);
			System.out.println("_______________________________________");
    	    
      }
      else {
			System.out.println(incoming.getLocalAddress().getHostAddress() + "(" + username + ") failed to log in with wrong username or password.");
		}
		// Close the connection.
		rs.close();
		in.close();
		out.close();
		incoming.close();
	} catch (EOFException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
 
   }
   
   public void printAll(UserObject u){
	   
	   System.out.println(u.getUsername()+" "+u.getName()+" "+u.getPassword()+"\n"+u.getOperation()+" "+u.getPhoneNum());
   }
   
   public UserObject copyUserObject(UserObject u){
	  
	    int status, userID;
	    String name, operation, username, password, mes, message, dob, phoneNum, email, address;
	    
	    status = u.getStatus();
	    userID = u.getUserID();
	    name = u.getName();
	    operation = u.getOperation();
	    username = u.getUsername();
	    password = u.getPassword();
	    mes = u.getMes();
	    message = u.getMessage();
	    dob = u.getDob();
	    phoneNum = u.getPhoneNum();
	    email = u.getEmail();
	    address = u.getAddress();
	   
	    
	    UserObject newObj = new UserObject();
	    
	    newObj.setStatus(status);
	    newObj.setUserID(userID);
	    newObj.setName(name);
	    newObj.setOperation(operation);
	    newObj.setUsername(username);
	    newObj.setPassword(password);
	    newObj.setMes(mes);
	    newObj.setMessage(message);
	    newObj.setDob(dob);
	    newObj.setPhoneNum(phoneNum);
	    newObj.setEmail(email);
	    newObj.setAddress(address);
	    
	    return newObj;
	   
   }
   
   public UserObject createUser(UserObject u) throws ClassNotFoundException, IOException, SQLException{
	   boolean ret = false;

	   //check if username already exists
	   rs = dbconn.executeSQL("select count(*) from Users where username=\""+u.getUsername()+"\";");

	   rs.next();
	   if(rs.getInt(1) != 0){
		   System.out.println("User already exists!");
		   u.setStatus(5);
		   return u;
	   }
	      

	   ret = dbconn.executeUpdate("insert into Users(username, password, name, birth_date, phone_num, email,"
	   		+ " address) values(\""+u.getUsername()+"\",\""+u.getPassword()+"\",\""+u.getName()+"\",\""
			   +u.getDob()+"\",\""+u.getPhoneNum()+"\",\""+u.getEmail()+"\",\""+u.getAddress()+"\");");

	   if(u.getStatus() == 4){
		   int id;
		   rs = dbconn.executeSQL("select user_id from Users where username=\""+u.getUsername()+"\";");
		   rs.next();
		   id = rs.getInt("user_id");
		   
		   ret = dbconn.executeUpdate("insert into Admins values("+id+");");
		   
	   }
	   if(ret){
			   System.out.println("User created Successfully!");
			   u.setStatus(3);
		}else{
			System.out.println("User not created!");
			   u.setStatus(6);
		}
	   
	   return u;
   }
   
   public UserObject addNames(UserObject u) throws ClassNotFoundException, IOException, SQLException {
	   
	   
	   String names="", usernames="";
	   rs = dbconn.executeSQL("select username,name from Users;");
	   while(rs.next()){
		   names =  rs.getString("name") + "," + names;
		   usernames = usernames + "," + rs.getString("username");
	   }
	  // System.out.println("First "+names+"\n"+usernames);
	   
	   u.setMes(names);
	   u.setMessage(usernames);
	   
	   return u;
   }
   
   
   public UserObject checkCredentials(UserObject user) throws ClassNotFoundException, IOException, SQLException {

	   		user.setUserID(-1);
			rs = dbconn.executeSQL("select * from Users;");
			boolean found = false;
			while(rs.next() && !found)
			{
				if (user.getUsername().equals(rs.getString("username")) && user.getPassword().equals(rs.getString("password"))) {
					user.setUsername(rs.getString("username"));
					user.setName(rs.getString("name"));
					user.setUserID(rs.getInt("user_id"));
					user.setDob(rs.getString("birth_date"));
					user.setPhoneNum(rs.getString("phone_num"));
					user.setEmail(rs.getString("email"));
					user.setAddress(rs.getString("address"));
					System.out.println(user.getAddress());
					user.setStatus(3);
					found = true;
				}
			}
			rs = dbconn.executeSQL("select * from Admins where user_id="+ user.getUserID() +";");
			if(rs.next()){
				user.setStatus(4);
			}
			
			user = addNames(user);

			return user;
	}
   
   UserObject updateInfo(UserObject u){
	   
	   boolean ret;
	   ret = dbconn.executeUpdate("update Users set name=\""+u.getName()+"\",birth_date=\""+u.getDob()+"\",phone_num=\""
			   +u.getPhoneNum()+"\",email=\""+u.getEmail()+"\",address=\""+u.getAddress()+"\" where username=\""+u.getUsername()+"\";");
	   if(ret){ u.setOperation("Update Successful");}
	   else { u.setOperation("Update Unsuccessful");}
	   
	   return u;
   }
   
   UserObject getSearch(UserObject user) throws ClassNotFoundException, IOException, SQLException {
	   String[] res = user.getMes().split(":");
	   String message = "";
	   	   
	   rs = dbconn.executeSQL("select * from Users where "+res[0]+"=\""+res[1]+"\";");
		if(rs.next())
		{
				message = rs.getString("username") + "-@-" + rs.getString("name") + "-@-" + rs.getString("birth_date") 
					+ "-@-" + rs.getString("phone_num") + "-@-" + rs.getString("email") + "-@-" + rs.getString("address");
			
				user.setMes(message);
				user.setOperation("Search Result");
		}else{
			user.setOperation(res[1]+" does not exist in our database!");
		}
	   
		//System.out.println("Results: "+message);
	    
	   
	   return user;
	   
   }
   
   UserObject deleteUser(UserObject u) throws ClassNotFoundException, IOException, SQLException {
	   
	   boolean ret;
	   String names="", usernames="";
	   dbconn.executeUpdate("delete from Admins where user_id=(select user_id from Users where username=\""+ u.getMes() +"\");");
	   ret = dbconn.executeUpdate("delete from Users where username=\""+ u.getMes() +"\";");
	   if(ret){
			System.out.println("Delete Successful! ");
			u.setMes("Delete Successful");
	   }else{
			System.out.println("Delete Unsuccessful! ");
			u.setMes("Delete Unsuccessful");
	   }
		
	    rs = dbconn.executeSQL("select username,name from Users;");
	    while(rs.next()){
			names =  rs.getString("name") + "," + names;
			usernames = usernames + "," + rs.getString("username");
	    }
	    System.out.println("First "+names+"\n"+usernames);
	    u.setMessage(names + "---" + usernames);	
			
	   return u;
   }
   
   DataObject getAdminInfo(DataObject d) throws ClassNotFoundException, IOException, SQLException{
	   
	   String usernames="", passwords="";
	   
	   rs = dbconn.executeSQL("select username,password from Users where user_id IN (Select * from Admins);");
	    while(rs.next()){
			usernames += "," + rs.getString("username");
			passwords += "," + rs.getString("password");
	    }
	    
	    System.out.println("This: "+usernames+" "+passwords);
	   d.setMessage(usernames + "---" + passwords);
	   return d;
   }
   
   DataObject myObject = null;
   UserObject myUser = null;
   private Socket incoming;
   dbConnection dbconn = null;
   ObjectInputStream in = null;
   ObjectOutputStream out = null;
   ResultSet rs = null;
   String username, password;

   
}