package model.bo;

import model.bean.SubmissionBean;
import model.dao.SubmissionDAO;

public class SubmissionBO {
    private final SubmissionDAO dao = new SubmissionDAO();

    public int createSubmission(SubmissionBean s) {
        try {
            return dao.insert(s);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
