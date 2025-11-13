package model;

public class Result {
    private int id;
    private int submissionId;
    private int comparedWith;
    private float similarity;

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

    public int getComparedWith() {
        return comparedWith;
    }

    public void setComparedWith(int comparedWith) {
        this.comparedWith = comparedWith;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }
}
