package model.bean;

import java.sql.Timestamp;

public class ResultWithSubmissionBean {
    private int resultId;
    private int submissionId;
    private String filename;
    private String submissionStatus;
    private double simWinnowing;
    private double simTfidf;
    private String matchedSegmentsJson;
    private String status;
    private String sourceDocument;
    private Timestamp uploadTime;

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }
    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getSubmissionStatus() { return submissionStatus; }
    public void setSubmissionStatus(String submissionStatus) { this.submissionStatus = submissionStatus; }
    public double getSimWinnowing() { return simWinnowing; }
    public void setSimWinnowing(double simWinnowing) { this.simWinnowing = simWinnowing; }
    public double getSimTfidf() { return simTfidf; }
    public void setSimTfidf(double simTfidf) { this.simTfidf = simTfidf; }
    public String getMatchedSegmentsJson() { return matchedSegmentsJson; }
    public void setMatchedSegmentsJson(String matchedSegmentsJson) { this.matchedSegmentsJson = matchedSegmentsJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }
    public Timestamp getUploadTime() { return uploadTime; }
    public void setUploadTime(Timestamp uploadTime) { this.uploadTime = uploadTime; }
}
