package com.emotech.e_bookshop.adapter;

import android.graphics.Bitmap;

/**
 * Created by muham_000 on 19/09/2017.
 */

public class ImageItem {

    private String image;
    private String hargaBuku;
    private String namaBuku;
    private String kodeBuku;

    public ImageItem(String image, String namaBuku, String hargaBuku, String kodeBuku) {
        super();
        this.image = image;
        this.namaBuku = namaBuku;
        this.hargaBuku = hargaBuku;
        this.kodeBuku = kodeBuku;
    }

    public String getImage() {
            return image;
        }

    public void setImage(String image) {
            this.image = image;
        }

    public String getNamaBuku() {
            return namaBuku;
        }

    public void setNamaBuku(String namaBuku) {
            this.namaBuku = namaBuku;
        }

    public String getHargaBuku() {
            return hargaBuku;
        }

    public void setHargaBuku(String hargaBuku) {
            this.hargaBuku = hargaBuku;
        }

    public String getKodeBuku() {
            return kodeBuku;
        }
}
