package model.bo;

import model.bean.UserBean;
import model.dao.UserDAO;

public class UserBO {
    private final UserDAO dao = new UserDAO();

    public UserBean authenticate(String username, String password) {
        try {
            return dao.findByUsernameAndPassword(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer authenticateAdmin(String username, String password) {
        try {
            return dao.findAdminId(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
