package model.bean;

import java.time.Instant;

public class SubmissionBean {
    private int id;
    private String batchToken;
    private String guestToken;
    private Integer userId;
    private String filename;
    private String rawContent;
    private String cleanedContent;
    private String status;
    private int stackOrder;
    private long uploadSize;
    private Instant uploadTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBatchToken() { return batchToken; }
    public void setBatchToken(String batchToken) { this.batchToken = batchToken; }
    public String getGuestToken() { return guestToken; }
    public void setGuestToken(String guestToken) { this.guestToken = guestToken; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    public String getCleanedContent() { return cleanedContent; }
    public void setCleanedContent(String cleanedContent) { this.cleanedContent = cleanedContent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getStackOrder() { return stackOrder; }
    public void setStackOrder(int stackOrder) { this.stackOrder = stackOrder; }
    public long getUploadSize() { return uploadSize; }
    public void setUploadSize(long uploadSize) { this.uploadSize = uploadSize; }
    public Instant getUploadTime() { return uploadTime; }
    public void setUploadTime(Instant uploadTime) { this.uploadTime = uploadTime; }
}
