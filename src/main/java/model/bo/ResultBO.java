package model.bo;

import model.bean.ResultBean;
import model.dao.ResultDAO;

/**
 * Business Object for Result. Encapsulates business logic and uses ResultDAO for persistence.
 */
public class ResultBO {
    private final ResultDAO dao = new ResultDAO();

    public ResultBean getResultForSubmission(int submissionId) {
        try {
            return dao.findBySubmissionId(submissionId);
        } catch (Exception ex) {
            // In real app, use logger
            ex.printStackTrace();
            return null;
        }
    }

    public int createOrUpdateResult(ResultBean r) {
        try {
            // For simplicity, always insert a new result row. Production logic may
            // upsert or reuse existing result entries based on sha256 or submissionId.
            return dao.insert(r);
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public boolean deleteResultForSubmission(int submissionId) {
        try {
            return dao.deleteBySubmissionId(submissionId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public java.util.List<model.bean.ResultWithSubmissionBean> listResults(Integer userId, String role, String guestToken, String batchToken) {
        try {
            return dao.listResultsWithSubmission(userId, role, guestToken, batchToken);
        } catch (Exception ex) {
            ex.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}
