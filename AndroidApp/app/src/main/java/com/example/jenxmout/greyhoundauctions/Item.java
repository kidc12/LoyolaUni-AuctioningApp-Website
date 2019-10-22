package com.example.jenxmout.greyhoundauctions;

import android.media.Image;

import java.util.LinkedList;

/**
 * This is a Item class
 *
 * @author Jennifer Moutenot
 * @author Mollie Morrow
 * @author Javon Kitson
 * @author Ian Leppo
 * @version 1.0 10/15/19
 */
public class Item {

    protected String title;
    protected String description;
    protected int resID;
    protected double minInc;

    protected LinkedList<String> tags;

    protected double currentHighestBid;
    protected String currentHighestBidder;

    public Item(String title, String desc, int resID, double minInc, LinkedList<String> tags,
                double currentHighestBid){
        this.title = title;
        this.description = desc;
        this.resID = resID;
        this.minInc = minInc;
        this.tags = tags;
        this.currentHighestBid = currentHighestBid;
        this.currentHighestBidder = "";

    }
}
