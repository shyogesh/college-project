package jobportal.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import jobportal.dao.UserDao;
import jobportal.model.User;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/users")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		List<User> userList = UserDao.getUsers();
		request.setAttribute("users", userList);		
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = saveImage(request,response);
			
		
		if(user.getUserId() !=0 ){
			UserDao.updateUser(user);
		}else {
			UserDao.saveUser(user);
		}

		List<User> userList = UserDao.getUsers();
		request.setAttribute("users", userList);		
		request.getRequestDispatcher("home.jsp").forward(request, response);
	}

	private User saveImage(HttpServletRequest request, HttpServletResponse response) { 
        //process only if its multipart content
		String UPLOAD_DIRECTORY = "/images/user/";
		UPLOAD_DIRECTORY=request.getContextPath()+UPLOAD_DIRECTORY;
		System.out.println(UPLOAD_DIRECTORY);
		File  uploadFile = null;
		User user = new User();
        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);              
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = new File(item.getName()).getName();
                        File uploadDir = new File(UPLOAD_DIRECTORY) ;
                        uploadDir.mkdirs();
                        uploadFile =  new File(uploadDir, File.separator + name);
                        user.setImgPath(uploadFile.getPath().replace("\\", "\\\\"));
                        item.write(uploadFile);
                    } else if(item.isFormField()){
                    	String fieldName = item.getFieldName();
                        String fieldValue = item.getString();
                        if(fieldName.equalsIgnoreCase("userid") && fieldValue != null && fieldValue.length() > 0){
                        	user.setUserId(Integer.parseInt(fieldValue));
                        } else if(fieldName.equalsIgnoreCase("username")){
                        	user.setUsername(fieldValue);
                        } else if(fieldName.equalsIgnoreCase("password")){
                        	user.setPassword(fieldValue);
                        } else if(fieldName.equalsIgnoreCase("email")){
                        	user.setEmail(fieldValue);
                        }
                        
                        
                    }
                }
                if(user.getImgPath() == null || user.getImgPath().trim() ==""){
                	user.setImgPath(UPLOAD_DIRECTORY + File.separator + "default.jpg");
                }
               //File uploaded successfully
               request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
               request.setAttribute("message", "File Upload Failed due to " + ex);
            }          
         
        }else{
            request.setAttribute("message",
                                 "Sorry this Servlet only handles file upload request");
        }
		return user;
	}

}


