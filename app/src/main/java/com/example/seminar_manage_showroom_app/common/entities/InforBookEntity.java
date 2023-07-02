package com.example.seminar_manage_showroom_app.common.entities;

public class InforBookEntity {
    private String ShelfCode;
    private String booktitle;



    private String categories;
    private String author;
    private String InventoryName;
    private String isbn13;



    private String rfidCode;
    private int id;
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public String getShelfCode() {
        return ShelfCode;
    }

    public void setShelfCode(String shelfCode) {
        ShelfCode = shelfCode;
    }

    public String getBooktitle() {
        return booktitle;
    }

    public void setBooktitle(String booktitle) {
        this.booktitle = booktitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "InforBookEntity{" +
                "ShelfCode='" + ShelfCode + '\'' +
                ", Book_title='" + booktitle + '\'' +
                ", Categories='" + categories + '\'' +
                ", Author='" + author + '\'' +
                ", ISBN_13='" + isbn13 + '\'' +
                ", RfidCode='" + rfidCode + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
