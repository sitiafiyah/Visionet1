package id.sch.smktelkom_mlg.visionet.kpu;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> contactList;
    ArrayList<HashMap<String, String>> awalList;
    EditText edsearch;
    //TextView Comm, Wilayah, Record, Cmd;
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv, aw;
    private String url = "https://data.kpu.go.id/open/v1/api.php?cmd=wilayah_browse&wilayah_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Comm = (TextView) findViewById(R.id.comm);
        Wilayah = (TextView) findViewById(R.id.wilayah);
        Record = (TextView) findViewById(R.id.record);
        Cmd = (TextView) findViewById(R.id.cmd);*/

        edsearch = (EditText) findViewById(R.id.edittext);

        contactList = new ArrayList<>();
        awalList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        aw = (ListView) findViewById(R.id.awal);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proses();
            }
        });

        /*Comm.setText(comm);
                    Wilayah.setText(wilayah);
                    Record.setText(records);*/

    }

    private void proses() {
        if (isValid()) {
            new GetContacts().execute();
        }
    }

    private boolean isValid() {

        boolean valid = true;
        String ed = edsearch.getText().toString();

        if (ed.isEmpty()) {
            edsearch.setError("Wilayah id belum di isi");
            valid = false;
        } else if (ed.length() > 6) {
            edsearch.setError("Jumlah digit yang dimasukkan adalah 6");
            valid = false;
        } else {
            edsearch.setError(null);
        }
        return valid;
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        String abc = edsearch.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url.trim() + abc);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Phone node is JSON Object

                    String com = jsonObj.getString("comm");
                    String wil = jsonObj.getString("wilayah");
                    String rec = jsonObj.getString("records");
                    String cmd = jsonObj.getString("cmd");

                    wil = wil.replace("&gt;", ">");
                    HashMap<String, String> awl = new HashMap<>();
                    awl.put("comm", "Comm : " + com);
                    awl.put("wilayah", "Wilayah : " + wil);
                    awl.put("records", "Records : " + rec);
                    awl.put("cmd", "Cmd : " + cmd);

                    awalList.clear();
                    awalList.add(awl);
                    /*Comm.setText(com);
                    Wilayah.setText(wil);
                    Record.setText(rec);*/
                    //Comm.setText("Comm : " + comm);

                    Log.e(TAG, "uji coba: " + com);
                    Log.e(TAG, "uji coba: " + wil);
                    Log.e(TAG, "uji coba: " + rec);
                    Log.e(TAG, "uji coba1: " + cmd);
                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("data");

                    contactList.clear();
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String wilayah_id = c.getString("wilayah_id");
                        String nama = c.getString("nama");
                        String parent = c.getString("parent");
                        String daerah = c.getString("daerah");
                        String tingkat = c.getString("tingkat");
                        String singkatan = c.getString("singkatan");

                        /*// Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");*/

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("wilayah_id", "Wilayah Id : " + wilayah_id);
                        contact.put("nama", "Nama : " + nama);
                        contact.put("parent", "Parent : " + parent);
                        contact.put("daerah", "Daerah : " + daerah);
                        contact.put("tingkat", "Tingkat : " + tingkat);
                        contact.put("singkatan", "Singkatan : " + singkatan);


                        // adding contact to contact list
                        contactList.add(contact);
                    }

                    //Cmd.setText(cmd);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Wilayah_id tidak ditemukan" + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter1;
            adapter1 = null;
            adapter1 = new SimpleAdapter(
                    MainActivity.this, awalList, R.layout.list_awal,
                    new String[]{"comm", "wilayah", "records", "cmd"},
                    new int[]{R.id.comm, R.id.wilayah, R.id.record, R.id.cmd});

            aw.setAdapter(adapter1);


            ListAdapter adapter;
            adapter = null;
            adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"wilayah_id", "nama",
                    "parent", "daerah", "tingkat", "singkatan"},
                    new int[]{R.id.wilayah_id, R.id.nama,
                            R.id.parent, R.id.daerah, R.id.tingkat, R.id.singkatan});

            lv.setAdapter(adapter);

        }

    }

}
