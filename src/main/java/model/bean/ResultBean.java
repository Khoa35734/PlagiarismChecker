package model.bean;

import java.time.Instant;

/**
 * Plain Bean representing a Result (DTO/Model bean).
 */
public class ResultBean {
    private int id;
    private int submissionId;
    private double similarityWinnowing;
    private double similarityTfidf;
    private String matchedSegmentsJson;
    private String status;
    private String sourceDocument;
    private Instant processedAt;

    public ResultBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public double getSimilarityWinnowing() {
        return similarityWinnowing;
    }

    public void setSimilarityWinnowing(double similarityWinnowing) {
        this.similarityWinnowing = similarityWinnowing;
    }

    public double getSimilarityTfidf() {
        return similarityTfidf;
    }

    public void setSimilarityTfidf(double similarityTfidf) {
        this.similarityTfidf = similarityTfidf;
    }

    public String getMatchedSegmentsJson() {
        return matchedSegmentsJson;
    }

    public void setMatchedSegmentsJson(String matchedSegmentsJson) {
        this.matchedSegmentsJson = matchedSegmentsJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourceDocument() {
        return sourceDocument;
    }

    public void setSourceDocument(String sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
