package model.bo;

import model.bean.DocumentBean;
import model.dao.DocumentDAO;

import java.util.List;

public class DocumentBO {
    private final DocumentDAO dao = new DocumentDAO();

    public boolean deleteDocument(int documentId, int ownerId) {
        try {
            return dao.deleteByIdAndOwner(documentId, ownerId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public DocumentBean getDocument(int id) {
        try {
            return dao.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DocumentBean> listDocumentsByOwner(int ownerId) {
        try {
            return dao.listByOwner(ownerId);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public boolean documentExists(int ownerId, String filename) {
        try {
            return dao.existsByOwnerAndName(ownerId, filename);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveDocument(int ownerId, String filename, String filepath, long size, String mimeType) {
        try {
            return dao.saveDocument(ownerId, filename, filepath, size, mimeType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<DocumentBean> listDocumentsForAdmins() {
        try {
            return dao.listDocumentsByAdminOwners();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}
