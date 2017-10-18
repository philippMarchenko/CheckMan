package com.devphill.checkman.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Declarations {


    @SerializedName("page")
    @Expose
    private Page page;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


    public class Item {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("lastname")
        @Expose
        private String lastname;
        @SerializedName("placeOfWork")
        @Expose
        private String placeOfWork;
        @SerializedName("position")
        @Expose
        private String position;
        @SerializedName("linkPDF")
        @Expose
        private String linkPDF;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getPlaceOfWork() {
            return placeOfWork;
        }

        public void setPlaceOfWork(String placeOfWork) {
            this.placeOfWork = placeOfWork;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getLinkPDF() {
            return linkPDF;
        }

        public void setLinkPDF(String linkPDF) {
            this.linkPDF = linkPDF;
        }

    }

    public class Page {

        @SerializedName("currentPage")
        @Expose
        private Integer currentPage;
        @SerializedName("batchSize")
        @Expose
        private Integer batchSize;
        @SerializedName("totalItems")
        @Expose
        private String totalItems;

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public Integer getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(Integer batchSize) {
            this.batchSize = batchSize;
        }

        public String getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(String totalItems) {
            this.totalItems = totalItems;
        }
    }
}