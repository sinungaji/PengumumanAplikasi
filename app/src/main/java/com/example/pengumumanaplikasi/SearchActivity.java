package com.example.pengumumanaplikasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.load.model.ModelLoader;
import com.example.pengumumanaplikasi.Model.MenuBawahPengumuman;
import com.example.pengumumanaplikasi.helper.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    ArrayList<MenuBawahPengumuman> MenuBawahPengumuman;

    RecyclerView recyclerView;

    private ImageView imgBack;
    private ProgressDialog pDialog;
    private EditText et_search;
    String visibilitas, limit, offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        imgBack = findViewById(R.id.imgBack);
        et_search = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.recycleView);

        et_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_search, InputMethodManager.SHOW_IMPLICIT);

        MenuBawahPengumuman = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(x);
        recyclerView.setNestedScrollingEnabled(true);

        actionButton();
    }

    private void actionButton() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    MenuBawahPengumuman.clear();
                    getDataRecyleBawah();
                    return true;
                }
                return false;
            }
        });

    }

    private void getDataRecyleBawah() {
        // Tag used to cancel the request
        pDialog.setMessage("Pengumuman Bawah ...");
        MenuBawahPengumuman.clear();
        showDialog();
        AndroidNetworking.post(Config.url+"pengumuman/list")
                .addBodyParameter("visibilitas", "0")
                .addBodyParameter("limit", "0")
                .addBodyParameter("offset", "5")
                .addBodyParameter("pencarian", et_search.getText().toString().trim())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hideDialog();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                MenuBawahPengumuman bk = new MenuBawahPengumuman(
                                        responses.getString("id_pengumuman"),
                                        responses.getString("judul_pengumuman"),
                                        responses.getString("isi_pengumuman"),
                                        responses.getString("tgl_tayang_simple"),
                                        responses.getString("tgl_tayang_pengumuman"),
                                        responses.getString("tgl_berakhir_pengumuman"),
                                        responses.getString("Thumbnail"));

                                Log.d("hehe", responses.optString("id_pengumuman"));
                                MenuBawahPengumuman.add(bk);
                            }
                            MenuAdapter menu = new MenuAdapter(getApplicationContext(), MenuBawahPengumuman);
                            recyclerView.setAdapter(menu);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        hideDialog();
                        Log.d("data1", "onError errorCode : " + error.getErrorCode());
                        Log.d("data1", "onError errorBody : " + error.getErrorBody());
                        Log.d("data1", "onError errorDetail : " + error.getErrorDetail());

                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SearchActivity.this);
                                builder1.setMessage(body.optString("pesan"));
                                builder1.setCancelable(false);

                                builder1.setPositiveButton(
                                        "Oke",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            } catch (JSONException ignored) {

                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(SearchActivity.this);
                            builder1.setMessage("Jaringan sedang sibuk. Klik 'Ok', untuk menutup pesan ini!");
                            builder1.setCancelable(false);

                            builder1.setPositiveButton(
                                    "Oke",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                    }
                });

    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ProductViewHolder> {
        private Context mCtx;
        private List<MenuBawahPengumuman> MenuBawahPengumuman;

        MenuAdapter(Context mCtx, List<MenuBawahPengumuman> MenuBawahPengumuman) {
            this.mCtx = mCtx;
            this.MenuBawahPengumuman = MenuBawahPengumuman;
        }

        @NonNull
        @Override
        public MenuAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_main_bawah, null);
            return new MenuAdapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuAdapter.ProductViewHolder holder, int i) {
            final com.example.pengumumanaplikasi.Model.MenuBawahPengumuman menu = MenuBawahPengumuman.get(i);
            holder.text_tanggal_tayang.setText(menu.getTgl_tayang_simple());
            holder.text_judul_pengumuman.setText(menu.getJudul_pengumuman());
            holder.text_isi_pengumuman.setText(menu.getIsi_pengumuman());
            holder.text_tgl_tayang_pengumuman.setText(menu.getTgl_tayang_pengumuman());
            holder.text_tgl_berakhir_pengumuman.setText(menu.getTgl_berakhir_pengumuman());
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SearchActivity.this, PengumumanActivity.class);
                    i.putExtra("id_pengumuman", menu.getId_pengumuman());
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return MenuBawahPengumuman.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_tanggal_tayang, text_tanggal_berakhir, text_judul_pengumuman, text_isi_pengumuman,
                    text_tgl_tayang_pengumuman, text_tgl_berakhir_pengumuman;
            ImageView imgTumbnail;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                cv = itemView.findViewById(R.id.cv);
                imgTumbnail = itemView.findViewById(R.id.tumbnail);
                text_tanggal_tayang = itemView.findViewById(R.id.tanggalTayangTV);
                text_judul_pengumuman = itemView.findViewById(R.id.judulpengumumanTV);
                text_isi_pengumuman = itemView.findViewById(R.id.isipengumumanTV);
                text_tgl_tayang_pengumuman = itemView.findViewById(R.id.tgltayangpengumumanTV);
                text_tgl_berakhir_pengumuman = itemView.findViewById(R.id.tglberakhirpengumumanTV);
            }
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}