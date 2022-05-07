package com.example.newgenfoods.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.newgenfoods.Adapters.MenuAdapter;
import com.example.newgenfoods.Adapters.OrderAdapter;
import com.example.newgenfoods.Adapters.OrderDetailsAdapter;
import com.example.newgenfoods.Api.CategoryApi;
import com.example.newgenfoods.Api.FoodApi;
import com.example.newgenfoods.Api.OrderApi;
import com.example.newgenfoods.Api.UserApi;
import com.example.newgenfoods.Classes.Cart;
import com.example.newgenfoods.Classes.User;
import com.example.newgenfoods.MainActivity;
import com.example.newgenfoods.Models.CartItemModel;
import com.example.newgenfoods.Models.MenuItems;
import com.example.newgenfoods.Models.OrderDetails;
import com.example.newgenfoods.Models.OrderItems;
import com.example.newgenfoods.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserDashboardActivity extends AppCompatActivity {

    private Button btnMenu, btnOrders, btnCart, btnCheckOut, btnClearCart, btnProfile, btnSignOut, btnSearch;
    private LinearLayout menuLayout, orderLayout, viewOrdersLayout, cartLayout, cartButtons, showCartStatus, searchLayout;
    private ChipGroup categoryChipGroup;
    private ListView menuItemList, ordersItemList, cartItemList, orderDetailsList, searchItemList;
    private TextView categoryName, total, orderDetailsGoBack, orderStatus, orderId, userName, discountNotice, searchName;
    private SwipeRefreshLayout cartPullToRefresh, ordersItemListPullToRefresh;

    private ArrayList<MenuItems> menuArrayList = new ArrayList<>();
    private ArrayList<OrderItems> orderArrayList = new ArrayList<>();
    private ArrayList<OrderDetails> orderDetailsArrayList = new ArrayList<>();
    private ArrayList<CartItemModel> cartItemArrayList = new ArrayList<>();

    private Cart cart;

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog, accountDialog, changePswDialog, checkoutDialog, searchDialog;

    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Double totalCartPrice = 0.00;
    private String isOfferAvailable = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        exitDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        accountDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        accountDialog.setContentView(R.layout.dialog_box_account_details);

        changePswDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        changePswDialog.setContentView(R.layout.dialog_box_psw_change);

        checkoutDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        checkoutDialog.setContentView(R.layout.dialog_box_checkout_confirm);

        searchDialog = new BottomSheetDialog(UserDashboardActivity.this, R.style.BottomSheetTheme);
        searchDialog.setContentView(R.layout.dialog_box_search);

        btnMenu = (Button) this.findViewById(R.id.btnMenu);
        btnOrders = (Button) this.findViewById(R.id.btnOrders);
        btnCart = (Button) this.findViewById(R.id.btnCart);
        btnProfile = (Button) this.findViewById(R.id.btnProfile);
        btnCheckOut = (Button) this.findViewById(R.id.btnCheckOut);
        btnClearCart = (Button) this.findViewById(R.id.btnClearCart);
        btnSignOut = (Button) this.findViewById(R.id.btnSignOut);
        btnSearch = (Button) this.findViewById(R.id.btnSearch);

        menuLayout = (LinearLayout) this.findViewById(R.id.menuLayout);
        orderLayout = (LinearLayout) this.findViewById(R.id.orderLayout);
        viewOrdersLayout = (LinearLayout) this.findViewById(R.id.viewOrdersLayout);
        cartLayout = (LinearLayout) this.findViewById(R.id.cartLayout);
        cartButtons = (LinearLayout) this.findViewById(R.id.cartButtons);
        showCartStatus = (LinearLayout) this.findViewById(R.id.showCartStatus);
        searchLayout = (LinearLayout) this.findViewById(R.id.searchLayout);

        categoryName = (TextView) this.findViewById(R.id.categoryName);
        total = (TextView) this.findViewById(R.id.total);
        orderDetailsGoBack = (TextView) this.findViewById(R.id.orderDetailsGoBack);
        orderStatus = (TextView) this.findViewById(R.id.orderStatus);
        orderId = (TextView) this.findViewById(R.id.orderId);
        userName = (TextView) this.findViewById(R.id.userName);
        discountNotice = (TextView) this.findViewById(R.id.discountNotice);
        searchName = (TextView) this.findViewById(R.id.searchName);

        menuItemList = (ListView) this.findViewById(R.id.menuItemList);
        ordersItemList = (ListView) this.findViewById(R.id.ordersItemList);
        cartItemList = (ListView) this.findViewById(R.id.cartItemList);
        orderDetailsList = (ListView) this.findViewById(R.id.orderDetailsList);
        searchItemList = (ListView) this.findViewById(R.id.searchItemList);

        cartPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.cartPullToRefresh);
        ordersItemListPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.ordersItemListPullToRefresh);

        categoryChipGroup = (ChipGroup) findViewById(R.id.categoryChipGroup);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        cart = new Cart();

        searchLayout.setVisibility(View.GONE);
        menuLayout.setVisibility(View.VISIBLE);
        orderLayout.setVisibility(View.GONE);
        cartLayout.setVisibility(View.GONE);
        cartItemList.setVisibility(View.GONE);
        viewOrdersLayout.setVisibility(View.GONE);

        showCategories();


//        For chip onclick
        categoryChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                Chip chip = chipGroup.findViewById(i);
                if (chip != null) {
                    String categoryName = chip.getText().toString();
                    showCategoryItems(categoryName);
                }
            }
        });

        searchItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Intent intent = new Intent(UserDashboardActivity.this, ViewItemDetailsActivity.class);
                intent.putExtra("itemID", menuArrayList.get(i).getId());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        menuItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Intent intent = new Intent(UserDashboardActivity.this, ViewItemDetailsActivity.class);
                intent.putExtra("itemID", menuArrayList.get(i).getId());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        ordersItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                searchLayout.setVisibility(View.GONE);
                orderLayout.setVisibility(View.GONE);
                cartLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.VISIBLE);

                showOrderDetails(orderArrayList.get(i).getOrderNo());
            }
        });

        cartItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Intent intent = new Intent(UserDashboardActivity.this, ViewItemDetailsActivity.class);
                intent.putExtra("itemID", cartItemArrayList.get(i).getId());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnSearch;
                ImageButton btnClose;
                EditText value;
                searchDialog.show();

                btnSearch = (Button) searchDialog.findViewById(R.id.btnSearch);
                btnClose = (ImageButton) searchDialog.findViewById(R.id.btnClose);
                value = (EditText) searchDialog.findViewById(R.id.value);

                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String searchValue = value.getText().toString();
                        if (searchValue.equals("")) {
                            Toast.makeText(UserDashboardActivity.this, "Fields empty",Toast.LENGTH_SHORT).show();

                        } else {

                            orderLayout.setVisibility(View.GONE);
                            cartLayout.setVisibility(View.GONE);
                            viewOrdersLayout.setVisibility(View.GONE);
                            menuLayout.setVisibility(View.GONE);
                            searchLayout.setVisibility(View.VISIBLE);

                            searchDialog.dismiss();

                            showSearchItem(searchValue);

                        }

                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchDialog.dismiss();
                    }
                });

            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchLayout.setVisibility(View.GONE);
                orderLayout.setVisibility(View.GONE);
                cartLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.VISIBLE);

                showCategories();

            }
        });

        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                cartLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                orderLayout.setVisibility(View.VISIBLE);

                showOrderItems();

            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchLayout.setVisibility(View.GONE);
                orderLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                cartLayout.setVisibility(View.VISIBLE);

                showCartItems();
                checkOffers();

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnEditAccount;
                ImageButton btnCloseForm;
                TextView btnPswChangeForm, dayCount;
                EditText name, email, dob;
                accountDialog.show();

                btnEditAccount = (Button) accountDialog.findViewById(R.id.btnEditAccount);
                btnCloseForm = (ImageButton) accountDialog.findViewById(R.id.btnCloseForm);
                btnPswChangeForm = (TextView) accountDialog.findViewById(R.id.btnPswChangeForm);
                dayCount = (TextView) accountDialog.findViewById(R.id.dayCount);
                name = (EditText) accountDialog.findViewById(R.id.name);
                email = (EditText) accountDialog.findViewById(R.id.email);
                dob = (EditText) accountDialog.findViewById(R.id.dob);

                email.setFocusable(false);
                email.setCursorVisible(false);
                email.setKeyListener(null);

                dob.setFocusable(false);
                dob.setCursorVisible(false);
                dob.setKeyListener(null);

                String URL = UserApi.USER_API + "/" + User.LOGGED_USER_EMAIL;

                RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
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

                                        String emailValue = jsonObject.getString("email");
                                        String nameValue = jsonObject.getString("name");
                                        String dobValue = jsonObject.getString("dob");

//                                        Calculate next birthday day count
                                        try {

                                            String strDob = dobValue.replace("/", "-");
                                            String dobSplit[]  = strDob.split("-");
                                            String dobYear = dobSplit[0];
                                            String dobMonth = dobSplit[1];
                                            String dobDate = dobSplit[2];

                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            String todayDate = df.format(Calendar.getInstance().getTime());
                                            String todaySplit[]  = todayDate.split("-");
                                            String thisYear = todaySplit[0];

                                            String thisYearDobDate = thisYear + "-" + dobMonth + "-" + dobDate;

                                            Date parseDob = df.parse(thisYearDobDate);
                                            Date parseToday = df.parse(todayDate);

                                            long diff = parseDob.getTime() - parseToday.getTime();
                                            long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                                            if (daysDiff < 0) {
                                                Integer nextYear = Integer.parseInt(thisYear) + 1;
                                                String nextYearDobDate = nextYear + "-" + dobMonth + "-" + dobDate;

                                                parseDob = df.parse(nextYearDobDate);
                                                parseToday = df.parse(todayDate);

                                                diff = parseDob.getTime() - parseToday.getTime();
                                                daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                                            }

                                            if (daysDiff == 0) {
                                                dayCount.setText("Happy Birthday " + nameValue +
                                                        ". Today is your Birthday. Please come to us celebrate with us.");

                                            } else {
                                                dayCount.setText("You have " + Long.toString(daysDiff) + " days to your next birthday.");
                                            }


                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        email.setText(emailValue);
                                        name.setText(nameValue);
                                        dob.setText(dobValue);

                                    } else {

                                        accountDialog.dismiss();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                                System.out.println("" + error.toString());
                            }
                        }

                );

                requestQueue.add(jsonArrayRequest);


                onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

//                Update label
                        String mFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(mFormat, Locale.US);
                        dob.setText(sdf.format(mCalendar.getTime()));
                    }
                };

                dob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(UserDashboardActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                onDateSetListener,
                                year, month, day);
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();

                    }
                });

                btnEditAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nameValue = name.getText().toString();
                        String dobValue = dob.getText().toString();

                        if (nameValue.equals("") || dobValue.equals("")) {
                            Toast.makeText(UserDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                        } else {


                            try {

                                String URL = UserApi.USER_API + "/" + User.LOGGED_USER_EMAIL;

                                RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("name", nameValue);
                                jsonBody.put("dob", dobValue);

                                final String requestBody = jsonBody.toString();

                                StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            String msg = jsonObject.getString("msg");
                                            Toast.makeText(UserDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(UserDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

                btnCloseForm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accountDialog.dismiss();
                    }
                });

                btnPswChangeForm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accountDialog.dismiss();

                        Button btnChangePsw;
                        ImageButton btnClosePswForm;
                        EditText npsw, cnpsw;
                        changePswDialog.show();

                        btnChangePsw = (Button) changePswDialog.findViewById(R.id.btnChangePsw);
                        btnClosePswForm = (ImageButton) changePswDialog.findViewById(R.id.btnClosePswForm);
                        npsw = (EditText) changePswDialog.findViewById(R.id.npsw);
                        cnpsw = (EditText) changePswDialog.findViewById(R.id.cnpsw);

                        btnChangePsw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String npswValue = npsw.getText().toString();
                                String cnpswValue = cnpsw.getText().toString();

                                if (npswValue.equals("") || cnpswValue.equals("")) {
                                    Toast.makeText(UserDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                                } else if (!npswValue.equals(cnpswValue)) {
                                    Toast.makeText(UserDashboardActivity.this, "Password and confirm password not matched!",Toast.LENGTH_SHORT).show();

                                } else {

                                    try {

                                        String URL = UserApi.POST_PSW_CHANGE + "/" + User.LOGGED_USER_EMAIL;

                                        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
                                        JSONObject jsonBody = new JSONObject();
                                        jsonBody.put("psw", npswValue);

                                        final String requestBody = jsonBody.toString();

                                        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);

                                                    String status = jsonObject.getString("status");
                                                    String msg = jsonObject.getString("msg");

                                                    if (status.equals("success")) {
                                                        npsw.setText("");
                                                        cnpsw.setText("");
                                                    }

                                                    Toast.makeText(UserDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(UserDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

                        btnClosePswForm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changePswDialog.dismiss();
                            }
                        });

                    }
                });

            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.clear();
                editor.apply();

                Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        orderDetailsGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchLayout.setVisibility(View.GONE);
                menuLayout.setVisibility(View.GONE);
                cartLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                orderLayout.setVisibility(View.VISIBLE);

            }
        });

        cartPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showCartItems();
                cartPullToRefresh.setRefreshing(false);
            }
        });

        ordersItemListPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showOrderItems();
                ordersItemListPullToRefresh.setRefreshing(false);
            }
        });

//        For clear cart
        btnClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cart.cartClear();
                showCartItems();
                Toast.makeText(UserDashboardActivity.this, "Your cart cleared!",Toast.LENGTH_SHORT).show();

            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkoutCart();

            }
        });


    }

    private void checkOffers()
    {

        if (isOfferAvailable.equals("no")) {

            String URL = OrderApi.GET_OFFER_DETAILS + "/" + User.LOGGED_USER_EMAIL;

            RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONArray response) {

                            try {

                                Integer jsonLength = response.length();

                                if (jsonLength > 0) {

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    String today = df.format(Calendar.getInstance().getTime());

                                    String order1 = response.getJSONObject(0).getString("date");
                                    String order2 = response.getJSONObject(1).getString("date");
                                    String order3 = response.getJSONObject(2).getString("date");

                                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
                                    LocalDate date0 = LocalDate.parse(today, dateFormatter);
                                    LocalDate date1 = LocalDate.parse(order1, dateFormatter);
                                    LocalDate date2 = LocalDate.parse(order2, dateFormatter);
                                    LocalDate date3 = LocalDate.parse(order3, dateFormatter);

                                    long def1 = Math.abs(ChronoUnit.DAYS.between(date0, date1));
                                    long def2 = Math.abs(ChronoUnit.DAYS.between(date1, date2));
                                    long def3 = Math.abs(ChronoUnit.DAYS.between(date2, date3));

                                    Integer totalDays = Math.toIntExact(def1 + def2 + def3);
                                    if (totalDays < 15) {
                                        isOfferAvailable = "yes";
                                        discountNotice.setVisibility(View.VISIBLE);
                                        showCartItems();
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
                            Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                            System.out.println("" + error.toString());
                        }
                    }

            );

            requestQueue.add(jsonArrayRequest);

        }

    }


    private void checkoutCart()
    {

        ArrayList<CartItemModel> cartArrayList = cart.getCartItems();
        Integer cartLength = cartArrayList.size();

        if (cartLength < 1) {

            total.setText("Your cart is empty!.");
            showCartItems();

        } else {

            checkOffers();

            Button btnCheckOut;
            ImageButton btnClose;
            checkoutDialog.show();

            btnClose = (ImageButton) checkoutDialog.findViewById(R.id.btnClose);
            btnCheckOut = (Button) checkoutDialog.findViewById(R.id.btnCheckOut);

            btnCheckOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try {

                        String URL = OrderApi.ORDER_API;

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String date = df.format(Calendar.getInstance().getTime());

                        JSONArray  jsonDetailsArray = new JSONArray ();

                        for (Integer i = 0; cartArrayList.size() > i; i++) {

                            CartItemModel cartItem = cartItemArrayList.get(i);

                            String addonValue = "";
                            if (!cartItem.getAddon().equals("Choose your addOn selection")) {
                                addonValue =  cartItem.getAddon();
                            }

                            JSONObject jsonDetailsItem = new JSONObject();
                            jsonDetailsItem.put("food_id", cartItem.getId());
                            jsonDetailsItem.put("qty", cartItem.getQty());
                            jsonDetailsItem.put("addon", addonValue);
                            jsonDetailsItem.put("notice", cartItem.getNotice());

                            jsonDetailsArray.put(jsonDetailsItem);

                        }

                        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("email", User.LOGGED_USER_EMAIL);
                        jsonBody.put("date", date);
                        jsonBody.put("total", totalCartPrice.toString());
                        jsonBody.put("status_type", "Pending");
                        jsonBody.put("discount", isOfferAvailable);
                        jsonBody.put("details", jsonDetailsArray);

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String status = jsonObject.getString("status");
                                    String msg = jsonObject.getString("msg");

                                    if (status.equals("success")) {
                                        cart.cartClear();

                                        isOfferAvailable = "no";
                                        discountNotice.setVisibility(View.GONE);

                                        showCartItems();
                                        checkoutDialog.dismiss();
                                    }

                                    Toast.makeText(UserDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(UserDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkoutDialog.dismiss();
                }
            });

        }

    }


    private void showCategories()
    {
        String URL = CategoryApi.CATEGORY_API;

        categoryChipGroup.removeAllViews();

        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
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

                                Chip chip = new Chip(UserDashboardActivity.this);
                                chip.setText(categoryName);
                                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(UserDashboardActivity.this, R.color.white)));
                                chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(UserDashboardActivity.this, R.color.black)));
                                chip.setCheckable(true);
                                chip.setClickable(true);
                                chip.setFocusable(false);
                                chip.setCheckedIconVisible(false);
                                categoryChipGroup.addView(chip);

                            }

                            if (categoryChipGroup.getChildCount() > 0) {
                                Chip chip = (Chip)categoryChipGroup.getChildAt(0);
                                String category = chip.getText().toString();
                                showCategoryItems(category);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showCategoryItems(String category)
    {
        categoryName.setText(category + " ");
        menuArrayList.clear();
        menuItemList.setAdapter(null);

        MenuAdapter menuAdapter = new MenuAdapter(this, R.layout.menu_item_row, menuArrayList);
        menuItemList.setAdapter(menuAdapter);

        String URL = FoodApi.GET_AVAILABLE_FOODS + "/" + category;

        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
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

                                String idValue = jsonObject.getString("id");
                                String titleValue = jsonObject.getString("title");
                                String priceValue = jsonObject.getString("price");
                                String ingredientsValue = jsonObject.getString("ingredients");
                                String statusValue = jsonObject.getString("status");

                                menuArrayList.add(new MenuItems(idValue, titleValue, priceValue, ingredientsValue, statusValue));

                            }

                            menuAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showSearchItem(String value)
    {
        searchName.setText(value + " ");
        menuArrayList.clear();
        searchItemList.setAdapter(null);

        MenuAdapter menuAdapter = new MenuAdapter(this, R.layout.menu_item_row, menuArrayList);
        searchItemList.setAdapter(menuAdapter);

        String URL = FoodApi.GET_SEARCH_FOOD_API + "/" + value;

        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
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

                                String idValue = jsonObject.getString("id");
                                String titleValue = jsonObject.getString("title");
                                String priceValue = jsonObject.getString("price");
                                String ingredientsValue = jsonObject.getString("ingredients");
                                String statusValue = jsonObject.getString("status");

                                menuArrayList.add(new MenuItems(idValue, titleValue, priceValue, ingredientsValue, statusValue));

                            }

                            menuAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

//    Show order items on list view
    private void showOrderItems()
    {
        orderArrayList.clear();
        ordersItemList.setAdapter(null);

        OrderAdapter orderAdapter = new OrderAdapter(this, R.layout.order_item_row, orderArrayList);
        ordersItemList.setAdapter(orderAdapter);

        String URL = OrderApi.GET_ORDERS_BY_USER + "/" + User.LOGGED_USER_EMAIL;

        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
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

                                String idValue = jsonObject.getString("id");
                                String statusValue = jsonObject.getString("status");

                                orderArrayList.add(new OrderItems(idValue, statusValue));

                            }

                            orderAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                        System.out.println("" + error.toString());
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showOrderDetails(String id)
    {
        orderDetailsArrayList.clear();
        orderDetailsList.setAdapter(null);

        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(this, R.layout.order_details_item_row, orderDetailsArrayList);
        orderDetailsList.setAdapter(orderDetailsAdapter);

        String URL = OrderApi.GET_ORDER_DETAILS + "/" + id;

        RequestQueue requestQueue = Volley.newRequestQueue(UserDashboardActivity.this);
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
                                String statusValue = jsonObject.getString("status");
                                String nameValue = jsonObject.getString("name");

                                orderId.setText(id);
                                userName.setText(nameValue);
                                orderStatus.setText(statusValue);

                            }

                            for (int index = 0; index < response.length(); index++) {
                                JSONObject jsonObject = response.getJSONObject(index);

                                String idValue = jsonObject.getString("id");
                                String titleValue = jsonObject.getString("title");
                                String qtyValue = jsonObject.getString("qty");
                                String addonValue = jsonObject.getString("addon");
                                String noticeValue = jsonObject.getString("notice");

                                orderDetailsArrayList.add(new OrderDetails(idValue, titleValue, qtyValue, addonValue, noticeValue));

                            }

                            orderDetailsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                        System.out.println("" + error.toString());
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }


//    Show cart items on list view
    private void showCartItems()
    {
        cartItemArrayList = cart.getCartItems();
        Integer cartLength = cartItemArrayList.size();

        if (cartLength < 1) {

            cartButtons.setVisibility(View.GONE);
            cartItemList.setVisibility(View.GONE);
            showCartStatus.setVisibility(View.VISIBLE);

            total.setText("Rs. " + new DecimalFormat("##.##").format(0.00).toString());

        } else {

            cartButtons.setVisibility(View.VISIBLE);
            cartItemList.setVisibility(View.VISIBLE);
            showCartStatus.setVisibility(View.GONE);

            cartItemList.setAdapter(null);
            CartAdapter cartAdapter = new CartAdapter(this, R.layout.cart_item_row, cartItemArrayList);
            cartItemList.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();

            Double totalPrice = 0.0;
            for(CartItemModel item : cartItemArrayList) {
                Double value = Double.parseDouble(item.getTotal().toString());
                totalPrice += value;
            }

            if (isOfferAvailable.equals("yes")) {
                Double offerAmount = ((totalPrice / 100) * 5);
                totalPrice = (totalPrice - offerAmount);
            }

            totalCartPrice = totalPrice;
            total.setText("Rs. " + new DecimalFormat("##.##").format(totalPrice).toString());

        }

    }


//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        Button btnExitYes, btnExitNo;
        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

}

class CartAdapter extends ArrayAdapter<CartItemModel> {

    private Context mContext;
    private int mResource;

    public CartAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CartItemModel> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView notice = (TextView) convertView.findViewById(R.id.notice);
        TextView addons = (TextView) convertView.findViewById(R.id.addons);
        TextView qty = (TextView) convertView.findViewById(R.id.qty);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        Double priceValue = Double.parseDouble(getItem(position).getTotal().toString());

        title.setText(getItem(position).getTitle());
        notice.setText(getItem(position).getNotice());

        String addonName = getItem(position).getAddon();
        if (!addonName.equals("")) {
            addonName = "with " + addonName;
        }

        addons.setText(addonName);
        qty.setText(getItem(position).getQty() + " Items");
        price.setText("Rs. " + new DecimalFormat("##.##").format(priceValue).toString());

        return convertView;
    }

}