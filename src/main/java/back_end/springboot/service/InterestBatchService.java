package back_end.springboot.service;

public interface InterestBatchService {
    public void collectHashTagInterest();
    public void collectAiObjectInterest();
    public void collectPostViewInterest();
    public void collectActiveUsers();
    public void collectActiveUsersInterest();
    public void collectActiveUsersReadPost();
    public void runPostRankingBatch();
}
