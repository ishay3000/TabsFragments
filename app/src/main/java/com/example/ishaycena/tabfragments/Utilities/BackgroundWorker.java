package com.example.ishaycena.tabfragments.Utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BackgroundWorker extends AsyncTask<Integer, AdapterFound, List<AdapterFound>> {
    private static final String TAG = "BackgroundWorker";

    public interface OnDataFetchedListener {
        /**
         * triggers when data is fetched from the database
         *
         * @param founds       ArrayList of Founds, with new data
         * @param changedOldestFoundId the new oldest post ID
         */
        void onDataFetched(final List<AdapterFound> founds, String changedOldestFoundId);
    }

    private OnDataFetchedListener listener;
    private DatabaseReference mDatabaseReference;
    private String mOldestPostId;

    public BackgroundWorker(OnDataFetchedListener listener, DatabaseReference reference, String oldestPostId) {
        this.listener = listener;
        mDatabaseReference = reference;
        mOldestPostId = oldestPostId;
    }

    @Override
    protected List<AdapterFound> doInBackground(Integer... integers) {
        Log.d(TAG, "doInBackground: starting database fetching");
        final List<AdapterFound> lst = new ArrayList<>();

        // fetching from database
        try {
            mDatabaseReference.orderByKey().startAt(mOldestPostId).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {
                        AdapterFound found = snapshot.getValue(AdapterFound.class);
                        lst.add(found);

                        // save the new oldest retrieved found id
                        mOldestPostId = snapshot.getKey();
                    }

                    Log.d(TAG, "onDataChange: finished retrieving data");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Thread.sleep(2000);

        } catch (InterruptedException e) {
            Log.e(TAG, "doInBackground: error:\n" + e.toString());
        }

        return lst;
    }

    @Override
    protected void onPostExecute(List<AdapterFound> founds) {
        listener.onDataFetched(founds, mOldestPostId);
//        ArrayList<AdapterFound> mLst = adapter.lstFounds;
//        int size = mLst.size();
//
//        mLst.remove(size - 1);
////                    Bitmap profile = BitmapFactory.decodeResource( getResources(),
////                    R.drawable.ishay_1);
////            Bitmap badge = BitmapFactory.decodeResource(getResources(),
////                    R.drawable.ic_crown);
////            Bitmap map = BitmapFactory.decodeResource(getResources(),
////                    R.drawable.ic_map);
////            Bitmap item = BitmapFactory.decodeResource(getResources(),
////                    R.drawable.ic_passport);
////
////            String name = "Ishay Cena", description = "AdapterFound this passport near the Town Hall...";
////            AdapterFound found2 = new AdapterFound(profile, badge, item, map, name, description);
//
////            adapter.addItem(found2);
//        adapter.notifyItemRangeChanged(size - 1, mLst.size() - size);
//        response.onResponse();
    }
}
