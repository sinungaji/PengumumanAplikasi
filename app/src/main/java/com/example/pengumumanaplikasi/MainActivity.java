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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.example.pengumumanaplikasi.Model.MenuBawahPengumuman;
import com.example.pengumumanaplikasi.Model.MenuPengumumanPribadi;
import com.example.pengumumanaplikasi.helper.Config;
import com.example.pengumumanaplikasi.user.ProfileUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<MenuPengumumanPribadi> MenuPengumumanPribadi;
    ArrayList<MenuBawahPengumuman> MenuBawahPengumuman;

    RecyclerView recyclerViewPengumumanPribadi;
    RecyclerView recyclerView;

    private ImageView profileUser, searchView;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        searchView = findViewById(R.id.imgSearch);
        profileUser = findViewById(R.id.imgProfile);
        recyclerViewPengumumanPribadi = findViewById(R.id.recycleViewPribadi);
        recyclerView = findViewById(R.id.recycleView);

        MenuPengumumanPribadi = new ArrayList<>();
        LinearLayoutManager b = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.HORIZONTAL, false);
        recyclerViewPengumumanPribadi.setHasFixedSize(true);
        recyclerViewPengumumanPribadi.setLayoutManager(b);
        recyclerViewPengumumanPribadi.setNestedScrollingEnabled(true);

        MenuBawahPengumuman = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(x);
        recyclerView.setNestedScrollingEnabled(true);

        actionButton();
        getDataRecylePengumumanPribadi();
        getDataRecyleBawah();
    }

    private void actionButton() {
        profileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
                startActivity(intent);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getDataRecylePengumumanPribadi() {
        // Tag used to cancel the request
        pDialog.setMessage("Pengumuman Pribadi ...");
        MenuPengumumanPribadi.clear();
        showDialog();
        AndroidNetworking.post(Config.url+"pengumuman/list")
                .addBodyParameter("visibilitas", "1")
                .addBodyParameter("limit", "0")
                .addBodyParameter("offset", "5")
                .addBodyParameter("pencarian", "")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray responseB) {
                        hideDialog();
                        try {
                            for (int i = 0; i < responseB.length(); i++) {
                                JSONObject responsesB = responseB.getJSONObject(i);
                                Log.d("hehe", responsesB.optString("id_pengumuman"));
                                MenuPengumumanPribadi pp = new MenuPengumumanPribadi(
                                        responsesB.getString("id_pengumuman"),
                                        responsesB.getString("judul_pengumuman"),
                                        responsesB.getString("isi_pengumuman"),
                                        responsesB.getString("tgl_tayang_pengumuman"),
                                        responsesB.getString("tgl_berakhir_pengumuman"),
                                        responsesB.getString("Thumbnail"));

                                MenuPengumumanPribadi.add(pp);
                            }
                            MenuAdapterPengumumanPribadi menuAdapterPengumumanPribadi = new MenuAdapterPengumumanPribadi(getApplicationContext(), MenuPengumumanPribadi);
                            recyclerViewPengumumanPribadi.setAdapter(menuAdapterPengumumanPribadi);
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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

    public class MenuAdapterPengumumanPribadi extends RecyclerView.Adapter<MenuAdapterPengumumanPribadi.ProductViewHolder> {
        private Context mCtb;
        private List<MenuPengumumanPribadi> MenuPengumumanPribadi;

        MenuAdapterPengumumanPribadi(Context mCtb, List<MenuPengumumanPribadi> MenuPegumumanPribadi) {
            this.mCtb= mCtb;
            this.MenuPengumumanPribadi = MenuPegumumanPribadi;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroupB, int b) {
            LayoutInflater inflaterB = LayoutInflater.from(mCtb);
            @SuppressLint("InflateParams") View viewB = inflaterB.inflate(R.layout.activity_pengumuman_pribadi, null);
            return new ProductViewHolder(viewB);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holderB, int b) {
            final com.example.pengumumanaplikasi.Model.MenuPengumumanPribadi menuPengumumanPribadi = MenuPengumumanPribadi.get(b);
            Glide.with(MainActivity.this).load(menuPengumumanPribadi.getThumbnail()).into(holderB.imgTumbnailPengumumanPribadi);
            holderB.text_judul_pengumuman_pribadi.setText(menuPengumumanPribadi.getJudul_pengumuman());
            holderB.text_isi_pengumuman_pribadi.setText(menuPengumumanPribadi.getIsi_pengumuman());
            holderB.text_tgl_tayang_pengumuman_pribadi.setText(menuPengumumanPribadi.getTgl_tayang_pengumuman());
        }

        @Override
        public int getItemCount() {
            return MenuPengumumanPribadi.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_judul_pengumuman_pribadi, text_isi_pengumuman_pribadi, text_tgl_tayang_pengumuman_pribadi;
            CardView cvPengumumanPribadi;
            ImageView imgTumbnailPengumumanPribadi;

            ProductViewHolder(View itemViewB) {
                super(itemViewB);
                cvPengumumanPribadi = itemViewB.findViewById(R.id.cvPengumumanPribadi);
                imgTumbnailPengumumanPribadi = itemView.findViewById(R.id.tumbnailPribadi);
                text_judul_pengumuman_pribadi = itemViewB.findViewById(R.id.judulpengumumanPribadiTV);
                text_isi_pengumuman_pribadi = itemViewB.findViewById(R.id.isipengumumanPribadiTV);
                text_tgl_tayang_pengumuman_pribadi = itemViewB.findViewById(R.id.tgltayangpengumumanPribadiTV);
            }
        }
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
                .addBodyParameter("pencarian", "")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hideDialog();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                Log.d("hehe", responses.optString("id_pengumuman"));
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_main_bawah, null);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int i) {
            final com.example.pengumumanaplikasi.Model.MenuBawahPengumuman menu = MenuBawahPengumuman.get(i);
            holder.text_tanggal_tayang.setText(menu.getTgl_tayang_simple());
            Glide.with(MainActivity.this).load(menu.getThumbnail()).into(holder.imgTumbnail);
            holder.text_judul_pengumuman.setText(menu.getJudul_pengumuman());
            holder.text_isi_pengumuman.setText(menu.getIsi_pengumuman());
            holder.text_tgl_tayang_pengumuman.setText(menu.getTgl_tayang_pengumuman());
            holder.text_tgl_berakhir_pengumuman.setText(menu.getTgl_berakhir_pengumuman());
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, PengumumanActivity.class);
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
            TextView text_tanggal_tayang, text_judul_pengumuman, text_isi_pengumuman, text_img,
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