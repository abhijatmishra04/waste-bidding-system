package io.com.didingapp.history;

public class historyModel {


    @Override
    public String toString() {
        return "historyModel{" +
                "title='" + title + '\'' +
                ", min_bids='" + min_bids + '\'' +
                ", start_bid_time='" + start_bid_time + '\'' +
                ", end_bid_time='" + end_bid_time + '\'' +
                ", auc_id='" + auc_id + '\'' +
                ", status='" + status + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public historyModel(String mybids, String title, String min_bids, String start_bid_time, String end_bid_time, String status, String description) {
        this.mybids = mybids;
        this.title = title;
        this.min_bids = min_bids;
        this.start_bid_time = start_bid_time;
        this.end_bid_time = end_bid_time;
        this.status = status;
        this.description = description;
    }

    public historyModel(String title, String min_bids, String start_bid_time, String end_bid_time, String status, String category, String description, String auc_id) {

        this.title=title;
        this.min_bids=min_bids;
        this.start_bid_time=start_bid_time;
        this.end_bid_time=end_bid_time;
        this.status=status;
        this.category=category;
        this.description=description;
        this.auc_id=auc_id;

    }


    private  String mybids;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMin_bids() {
        return min_bids;
    }

    public void setMin_bids(String min_bids) {
        this.min_bids = min_bids;
    }

    public String getStart_bid_time() {
        return start_bid_time;
    }

    public void setStart_bid_time(String start_bid_time) {
        this.start_bid_time = start_bid_time;
    }

    public String getEnd_bid_time() {
        return end_bid_time;
    }

    public void setEnd_bid_time(String end_bid_time) {
        this.end_bid_time = end_bid_time;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String min_bids;
    private String start_bid_time;
    private String end_bid_time;
    private String auc_id;
    private String status;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String category;
    private String description;


    public String getAuc_id() {
        return auc_id;
    }

    public void setAuc_id(String auc_id) {
        this.auc_id = auc_id;
    }

    public String getMybids() {
        return mybids;
    }

    public void setMybids(String mybids) {
        this.mybids = mybids;
    }
}
