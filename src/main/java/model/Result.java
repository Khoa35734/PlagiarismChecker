package model;

public class Result {
    private int id;
    private int submissionId;
    private double similarityWinnowing;
    private double similarityTfidf;
    private String matchedSegmentsJson;
    private String status;
    private String sourceDocument;

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
}
