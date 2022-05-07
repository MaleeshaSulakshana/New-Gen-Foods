package com.example.newgenfoods.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.newgenfoods.Adapters.CommentAdapter;
import com.example.newgenfoods.Api.CategoryApi;
import com.example.newgenfoods.Api.CommentApi;
import com.example.newgenfoods.Api.FoodApi;
import com.example.newgenfoods.Models.CommentItems;
import com.example.newgenfoods.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class AdminViewFoodItemDetailsActivity extends AppCompatActivity {

    private ListView commentsList;
    private ChipGroup categoryChipGroup;
    private Button btnUpdateItem, btnRemoveItem, btnAddItem;
    private EditText price, title, ingredients;
    private ImageView itemImage;
    private LinearLayout commentSection;

    private BottomSheetDialog removeConfirmDialog;

    private ArrayList<CommentItems> commentArrayList = new ArrayList<>();

    private String itemId = "";
    private String itemMode = "";
    private String thumbnailUrl = "";
    private String selectedCategoryName = "";
    private Uri imgUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_food_item_details);

        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemID");
        itemMode = intent.getStringExtra("itemMode");

        removeConfirmDialog = new BottomSheetDialog(AdminViewFoodItemDetailsActivity.this, R.style.BottomSheetTheme);
        removeConfirmDialog.setContentView(R.layout.dialog_box_remove_confirm);

        commentSection = (LinearLayout) this.findViewById(R.id.commentSection);

        commentsList = (ListView) this.findViewById(R.id.commentsList);

        price = (EditText) this.findViewById(R.id.price);
        title = (EditText) this.findViewById(R.id.title);
        ingredients = (EditText) this.findViewById(R.id.ingredients);

        btnAddItem = (Button) this.findViewById(R.id.btnAddItem);
        btnUpdateItem = (Button) this.findViewById(R.id.btnUpdateItem);
        btnRemoveItem = (Button) this.findViewById(R.id.btnRemoveItem);

        itemImage = (ImageView) this.findViewById(R.id.itemImage);

        categoryChipGroup = (ChipGroup) findViewById(R.id.categoryChipGroup);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (itemMode.equals("New")) {
            btnUpdateItem.setVisibility(View.GONE);
            btnRemoveItem.setVisibility(View.GONE);
            commentSection.setVisibility(View.GONE);
            btnAddItem.setVisibility(View.VISIBLE);
            int min = 1000000;
            int max = 9999999;
            String random_number = String.valueOf(new Random().nextInt(max) + min);

            itemId = random_number;
            showCategories();
        } else {
            btnAddItem.setVisibility(View.GONE);
            btnUpdateItem.setVisibility(View.VISIBLE);
            btnRemoveItem.setVisibility(View.VISIBLE);
            commentSection.setVisibility(View.VISIBLE);

            showFoodDetails();
            showCategories();
            showComments();
        }


//        For chip onclick
        categoryChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    String categoryName = chip.getText().toString();
                    selectedCategoryName = categoryName;
                }
            }
        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopUp();
                choosePicture();
            }
        });


//        Buttons functions
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String titleValue = title.getText().toString();
                String ingredientsValue = ingredients.getText().toString();
                String priceValue = price.getText().toString();

                if (titleValue.equals("") || ingredientsValue.equals("")
                        || priceValue.equals("") || selectedCategoryName.equals("")
                        || selectedCategoryName.equals("") || thumbnailUrl.equals("")) {
                    Toast.makeText(AdminViewFoodItemDetailsActivity.this, "Fields empty",Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        String URL = FoodApi.FOOD_API;

                        RequestQueue requestQueue = Volley.newRequestQueue(AdminViewFoodItemDetailsActivity.this);
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("title", titleValue);
                        jsonBody.put("price", priceValue);
                        jsonBody.put("category", selectedCategoryName);
                        jsonBody.put("ingredients", ingredientsValue);
                        jsonBody.put("image", thumbnailUrl);
                        jsonBody.put("status", "Available");

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {

                                        Intent intent = new Intent(AdminViewFoodItemDetailsActivity.this, AdminDashboardActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    Toast.makeText(AdminViewFoodItemDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(AdminViewFoodItemDetailsActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    return null;
                                }
                            }

                        };

                        requestQueue.add(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String titleValue = title.getText().toString();
                String ingredientsValue = ingredients.getText().toString();
                String priceValue = price.getText().toString();

                if (titleValue.equals("") || ingredientsValue.equals("")
                        || priceValue.equals("") || selectedCategoryName.equals("")
                        || selectedCategoryName.equals("") || thumbnailUrl.equals("")) {
                    Toast.makeText(AdminViewFoodItemDetailsActivity.this, "Fields empty",Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        String URL = FoodApi.FOOD_API + "/" + itemId;

                        RequestQueue requestQueue = Volley.newRequestQueue(AdminViewFoodItemDetailsActivity.this);
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("title", titleValue);
                        jsonBody.put("price", priceValue);
                        jsonBody.put("category", selectedCategoryName);
                        jsonBody.put("ingredients", ingredientsValue);
                        jsonBody.put("image", thumbnailUrl);
                        jsonBody.put("status", "Available");

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {

                                        Intent intent = new Intent(AdminViewFoodItemDetailsActivity.this, AdminDashboardActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                    Toast.makeText(AdminViewFoodItemDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(AdminViewFoodItemDetailsActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    return null;
                                }
                            }

                        };

                        requestQueue.add(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnRemove;
                ImageButton btnClose;
                TextView msg;
                removeConfirmDialog.show();

                msg = (TextView) removeConfirmDialog.findViewById(R.id.msg);
                btnRemove = (Button) removeConfirmDialog.findViewById(R.id.btnRemove);
                btnClose = (ImageButton) removeConfirmDialog.findViewById(R.id.btnClose);

                msg.setText(title.getText().toString());

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Remove Function
                        String URL = FoodApi.FOOD_API + "/" + itemId;

                        RequestQueue requestQueue = Volley.newRequestQueue(AdminViewFoodItemDetailsActivity.this);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.DELETE,
                                URL,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            String status = response.getString("status");
                                            String msg = response.getString("msg");

                                            if (status.equals("success")) {

                                                Intent intent = new Intent(AdminViewFoodItemDetailsActivity.this, AdminDashboardActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                            Toast.makeText(AdminViewFoodItemDetailsActivity.this, msg,Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(AdminViewFoodItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                                        System.out.println("" + error.toString());
                                    }
                                }

                        );

                        requestQueue.add(jsonObjectRequest);

                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeConfirmDialog.dismiss();
                    }
                });

            }
        });

    }

    private void showFoodDetails()
    {

        String URL = FoodApi.GET_FOOD_DETAILS + "/" + itemId;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminViewFoodItemDetailsActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            if (response.length() > 0) {

                                JSONObject jsonObject = response.getJSONObject(0);

                                String idValue = jsonObject.getString("id");
                                String titleValue = jsonObject.getString("title");
                                String categoryValue = jsonObject.getString("category");
                                String imageValue = jsonObject.getString("image");
                                String priceValue = jsonObject.getString("price");
                                String ingredientsValue = jsonObject.getString("ingredients");

                                title.setText(" " + titleValue + " ");
                                ingredients.setText(ingredientsValue);
                                price.setText(new DecimalFormat("##.##").format(Double.parseDouble(priceValue)).toString());
                                selectCategory(categoryValue.toUpperCase());
                                selectedCategoryName = categoryValue;
                                thumbnailUrl = imageValue;

                                Uri imgUri = Uri.parse(imageValue);
                                Picasso.get().load(imgUri).into(itemImage);


                            } else {

                                Intent intent = new Intent(AdminViewFoodItemDetailsActivity.this, AdminDashboardActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminViewFoodItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showCategories()
    {
        String URL = CategoryApi.GET_ALL_CATEGORY;

        categoryChipGroup.removeAllViews();

        RequestQueue requestQueue = Volley.newRequestQueue(AdminViewFoodItemDetailsActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for (int index = 0; index < response.length(); index++) {
                                JSONObject jsonObject = response.getJSONObject(index);

                                String categoryName = jsonObject.getString("category");

                                Chip chip = new Chip(AdminViewFoodItemDetailsActivity.this);
                                chip.setText(categoryName);
                                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AdminViewFoodItemDetailsActivity.this, R.color.white)));
                                chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(AdminViewFoodItemDetailsActivity.this, R.color.black)));
                                chip.setCheckable(true);
                                chip.setClickable(true);
                                chip.setFocusable(true);
                                chip.setCheckedIconVisible(true);
                                categoryChipGroup.addView(chip);

                            }

                            if (!selectedCategoryName.equals("")) {
                                selectCategory(selectedCategoryName);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminViewFoodItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void selectCategory(String category)
    {
        if (categoryChipGroup.getChildCount() > 0) {

            for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) categoryChipGroup.getChildAt(i);
                String categoryName = chip.getText().toString();

                if (category.equals(categoryName)) {
                    chip.setChecked(true);
                }

            }

        }
    }

    private void showComments()
    {
        commentArrayList.clear();
        commentsList.setAdapter(null);

        CommentAdapter commentAdapter = new CommentAdapter(this, R.layout.comment_item_row, commentArrayList);
        commentsList.setAdapter(commentAdapter);

        String URL = CommentApi.COMMENT_API + "/" + itemId;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminViewFoodItemDetailsActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for (int index = 0; index < response.length(); index++) {
                                JSONObject jsonObject = response.getJSONObject(index);

                                String nameValue = jsonObject.getString("name");
                                String commentValue = jsonObject.getString("comment");

                                commentArrayList.add(new CommentItems(nameValue, commentValue));
                            }

                            commentAdapter.notifyDataSetChanged();
                            setListViewHeight(commentsList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminViewFoodItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);


    }

//    For select item image
    private void choosePicture() {
        Intent selectPicture = new Intent();
        selectPicture.setType("image/*");
        selectPicture.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectPicture, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri = data.getData();
            itemImage.setImageURI(imgUri);

            uploadPicture();
        }

    }

    private void uploadPicture() {

        final ProgressDialog progressDialog = new ProgressDialog(AdminViewFoodItemDetailsActivity.this);
        progressDialog.setTitle("Uploading ...");
        progressDialog.show();

        StorageReference riversRef = storageReference.child("Foods_Pictures/" + itemId);
        riversRef.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                thumbnailUrl = uri.toString();
                            }
                        });
                        progressDialog.dismiss();
                        Toast.makeText(AdminViewFoodItemDetailsActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminViewFoodItemDetailsActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading... " + (int) progressPercentage + "%");
            }
        });

    }


    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
