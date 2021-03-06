package com.example.jenxmout.greyhoundauctions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the Main Activity class that
 * displays the items up for auction
 * and allows users to search by tags,
 * select items, and access other menu options
 *
 * @author Jennifer Moutenot
 * @author Mollie Morrow
 * @version 1.0 12/15/19
 */

public class MainActivity extends AppCompatActivity {

    /**
     * The search bar to search item by tag
     */
    SearchView searchBar;

    /**
     * The list that holds all the items
     */
    ListView listView;

    /**
     * An array of the titles of items
     */
    String[] titles;

    /**
     * The list of auctioned items
     */
    protected static AuctionItems ais;

    /**
     * The start time of the fundraiser
     */
    protected static long START_TIME_IN_MILLIS;

    /**
     * The countdown clock text view
     */
    private TextView textViewCountDown;

    /**
     * The text view that displays if a user
     * is logged in or not
     */
    private TextView textViewLoggedIn;


    /**
     * Auction over flag
     */
    protected static boolean auctionOver;


    /**
     * The time left for the fundraiser
     */
    private long timeLeftInMillis;

    /**
     * The end time for the fundraiser
     */
    protected static long endTime;

    /**
     * The user
     */
    protected static User you;

    /**
     * The event
     */
    protected static  FundraiserInfo fInfo;

    /**
     * To get the user's information
     *
     * @return you the User object
     */
    protected User getUser() {
        return you;
    }

    /**
     * To get the list of auctioned items
     *
     * @return ais the AuctionItems object
     */
    protected AuctionItems getAis() {
        return ais;
    }

    /**
     * To get the fundraiser's information
     *
     * @return fInfo the FundraiserInfo object
     */
    protected FundraiserInfo getFInfo() {return fInfo;}

    /**
     * Sets up the main screen view
     *
     * @param savedInstanceState the reference to a Bundle object that is passed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //run backgroundworker
        if(fInfo == null) {
            Log.w("Background worker", "running!");
            BackgroundWorker bw = new BackgroundWorker(MainActivity.this);
            bw.execute("fetch event data", "");
        }

        if(ais == null){
            Log.w("Background worker", "running!");
            BackgroundWorker bw = new BackgroundWorker(MainActivity.this);
            bw.execute("fetch item data", "");
        }

        if(ais != null) {
            for (Item i : ais.items) {
                i.updateAutoBid();
                Log.w("updating autobid for", i.title);
                String name = i.currentHighestBidder;
                String bid = String.valueOf(i.currentHighestBid);
                BackgroundWorker bw = new BackgroundWorker(MainActivity.this);
                bw.execute("update item data", bid, name, i.title);
            }

            // grab existing static user and items
            you = getUser();
            ais = getAis();

            textViewLoggedIn = findViewById(R.id.loggedIn);
            if (you != null) {
                if (you.signedIn) {
                    textViewLoggedIn.setText("Logged In");
                    Log.w("signed in main", "true");
                }
                else {
                    textViewLoggedIn.setText("Logged Out");
                    Log.w("signed in main", "false");
                }
            } else
                textViewLoggedIn.setText("Logged Out");
                Log.w("signed in main", "null");

            if (you != null) {
                if (you.itemsBidOn.size() > 0) {
                    you.itemsCurrentHighestBidderOn = new LinkedList<>();
                    for (Item i : you.itemsBidOn) {
                        if (i.currentHighestBidder.equals(you.firstName + " " + you.lastName))
                            you.itemsCurrentHighestBidderOn.add(i);
                    }
                }
            }

            //instantiate titles
            titles = new String[ais.items.size()];
            //create list view
            listView = (ListView) findViewById(R.id.list_of_items);
            //adapter for scroll view
            final MyAdapter adptr = new MyAdapter(this, titles, ais.items);
            listView.setAdapter(adptr);

            // now set item click on list view
            /**
             * This methods takes the user to a particular item's screen when button is clicked
             */
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                /**
                 * This method sets a click listener for when an item is clicked, which takes
                 * the user to that item's page in order to bid/auto-bid
                 *
                 * @param parent   the parent...
                 * @param view     the view of the current state
                 * @param position the position of the item
                 * @param id       the id of the ....
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Log.w("items size", ais.items.size() + " items");
                    //Log.w("position", "index " + position);
                    if (ais.items.size() >= position + 1) {
                        Intent itemIntent = new Intent(MainActivity.this, ItemActivity.class);
                        itemIntent.putExtra("itemPosition", position);
                        itemIntent.putExtra("from", "main");
                        String tagsStr = "";
                        for (String tag : ais.items.get(position).tags) {
                            tagsStr += "#" + tag + " ";
                        }

                        itemIntent.putExtra("itemTags", tagsStr);
                        startActivity(itemIntent);
                    }
                }
            });

            // Search Bar
            searchBar = (SearchView) findViewById(R.id.search_bar);
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
                    if (TextUtils.isEmpty(s)) {
                        adptr.filter("");
                        listView.clearTextFilter();
                    } else {
                        adptr.filter(s);
                    }
                    return true;
                }
            });

            // Creating Countdown Clock while start time and end time exist
            if (fInfo != null) {
                START_TIME_IN_MILLIS = fInfo.getStartTimeMillis();
                endTime = fInfo.getEndTimeMillis();


                long currentTimeMillis = System.currentTimeMillis();

                String currentTime = DateFormat.getInstance().format(currentTimeMillis);
                String stringStartTime = DateFormat.getInstance().format(START_TIME_IN_MILLIS);
                String stringEndTime = DateFormat.getInstance().format(endTime);

                timeLeftInMillis = (endTime - currentTimeMillis);

                textViewCountDown = findViewById(R.id.countdown);

                MyCount counter = new MyCount(timeLeftInMillis, 1000);
                counter.start();

            }

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
                    Intent eventIntent = new Intent(MainActivity.this, EventActivity.class);
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
                    Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(accountIntent);
                }
            });

            // Display User Bids Button
            ImageButton userBidButton = (ImageButton) findViewById(R.id.whatIBidOnButton);

            /**
             * This methods takes the user to the BidsActivity screen when button is clicked
             */
            userBidButton.setOnClickListener(new View.OnClickListener() {

                /**
                 * This method sets a click listener for the new game button in the UI
                 * When clicked, user can see the bids they have placed
                 *
                 * @param v the view of the current state
                 */
                @Override
                public void onClick(View v) {
                    if (you != null) {
                        if (you.signedIn) {
                            Intent userBidIntent = new Intent(MainActivity.this, BidsActivity.class);
                            startActivity(userBidIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Log in or sign up to view!",
                                    Toast.LENGTH_LONG).show();
                            Intent loginIntent = new Intent(MainActivity.this, AccountActivity.class);
                            startActivity(loginIntent);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Log in or sign up to view!",
                                Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(MainActivity.this, AccountActivity.class);
                        startActivity(loginIntent);
                    }
                }
            });

            // Display User Highest Bids Button
            ImageButton userHighestBidButton = (ImageButton) findViewById(R.id.itemsHighestButton);

            /**
             * This methods takes the user to the HighestActivity screen when button is clicked
             */
            userHighestBidButton.setOnClickListener(new View.OnClickListener() {

                /**
                 * This method sets a click listener for the new game button in the UI
                 * When clicked, user can see the bids they have placed that are
                 * currently winning or are the highest bidder
                 *
                 * @param v the view of the current state
                 */
                @Override
                public void onClick(View v) {
                    if (you != null) {
                        if (you.signedIn) {
                            Intent userHighestBidIntent = new Intent(MainActivity.this, HighestActivity.class);
                            startActivity(userHighestBidIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Log in or sign up to view!",
                                    Toast.LENGTH_LONG).show();
                            Intent loginIntent = new Intent(MainActivity.this, AccountActivity.class);
                            startActivity(loginIntent);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Log in or sign up to view!",
                                Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(MainActivity.this, AccountActivity.class);
                        startActivity(loginIntent);
                    }
                }
            });
        }
    }

    /**
     * This MyAdapter class that updates the ListView
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

            if(you != null) {
                if (you.signedIn) {
                    Log.w("adapter", "signed in");
                    for (Item item : you.itemsBidOn) {
                        Log.w("adapter", "checking" + item.title);
                        if (item.title.equals(ais.items.get(position).title)) {
                            if (you.itemsCurrentHighestBidderOn.contains(item))
                                row.setBackgroundColor(getResources().getColor(R.color.winningBidGreen));
                            else
                                row.setBackgroundColor(getResources().getColor(R.color.losingBidRed));
                        }
                    }
                }
            }

            if (position < items.size()) {
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
                    for (int i = 0; i < item.tags.length; i++) {
                        //check if the tags contain anything being searched
                        if (item.tags[i].toLowerCase(Locale.getDefault()).contains(charText)) {
                            //dont double add an item
                            if (!items.contains(item))
                                items.add(item);
                        }
                    }
                }
            }
            //refresh ais.items for onClick method
            ais.items = items;
            notifyDataSetChanged();
        }
    }

    // countdowntimer is an abstract class, so extend it and fill in methods

    /**
     * The MyCount class to keep track of the time of
     * the fundraiser event
     */
    public class MyCount extends CountDownTimer {

        /**
         * MyCount Constructor
         *
         * @param millisInFuture the milliseconds left
         * @param countDownInterval the interval in between
         *                          the countdown ticks
         */
        MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * To set the text to "done!" when the fundraiser is finished
         */
        @Override
        public void onFinish() {
            textViewCountDown.setText("Auction Over!");
            auctionOver = true;

        }

        /**
         * To update the countdown timer text view
         * on every second tick
         * @param millisUntilFinished the milliseconds left
         */
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = ((TimeUnit.MILLISECONDS.toDays(millis) * 24)
                    + (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis))) + ":")
                    + (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) + ":"
                    + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));

            textViewCountDown.setText(/*context.getString(R.string.ends_in) + " " +*/ hms);
        }
    }
}
















