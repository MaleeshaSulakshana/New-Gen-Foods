package com.example.newgenfoods.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newgenfoods.Adapters.CommentAdapter;
import com.example.newgenfoods.Api.AddonApi;
import com.example.newgenfoods.Api.CommentApi;
import com.example.newgenfoods.Api.FoodApi;
import com.example.newgenfoods.Classes.Cart;
import com.example.newgenfoods.Classes.User;
import com.example.newgenfoods.Models.AddonItems;
import com.example.newgenfoods.Models.CartItemModel;
import com.example.newgenfoods.Models.CommentItems;
import com.example.newgenfoods.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewItemDetailsActivity extends AppCompatActivity {

    private Spinner addonSpinner;
    private ListView commentsList;
    private ImageView btnAddOneItem, btnRemoveOneItem;
    private TextView txtQty, total, price, title, ingredients, category;
    private Button btnAddToCart, btnAddComment, btnRemoveFromCart;
    private EditText notice, comment;
    private ImageView itemImage;

    private ArrayList<CommentItems> commentArrayList = new ArrayList<>();
    private ArrayList<AddonItems> addonArrayList = new ArrayList<>();

    private ArrayList<String> addonItemsArray = new ArrayList<String>();
    private ArrayAdapter<String> addonAdapter;

    private String itemId = "";
    private String itemPrice = "0";
    private String addonSelectedValue = "Choose your addOn selection";
    private Double totalOrderPrice = 0.00;

    private Cart cart;
    private ArrayList<CartItemModel> cartItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_details);

        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemID");

        addonSpinner = (Spinner) this.findViewById(R.id.addonSpinner);
        commentsList = (ListView) this.findViewById(R.id.commentsList);

        btnAddOneItem = (ImageView) this.findViewById(R.id.btnAddOneItem);
        btnRemoveOneItem = (ImageView) this.findViewById(R.id.btnRemoveOneItem);

        txtQty = (TextView) this.findViewById(R.id.txtQty);
        total = (TextView) this.findViewById(R.id.total);
        price = (TextView) this.findViewById(R.id.price);
        title = (TextView) this.findViewById(R.id.title);
        ingredients = (TextView) this.findViewById(R.id.ingredients);
        category = (TextView) this.findViewById(R.id.category);

        notice = (EditText) this.findViewById(R.id.notice);
        comment = (EditText) this.findViewById(R.id.comment);

        btnAddToCart = (Button) this.findViewById(R.id.btnAddToCart);
        btnRemoveFromCart = (Button) this.findViewById(R.id.btnRemoveFromCart);
        btnAddComment = (Button) this.findViewById(R.id.btnAddComment);

        itemImage = (ImageView) this.findViewById(R.id.itemImage);

        price.setText("Rs. " + new DecimalFormat("##.##").format(Double.parseDouble(itemPrice)).toString());
        txtQty.setText("0");
        total.setText("Rs. " + new DecimalFormat("##.##").format(0.00).toString());

        cart = new Cart();
        cartItemsList = cart.getCartItems();

        showItemDetails();

//        addonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


//        Add an remove qty
        btnAddOneItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer value = Integer.parseInt(txtQty.getText().toString()) + 1;
                txtQty.setText(value.toString());

                calculateTotalPrice();

            }
        });

        btnRemoveOneItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer value = Integer.parseInt(txtQty.getText().toString()) - 1;

                if (value < 0) {
                    value = 0;
                }

                txtQty.setText(value.toString());
                calculateTotalPrice();

            }
        });

//        Add to cart button
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String qtyValue = txtQty.getText().toString();
                if (qtyValue.equals("0")) {
                    Toast.makeText(ViewItemDetailsActivity.this, "Please add item quantity do you want",Toast.LENGTH_SHORT).show();

                } else {

                    CartItemModel cartItem = new CartItemModel(itemId,Integer.parseInt(qtyValue),
                            Double.parseDouble(itemPrice), totalOrderPrice, title.getText().toString(),category.getText().toString(),
                            ingredients.getText().toString(), addonSelectedValue.toString(), notice.getText().toString());

                    cart.removeFromCart(itemId);
                    cart.addToCart(cartItem);
                    btnRemoveFromCart.setVisibility(View.VISIBLE);

                    Toast.makeText(ViewItemDetailsActivity.this, "Item added to cart." + totalOrderPrice.toString(),Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnRemoveFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.removeFromCart(itemId);
                btnRemoveFromCart.setVisibility(View.GONE);
                Toast.makeText(ViewItemDetailsActivity.this, "Item removed from cart." + totalOrderPrice.toString(),Toast.LENGTH_SHORT).show();
            }
        });


        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String commentValue = comment.getText().toString();
                if (commentValue.equals("")) {
                    Toast.makeText(ViewItemDetailsActivity.this, "Please enter comment!",Toast.LENGTH_SHORT).show();

                } else {

                    try {

                        String URL = CommentApi.COMMENT_API;

                        RequestQueue requestQueue = Volley.newRequestQueue(ViewItemDetailsActivity.this);
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("food_id", itemId);
                        jsonBody.put("email", User.LOGGED_USER_EMAIL);
                        jsonBody.put("comment", commentValue);

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {
                                        comment.setText("");
                                        showComments();
                                    }

                                    Toast.makeText(ViewItemDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(ViewItemDetailsActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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


        addonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                addonSelectedValue = (String) parent.getItemAtPosition(position);
                calculateTotalPrice();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

//    For calculate total price
    private void calculateTotalPrice()
    {
        Integer value = Integer.parseInt(txtQty.getText().toString());
        Double price = Double.parseDouble(itemPrice);
        totalOrderPrice = Double.parseDouble(String.valueOf(value * price));

        Double addonPrice = 0.00;
        for (AddonItems item : addonArrayList)
        {

            if (addonSelectedValue.equals(item.getName()))
            {
                addonPrice = Double.parseDouble(item.getPrice().toString());
                break;
            }
        }

        totalOrderPrice = totalOrderPrice + (addonPrice * value);
        total.setText("Rs." + new DecimalFormat("##.##").format(totalOrderPrice).toString());
    }


    private void showItemDetails()
    {

        showFoodDetails();
        showAddons();
        showComments();

        String isExist = "no";
        for (CartItemModel item : cartItemsList) {

            String cartItemId = item.getId();
            if (itemId.equals(cartItemId)) {

                notice.setText(item.getNotice());
                txtQty.setText(item.getQty().toString());
                total.setText("Rs. " + new DecimalFormat("##.##").format(item.getTotal()).toString());

                btnRemoveFromCart.setVisibility(View.VISIBLE);
                isExist = "yes";
                break;
            }

        }

        if (isExist.equals("no")) {
            btnRemoveFromCart.setVisibility(View.GONE);
        }

    }


    private void showFoodDetails()
    {

        String URL = FoodApi.GET_FOOD_DETAILS + "/" + itemId;

        RequestQueue requestQueue = Volley.newRequestQueue(ViewItemDetailsActivity.this);
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
                                category.setText(categoryValue.toUpperCase(Locale.ROOT));
                                ingredients.setText(ingredientsValue);
                                price.setText("Rs. " + new DecimalFormat("##.##").format(Double.parseDouble(priceValue)).toString());

                                itemPrice = priceValue;

                                Uri imgUri = Uri.parse(imageValue);
                                Picasso.get().load(imgUri).into(itemImage);


                            } else {

                                Intent intent = new Intent(ViewItemDetailsActivity.this, UserDashboardActivity.class);
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
                        Toast.makeText(ViewItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }


    private void showAddons()
    {
        addonArrayList.clear();
        addonArrayList.add(new AddonItems("Choose your addOn selection", new Double(0)));
        addonItemsArray.add("Choose your addOn selection");

        String URL = AddonApi.ADDON_API;

        RequestQueue requestQueue = Volley.newRequestQueue(ViewItemDetailsActivity.this);
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
                                String priceValue = jsonObject.getString("price");

                                addonArrayList.add(new AddonItems(nameValue, Double.parseDouble(priceValue)));

                                addonItemsArray.add(nameValue);
                            }

                            addonAdapter = new ArrayAdapter<String>(ViewItemDetailsActivity.this,
                                    R.layout.spinner_row, addonItemsArray);
                            addonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                            addonSpinner.setAdapter(addonAdapter);

                            for (CartItemModel item : cartItemsList) {

                                String cartItemId = item.getId();
                                if (itemId.equals(cartItemId)) {

                                    addonSpinner.setSelection(addonAdapter.getPosition(item.getAddon()));
                                    break;
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showComments()
    {
        commentArrayList.clear();
        commentsList.setAdapter(null);

        CommentAdapter commentAdapter = new CommentAdapter(this, R.layout.comment_item_row, commentArrayList);
        commentsList.setAdapter(commentAdapter);

        String URL = CommentApi.COMMENT_API + "/" + itemId;

        RequestQueue requestQueue = Volley.newRequestQueue(ViewItemDetailsActivity.this);
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
                        Toast.makeText(ViewItemDetailsActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);


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