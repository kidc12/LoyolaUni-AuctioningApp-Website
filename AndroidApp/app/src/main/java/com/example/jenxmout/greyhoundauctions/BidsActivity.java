package com.example.jenxmout.greyhoundauctions;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedList;
import java.util.Locale;

/**
 * This is the Bids Activity class that displays the
 * bids placed by the current user for the UI
 *
 * @author Jennifer Moutenot
 * @author Mollie Morrow
 * @version 1.0 12/15/19
 */
public class BidsActivity extends AppCompatActivity{

    /**
     * The search bar to search items by tag
     */
    SearchView searchBar;

    /**
     * The listview that displays all items that
     * the user has bid on
     */
    ListView listView;

    /**
     * An array of the titles of items
     * the user has bid on
     */
    String[] titles;

    /**
     * The text view that displays if a user
     * is logged in or not
     */
    private TextView textViewLoggedIn;

    /**
     * The countdown clock text view
     */
    private TextView textViewCountDown;
    
    /**
     * Sets up the User's Bid's screen view in order
     * for the user to view all bids that the user has currently placed
     *
     * @param savedInstanceState the reference to a Bundle object that is passed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCountDown = findViewById(R.id.countdown);
        textViewCountDown.setAlpha(0.0f);

        textViewLoggedIn = findViewById(R.id.loggedIn);
        textViewLoggedIn.setAlpha(0.0f);

        for(Item i: MainActivity.ais.items){
            i.updateAutoBid();
        }
        
        this.titles = new String[MainActivity.you.itemsBidOn.size()];

        for(Item i : MainActivity.you.itemsBidOn){
            for(int j = 0; j < MainActivity.you.itemsBidOn.size(); j++){
                titles[j] = i.title;   
            }
        }

        /* Instantiate the listview for the items to be in a list */
        listView = (ListView) findViewById(R.id.list_of_items);

        /* Instantiate the adapter for updating the listview of items */
        final BidsActivity.MyAdapter adptr = new BidsActivity.MyAdapter(this, titles, MainActivity.you.itemsBidOn);
        listView.setAdapter(adptr);

        // Set item click on the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * This method sets a click listener for when an item is clicked, which takes
             * the user to that item's page in order to bid/auto-bid
             *
             * @param parent the parent...
             * @param view the view of the current state
             * @param position the position of the item
             * @param id the id of the ....
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MainActivity.you.itemsBidOn.size() >= position+1) {
                    Intent itemIntent = new Intent(BidsActivity.this, ItemActivity.class);

                    //information from each item to populate the custom item intent
                    itemIntent.putExtra("itemPosition", position);
                    itemIntent.putExtra("from", "bids");

                    String tagsStr = "";
                    for (String tag : MainActivity.you.itemsBidOn.get(position).tags) {
                        tagsStr += "#" + tag + " ";
                    }

                    itemIntent.putExtra("itemTags", tagsStr);
                    startActivity(itemIntent);
                }
            }
        });

        // Search Bar
        searchBar = (SearchView) findViewById(R.id.search_bar);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            /**
             * This method takes the string that
             * the user types in at the search bar
             * and submits the query to search
             *
             * @param s the string the user searches at the
             *          search bar
             * @return false if the method is called
             */
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            /**
             * This method takes the string that the user
             * types in at the search bar and filters the
             * list according to the the changes of the string
             * being searched
             *
             * @param s the string the user searches at the
             *          search bar
             * @return true if the method is called
             */
            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)){
                    adptr.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adptr.filter(s);
                }
                return true;
            }
        });
        
        // Event Button
        ImageButton eventButton = (ImageButton) findViewById(R.id.eventButton);

        /**
         * This methods takes the user to the EventActivity screen when button is clicked
         */
        eventButton.setOnClickListener(new View.OnClickListener() {

            /**
             * This method sets a click listener for the button in the UI
             * When clicked, the user is taken to the next view
             * EventActivity
             *
             * @param v the view of the current state
             */
            @Override
            public void onClick(View v) {
                Intent eventIntent = new Intent(BidsActivity.this, EventActivity.class);
                startActivity(eventIntent);
            }
        });


        // Account Button
        ImageButton accountButton = (ImageButton) findViewById(R.id.loginButton);

        /**
         * This methods takes the user to the AccountActivity screen when button is clicked
         */
        accountButton.setOnClickListener(new View.OnClickListener() {

            /**
             * This method sets a click listener for the button in the UI
             * When clicked, the user is taken to the next view
             * AccountActivity
             *
             * @param v the view of the current state
             */
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(BidsActivity.this, AccountActivity.class);
                startActivity(accountIntent);
            }
        });

        //Home Button
        ImageButton homeButton = (ImageButton) findViewById(R.id.homeButton);

        /**
         * This methods takes the user to the MainActivity screen when button is clicked
         */
        homeButton.setOnClickListener(new View.OnClickListener() {

            /**
             * This method sets a click listener for the button in the UI
             * When clicked, the user is taken to the next view
             * MainActivity
             *
             * @param v the view of the current state
             */
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(BidsActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        // Display User Highest Bids Button
        ImageButton userHighestBidButton = (ImageButton) findViewById(R.id.itemsHighestButton);

        /**
         * This methods takes the user to the HighestActivity screen when button is clicked
         */
        userHighestBidButton.setOnClickListener(new View.OnClickListener() {

            /**
             *This method sets a click listener for the new game button in the UI
             * When clicked, user can see the bids they have placed that are
             * currently winning or are the highest bidder
             *
             * @param v the view of the current state
             */
            @Override
            public void onClick(View v) {
                if (MainActivity.you != null) {
                    if(MainActivity.you.signedIn) {
                        Intent userHighestBidIntent = new Intent(BidsActivity.this, HighestActivity.class);
                        startActivity(userHighestBidIntent);
                    }
                    else {
                        Toast.makeText(BidsActivity.this, "Log in or sign up to view!",
                                Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(BidsActivity.this, AccountActivity.class);
                        startActivity(loginIntent);
                    }
                }
                else {
                    Toast.makeText(BidsActivity.this, "Log in or sign up to view!",
                            Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(BidsActivity.this, AccountActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }

    /**
     * This is the MyAdapter class that updates the ListView
     * when scrolling
     */
    class MyAdapter extends ArrayAdapter<String> {

        /**
         * The context of the current activity
         */
        protected Context context;

        /**
         * The linked list of items
         */
        protected LinkedList<Item> items;

        /**
         * The updated linked list of items post-search
         */
        protected LinkedList<Item> itemsList;

        /**
         * MyAdapter Constructor
         *
         * @param c the context of the current activity
         * @param titles the titles of all the items
         * @param items the linked list of items
         */
        public MyAdapter(Context c, String[] titles, LinkedList<Item> items) {
            super(c, R.layout.row, R.id.item_title, titles);
            this.context = c;
            this.items = items;
            this.itemsList = new LinkedList<>();
            this.itemsList.addAll(items);
        }

        /**
         * This method returns the updated list view
         * post-search based on the query at the search bar
         *
         * @param position the positon of the item in the list view
         * @param convertView the view of the list
         * @param parent the parent of the ViewGroup
         * @return the View of the updated list view
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.item_image);
            TextView myTitle = row.findViewById(R.id.item_title);
            TextView myDescription = row.findViewById(R.id.item_description);
            TextView myCHB = row.findViewById(R.id.item_CHB);
            TextView myCHBr = row.findViewById(R.id.item_CHBr);

            if(MainActivity.you != null) {
                if (MainActivity.you.signedIn) {

                    if (MainActivity.you.itemsCurrentHighestBidderOn.contains(MainActivity.you.itemsBidOn.get(position)))
                        row.setBackgroundColor(getResources().getColor(R.color.winningBidGreen));
                    else
                        row.setBackgroundColor(getResources().getColor(R.color.losingBidRed));
                }
            }

            if(position < items.size()) {
                images.setImageBitmap(items.get(position).resID);
                myTitle.setText(items.get(position).title);
                myDescription.setText(items.get(position).description);
                myCHB.setText("$" + items.get(position).currentHighestBid + "0");
                myCHBr.setText(items.get(position).currentHighestBidder);
            }
            return row;
        }

        /**
         * This method filters out the items according to
         * the query at the search bar
         *
         * @param charText
         */
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            items.clear();
            if (charText.length() == 0) {
                items.addAll(itemsList);
            } else {
                for (Item item : itemsList) {
                    for(int i = 0; i < item.tags.length; i++) {
                        //check if the tags contain anything being searched
                        if(item.tags[i].toLowerCase(Locale.getDefault()).contains(charText)) {
                            //dont double add an item
                            if(!items.contains(item))
                                items.add(item);
                        }
                    }
                }
            }
            //refresh ais.items for onClick method
            MainActivity.you.itemsBidOn = items;
            notifyDataSetChanged();
        }
    }
}

