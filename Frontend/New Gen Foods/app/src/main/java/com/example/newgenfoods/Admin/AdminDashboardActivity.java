package com.example.newgenfoods.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newgenfoods.Utils.EmailValidator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.newgenfoods.Adapters.MenuAdapter;
import com.example.newgenfoods.Adapters.OrderAdapter;
import com.example.newgenfoods.Adapters.OrderDetailsAdapter;
import com.example.newgenfoods.Api.AddonApi;
import com.example.newgenfoods.Api.AdminApi;
import com.example.newgenfoods.Api.CategoryApi;
import com.example.newgenfoods.Api.FoodApi;
import com.example.newgenfoods.Api.OrderApi;
import com.example.newgenfoods.Classes.User;
import com.example.newgenfoods.MainActivity;
import com.example.newgenfoods.Models.AddonItems;
import com.example.newgenfoods.Models.AdminItems;
import com.example.newgenfoods.Models.CategoryItems;
import com.example.newgenfoods.Models.MenuItems;
import com.example.newgenfoods.Models.OrderDetails;
import com.example.newgenfoods.Models.OrderItems;
import com.example.newgenfoods.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class AdminDashboardActivity extends AppCompatActivity {

    private TextView todayDateView, txtOrderType, categoryName,
            orderDetailsGoBack, orderStatus, orderId, requesterName;

    private Button btnPending, btnProcessing, btnCompleted, btnDelivered,
            btnFoodItems, btnCategories, btnAddons, btnAdmins, btnProfile, btnSignOut,
            btnAddNewItem, btnAddNewAddon, btnAddNewCategory, btnAddNewAdmin;

    private LinearLayout ordersLayout, foodItemsLayout, addonsLayout,
            categoriesLayout, adminsLayout, viewOrdersLayout;

    private ListView orderItemsList, orderDetailsList, menuItemList,
            addonItemList, categoriesItemList, adminsItemList;

    private SwipeRefreshLayout orderItemsListPullToRefresh, menuItemListPullToRefresh, categoriesItemListPullToRefresh,
            addonItemListPullToRefresh, adminsItemListPullToRefresh;

    private ChipGroup categoryChipGroup;

    private ArrayList<OrderItems> orderArrayList = new ArrayList<>();
    private ArrayList<OrderDetails> orderDetailsArrayList = new ArrayList<>();
    private ArrayList<MenuItems> menuArrayList = new ArrayList<>();
    private ArrayList<AddonItems> addonArrayList = new ArrayList<>();
    private ArrayList<CategoryItems> categoriesArrayList = new ArrayList<>();
    private ArrayList<AdminItems> adminsArrayList = new ArrayList<>();

    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetDialog exitDialog, accountDialog, addAddonDialog,
            changePswDialog, addCategoryDialog, removeConfirmDialog,
            itemDialogBox, itemStatusDialogBox, addNewAdminDialog;

    private String viewOrderType = "";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        exitDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        exitDialog.setContentView(R.layout.dialog_box_exit);

        accountDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        accountDialog.setContentView(R.layout.dialog_box_admin_account_details);

        changePswDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        changePswDialog.setContentView(R.layout.dialog_box_psw_change);

        addAddonDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        addAddonDialog.setContentView(R.layout.dialog_box_add_addon);

        addCategoryDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        addCategoryDialog.setContentView(R.layout.dialog_box_add_category);

        removeConfirmDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        removeConfirmDialog.setContentView(R.layout.dialog_box_remove_confirm);

        itemDialogBox = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        itemDialogBox.setContentView(R.layout.dialog_box_item_status);

        itemStatusDialogBox = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        itemStatusDialogBox.setContentView(R.layout.dialog_box_item_status_update);

        addNewAdminDialog = new BottomSheetDialog(AdminDashboardActivity.this, R.style.BottomSheetTheme);
        addNewAdminDialog.setContentView(R.layout.dialog_box_add_admin);

        todayDateView = (TextView) this.findViewById(R.id.todayDateView);
        txtOrderType = (TextView) this.findViewById(R.id.txtOrderType);
        categoryName = (TextView) this.findViewById(R.id.categoryName);
        orderStatus = (TextView) this.findViewById(R.id.orderStatus);
        orderDetailsGoBack = (TextView) this.findViewById(R.id.orderDetailsGoBack);
        orderId = (TextView) this.findViewById(R.id.orderId);
        requesterName = (TextView) this.findViewById(R.id.requesterName);

        btnPending = (Button) this.findViewById(R.id.btnPending);
        btnProcessing = (Button) this.findViewById(R.id.btnProcessing);
        btnCompleted = (Button) this.findViewById(R.id.btnCompleted);
        btnDelivered = (Button) this.findViewById(R.id.btnDelivered);
        btnFoodItems = (Button) this.findViewById(R.id.btnFoodItems);
        btnCategories = (Button) this.findViewById(R.id.btnCategories);
        btnAddons = (Button) this.findViewById(R.id.btnAddons);
        btnAdmins = (Button) this.findViewById(R.id.btnAdmins);
        btnProfile = (Button) this.findViewById(R.id.btnProfile);
        btnSignOut = (Button) this.findViewById(R.id.btnSignOut);
        btnAddNewItem = (Button) this.findViewById(R.id.btnAddNewItem);
        btnAddNewAddon = (Button) this.findViewById(R.id.btnAddNewAddon);
        btnAddNewCategory = (Button) this.findViewById(R.id.btnAddNewCategory);
        btnAddNewAdmin = (Button) this.findViewById(R.id.btnAddNewAdmin);

        ordersLayout = (LinearLayout) this.findViewById(R.id.ordersLayout);
        viewOrdersLayout = (LinearLayout) this.findViewById(R.id.viewOrdersLayout);
        foodItemsLayout = (LinearLayout) this.findViewById(R.id.foodItemsLayout);
        addonsLayout = (LinearLayout) this.findViewById(R.id.addonsLayout);
        categoriesLayout = (LinearLayout) this.findViewById(R.id.categoriesLayout);
        adminsLayout = (LinearLayout) this.findViewById(R.id.adminsLayout);

        orderItemsList = (ListView) this.findViewById(R.id.orderItemsList);
        orderDetailsList = (ListView) this.findViewById(R.id.orderDetailsList);
        menuItemList = (ListView) this.findViewById(R.id.menuItemList);
        addonItemList = (ListView) this.findViewById(R.id.addonItemList);
        categoriesItemList = (ListView) this.findViewById(R.id.categoriesItemList);
        adminsItemList = (ListView) this.findViewById(R.id.adminsItemList);

        orderItemsListPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.orderItemsListPullToRefresh);
        menuItemListPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.menuItemListPullToRefresh);
        categoriesItemListPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.categoriesItemListPullToRefresh);
        addonItemListPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.addonItemListPullToRefresh);
        adminsItemListPullToRefresh = (SwipeRefreshLayout) this.findViewById(R.id.adminsItemListPullToRefresh);

        categoryChipGroup = (ChipGroup) findViewById(R.id.categoryChipGroup);

//        For shared preferences
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ordersLayout.setVisibility(View.VISIBLE);
        viewOrdersLayout.setVisibility(View.GONE);
        foodItemsLayout.setVisibility(View.GONE);
        addonsLayout.setVisibility(View.GONE);
        adminsLayout.setVisibility(View.GONE);
        categoriesLayout.setVisibility(View.GONE);


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = df.format(Calendar.getInstance().getTime());
        todayDateView.setText(date);

        showMenuItems();
        showAddonItems();
        showCategoriesItems();
        showAdminsItems();
        showOrders("Pending");

        orderItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                ordersLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.VISIBLE);

                showOrderDetails(orderArrayList.get(i).getOrderNo());
            }
        });

        orderDetailsGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.VISIBLE);
                foodItemsLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);

            }
        });

        orderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String orderIdValue = orderId.getText().toString();
                String orderStatusText = orderStatus.getText().toString();

                Button btnChange;
                ImageButton btnClose;
                ChipGroup statusChipGroup;
                String[] statusItems = {"Pending", "Processing", "Completed", "Delivered"};
                final String[] selectedStatus = {""};

                itemStatusDialogBox.show();

                btnChange = (Button) itemStatusDialogBox.findViewById(R.id.btnChange);
                btnClose = (ImageButton) itemStatusDialogBox.findViewById(R.id.btnClose);
                statusChipGroup = (ChipGroup) itemStatusDialogBox.findViewById(R.id.statusChipGroup);

                statusChipGroup.removeAllViews();

                for(String item : statusItems) {
                    Chip chip = new Chip(AdminDashboardActivity.this);
                    chip.setText(item);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AdminDashboardActivity.this, R.color.white)));
                    chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(AdminDashboardActivity.this, R.color.black)));
                    chip.setCheckable(true);
                    chip.setClickable(true);
                    chip.setFocusable(true);
                    chip.setCheckedIconVisible(true);
                    statusChipGroup.addView(chip);
                }

                if (statusChipGroup.getChildCount() > 0) {

                    for (int i = 0; i < statusChipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) statusChipGroup.getChildAt(i);
                        String statusName = chip.getText().toString();

                        if (orderStatusText.equals(statusName)) {
                            chip.setChecked(true);
                        }

                    }

                }

                statusChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(ChipGroup chipGroup, int i) {

                        Chip chip = chipGroup.findViewById(i);
                        if (chip != null) {
                            String statusName = chip.getText().toString();
                            selectedStatus[0] = statusName;
                        }
                    }
                });

                btnChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String selectedStatusValue = selectedStatus[0];
                        if (selectedStatusValue.equals("")) {
                            Toast.makeText(AdminDashboardActivity.this, "Status not selected!",Toast.LENGTH_SHORT).show();

                        } else {

                            try {
                                String URL = OrderApi.PUT_ORDER_STATUS + "/" + orderIdValue;

                                RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("status", selectedStatusValue);

                                final String requestBody = jsonBody.toString();

                                StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            String status = jsonObject.getString("status");
                                            String msg = jsonObject.getString("msg");

                                            if (status.equals("success")) {
                                                orderStatus.setText(selectedStatusValue);
                                                itemStatusDialogBox.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemStatusDialogBox.dismiss();
                    }
                });

            }
        });

        menuItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String menuSelectedValueId = menuArrayList.get(i).getId().toString();
                String statusSelectedValue = menuArrayList.get(i).getStatus().toString();

                Button btnStatus, btnDetails;
                ImageButton btnClose;
                itemDialogBox.show();

                btnStatus = (Button) itemDialogBox.findViewById(R.id.btnStatus);
                btnDetails = (Button) itemDialogBox.findViewById(R.id.btnDetails);
                btnClose = (ImageButton) itemDialogBox.findViewById(R.id.btnClose);

                btnStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        itemDialogBox.dismiss();

                        Button btnChange;
                        ImageButton btnClose;
                        ChipGroup statusChipGroup;
                        String[] statusItems = {"Available", "NotAvailable"};
                        final String[] selectedStatus = {""};

                        itemStatusDialogBox.show();

                        btnChange = (Button) itemStatusDialogBox.findViewById(R.id.btnChange);
                        btnClose = (ImageButton) itemStatusDialogBox.findViewById(R.id.btnClose);
                        statusChipGroup = (ChipGroup) itemStatusDialogBox.findViewById(R.id.statusChipGroup);

                        statusChipGroup.removeAllViews();

                        for(String item : statusItems) {
                            Chip chip = new Chip(AdminDashboardActivity.this);
                            chip.setText(item);
                            chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AdminDashboardActivity.this, R.color.white)));
                            chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(AdminDashboardActivity.this, R.color.black)));
                            chip.setCheckable(true);
                            chip.setClickable(true);
                            chip.setFocusable(true);
                            chip.setCheckedIconVisible(true);
                            statusChipGroup.addView(chip);
                        }

                        if (statusChipGroup.getChildCount() > 0) {

                            for (int i = 0; i < statusChipGroup.getChildCount(); i++) {
                                Chip chip = (Chip) statusChipGroup.getChildAt(i);
                                String statusName = chip.getText().toString();

                                if (statusSelectedValue.equals(statusName)) {
                                    chip.setChecked(true);
                                }

                            }

                        }

                        statusChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(ChipGroup chipGroup, int i) {

                                Chip chip = chipGroup.findViewById(i);
                                if (chip != null) {
                                    String statusName = chip.getText().toString();
                                    selectedStatus[0] = statusName;
                                }
                            }
                        });

                        btnChange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String selectedStatusValue = selectedStatus[0];

                                if (selectedStatusValue.equals("")) {
                                    Toast.makeText(AdminDashboardActivity.this, "Status not selected!",Toast.LENGTH_SHORT).show();

                                } else {

                                    try {
                                        String URL = FoodApi.PUT_FOOD_STATUS + "/" + menuSelectedValueId;

                                        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
                                        JSONObject jsonBody = new JSONObject();
                                        jsonBody.put("status", selectedStatusValue);

                                        final String requestBody = jsonBody.toString();

                                        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);

                                                    String status = jsonObject.getString("status");
                                                    String msg = jsonObject.getString("msg");

                                                    if (status.equals("success")) {
                                                        itemStatusDialogBox.dismiss();
                                                    }

                                                    Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemStatusDialogBox.dismiss();
                            }
                        });

                    }
                });

                btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        itemDialogBox.dismiss();

                        Intent intent = new Intent(AdminDashboardActivity.this, AdminViewFoodItemDetailsActivity.class);
                        intent.putExtra("itemID", menuArrayList.get(i).getId());
                        intent.putExtra("itemMode", "Exist");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                });

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemDialogBox.dismiss();
                    }
                });


            }
        });


//        Remove buttons function
        categoriesItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String value =  categoriesArrayList.get(i).getName();

                Button btnRemove;
                ImageButton btnClose;
                TextView msg;
                removeConfirmDialog.show();

                msg = (TextView) removeConfirmDialog.findViewById(R.id.msg);
                btnRemove = (Button) removeConfirmDialog.findViewById(R.id.btnRemove);
                btnClose = (ImageButton) removeConfirmDialog.findViewById(R.id.btnClose);

                msg.setText(value.toString());

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Remove Function
                        String URL = CategoryApi.CATEGORY_API + "/" + value;

                        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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
                                                showCategoriesItems();
                                                removeConfirmDialog.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg,Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
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

        addonItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String value =  addonArrayList.get(i).getName();

                Button btnRemove;
                ImageButton btnClose;
                TextView msg;
                removeConfirmDialog.show();

                msg = (TextView) removeConfirmDialog.findViewById(R.id.msg);
                btnRemove = (Button) removeConfirmDialog.findViewById(R.id.btnRemove);
                btnClose = (ImageButton) removeConfirmDialog.findViewById(R.id.btnClose);

                msg.setText(value.toString());

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Remove Function
                        String URL = AddonApi.ADDON_API + "/" + value;

                        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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
                                                showAddonItems();
                                                removeConfirmDialog.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg,Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
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

        adminsItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                String value =  adminsArrayList.get(i).getName();
                String email =  adminsArrayList.get(i).getEmail();

                Button btnRemove;
                ImageButton btnClose;
                TextView msg;
                removeConfirmDialog.show();

                msg = (TextView) removeConfirmDialog.findViewById(R.id.msg);
                btnRemove = (Button) removeConfirmDialog.findViewById(R.id.btnRemove);
                btnClose = (ImageButton) removeConfirmDialog.findViewById(R.id.btnClose);

                msg.setText(value.toString());

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Remove Function
                        String URL = AdminApi.ADMIN_API + "/" + email;

                        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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
                                                showAdminsItems();
                                                removeConfirmDialog.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg,Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
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

        btnPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.VISIBLE);
                viewOrdersLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);

                viewOrderType = "Pending";
                showOrders("Pending");

            }
        });

        btnProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.VISIBLE);
                viewOrdersLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);

                viewOrderType = "Processing";
                showOrders("Processing");

            }
        });

        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.VISIBLE);
                viewOrdersLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);

                viewOrderType = "Completed";
                showOrders("Completed");

            }
        });

        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.VISIBLE);
                viewOrdersLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);

                viewOrderType = "Delivered";
                showOrders("Delivered");

            }
        });

        btnFoodItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.VISIBLE);

                showMenuItems();

            }
        });

        btnCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.VISIBLE);

                showCategoriesItems();

            }
        });

        btnAddons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.VISIBLE);

                showAddonItems();

            }
        });

        btnAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ordersLayout.setVisibility(View.GONE);
                viewOrdersLayout.setVisibility(View.GONE);
                foodItemsLayout.setVisibility(View.GONE);
                categoriesLayout.setVisibility(View.GONE);
                addonsLayout.setVisibility(View.GONE);
                adminsLayout.setVisibility(View.VISIBLE);

                showAdminsItems();

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnEditAccount;
                ImageButton btnCloseForm;
                TextView btnPswChangeForm;
                EditText name, email;
                accountDialog.show();

                btnEditAccount = (Button) accountDialog.findViewById(R.id.btnEditAccount);
                btnCloseForm = (ImageButton) accountDialog.findViewById(R.id.btnCloseForm);
                btnPswChangeForm = (TextView) accountDialog.findViewById(R.id.btnPswChangeForm);

                name = (EditText) accountDialog.findViewById(R.id.name);
                email = (EditText) accountDialog.findViewById(R.id.email);

                email.setFocusable(false);
                email.setCursorVisible(false);
                email.setKeyListener(null);

                String URL = AdminApi.ADMIN_PROFILE_API + "/" + User.LOGGED_USER_EMAIL;

                RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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

                                        email.setText(emailValue);
                                        name.setText(nameValue);

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
                                Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }

                );

                requestQueue.add(jsonArrayRequest);

                btnEditAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nameValue = name.getText().toString();

                        if (nameValue.equals("")) {
                            Toast.makeText(AdminDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                        } else {

                            try {

                                String URL = AdminApi.ADMIN_API + "/" + User.LOGGED_USER_EMAIL;

                                RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
                                JSONObject jsonBody = new JSONObject();

                                jsonBody.put("name", nameValue);

                                final String requestBody = jsonBody.toString();

                                StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            String msg = jsonObject.getString("msg");
                                            Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(AdminDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                                } else if (!npswValue.equals(cnpswValue)) {
                                    Toast.makeText(AdminDashboardActivity.this, "Password and confirm password not matched!",Toast.LENGTH_SHORT).show();

                                } else {

                                    try {

                                        String URL = AdminApi.POST_PSW_CHANGE + "/" + User.LOGGED_USER_EMAIL;

                                        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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

                                                    Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

//        Pull to list refresh
        orderItemsListPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showOrders(viewOrderType);
                orderItemsListPullToRefresh.setRefreshing(false);

            }
        });

        menuItemListPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showMenuItems();
                menuItemListPullToRefresh.setRefreshing(false);

            }
        });

        categoriesItemListPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showCategoriesItems();
                categoriesItemListPullToRefresh.setRefreshing(false);

            }
        });

        addonItemListPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showAddonItems();
                addonItemListPullToRefresh.setRefreshing(false);

            }
        });

        adminsItemListPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showAdminsItems();
                adminsItemListPullToRefresh.setRefreshing(false);

            }
        });

//        Button functions for add new
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminViewFoodItemDetailsActivity.class);
                intent.putExtra("itemID", "");
                intent.putExtra("itemMode", "New");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnAddNewAddon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnAddAddon;
                ImageButton addonBtnCloseForm;
                EditText name, price;
                addAddonDialog.show();

                btnAddAddon = (Button) addAddonDialog.findViewById(R.id.btnAddAddon);
                addonBtnCloseForm = (ImageButton) addAddonDialog.findViewById(R.id.btnCloseForm);
                name = (EditText) addAddonDialog.findViewById(R.id.name);
                price = (EditText) addAddonDialog.findViewById(R.id.price);

                btnAddAddon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nameValue = name.getText().toString();
                        String priceValue = price.getText().toString();

                        if (nameValue.equals("") || priceValue.equals("")) {
                            Toast.makeText(AdminDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                        } else {

                            try {
                                String URL = AddonApi.ADDON_API;

                                RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("name", nameValue);
                                jsonBody.put("price", priceValue);

                                final String requestBody = jsonBody.toString();

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            String status = jsonObject.getString("status");
                                            String msg = jsonObject.getString("msg");

                                            if (status.equals("success")) {
                                                name.setText("");
                                                price.setText("");

                                                showAddonItems();
                                                addAddonDialog.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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

                addonBtnCloseForm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addAddonDialog.dismiss();
                    }
                });

            }
        });

        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnAdd;
                ImageButton btnCloseForm;
                EditText name;
                addCategoryDialog.show();

                btnAdd = (Button) addCategoryDialog.findViewById(R.id.btnAdd);
                btnCloseForm = (ImageButton) addCategoryDialog.findViewById(R.id.btnCloseForm);
                name = (EditText) addCategoryDialog.findViewById(R.id.name);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nameValue = name.getText().toString();

                        if (nameValue.equals("")) {
                            Toast.makeText(AdminDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                        } else {

                            try {
                                String URL = CategoryApi.CATEGORY_API;

                                RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("category", nameValue);

                                final String requestBody = jsonBody.toString();

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            String status = jsonObject.getString("status");
                                            String msg = jsonObject.getString("msg");

                                            if (status.equals("success")) {
                                                name.setText("");

                                                showCategoriesItems();
                                                addCategoryDialog.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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
                        addCategoryDialog.dismiss();
                    }
                });

            }
        });

        btnAddNewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnAdd;
                ImageButton btnCloseForm;
                EditText name, email;
                addNewAdminDialog.show();

                btnAdd = (Button) addNewAdminDialog.findViewById(R.id.btnAdd);
                btnCloseForm = (ImageButton) addNewAdminDialog.findViewById(R.id.btnCloseForm);
                name = (EditText) addNewAdminDialog.findViewById(R.id.name);
                email = (EditText) addNewAdminDialog.findViewById(R.id.email);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nameValue = name.getText().toString();
                        String emailValue = email.getText().toString();

                        if (nameValue.equals("") || emailValue.equals("")) {
                            Toast.makeText(AdminDashboardActivity.this, "Fields empty!",Toast.LENGTH_SHORT).show();

                        } else if (!EmailValidator.isValidEmail(emailValue)) {
                            Toast.makeText(AdminDashboardActivity.this, "Email pattern invalid!", Toast.LENGTH_SHORT).show();

                        } else {

                            try {
                                String URL = AdminApi.ADMIN_API;

                                RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("name", nameValue);
                                jsonBody.put("email", emailValue);

                                final String requestBody = jsonBody.toString();

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            String status = jsonObject.getString("status");
                                            String msg = jsonObject.getString("msg");

                                            if (status.equals("success")) {
                                                name.setText("");
                                                email.setText("");

                                                showAdminsItems();
                                                addNewAdminDialog.dismiss();
                                            }

                                            Toast.makeText(AdminDashboardActivity.this, msg, Toast.LENGTH_SHORT).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(AdminDashboardActivity.this, "Some error occur" + error.toString(), Toast.LENGTH_SHORT).show();
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
                        addNewAdminDialog.dismiss();
                    }
                });

            }
        });

    }



    private void showOrders(String type)
    {
        orderArrayList.clear();
        orderItemsList.setAdapter(null);

        if (type.equals("Pending")) {
            txtOrderType.setText("Pending Orders");

        } else if (type.equals("Processing")) {
            txtOrderType.setText("Processing Orders");

        } else if (type.equals("Completed")) {
            txtOrderType.setText("Complete Orders");

        } else if (type.equals("Delivered")) {
            txtOrderType.setText("Delivered Orders");

        }

        OrderAdapter orderAdapter = new OrderAdapter(this, R.layout.order_item_row, orderArrayList);
        orderItemsList.setAdapter(orderAdapter);

        String URL = OrderApi.GET_ORDERS_WITH_FILTER + "/" + type;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showOrderDetails(String id)
    {
        orderDetailsArrayList.clear();
        orderDetailsList.setAdapter(null);

        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(this, R.layout.admin_order_details_item_row, orderDetailsArrayList);
        orderDetailsList.setAdapter(orderDetailsAdapter);

        String URL = OrderApi.GET_ORDER_DETAILS + "/" + id;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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
                                requesterName.setText(nameValue);
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
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showMenuItems()
    {
        String URL = CategoryApi.CATEGORY_API;

        categoryChipGroup.removeAllViews();

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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

                                Chip chip = new Chip(AdminDashboardActivity.this);
                                chip.setText(categoryName);
                                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(AdminDashboardActivity.this, R.color.white)));
                                chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(AdminDashboardActivity.this, R.color.black)));
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
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
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

        String URL = FoodApi.FOOD_API + "/" + category;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showCategoriesItems()
    {
        categoriesArrayList.clear();
        addonItemList.setAdapter(null);

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, R.layout.addon_item_row, categoriesArrayList);
        categoriesItemList.setAdapter(categoryAdapter);

        String URL = CategoryApi.GET_ALL_CATEGORY;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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

                                String categoryValue = jsonObject.getString("category");

                                categoriesArrayList.add(new CategoryItems(categoryValue));

                            }

                            categoryAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showAddonItems()
    {
        addonArrayList.clear();
        addonItemList.setAdapter(null);

        AddonAdapter addonAdapter = new AddonAdapter(this, R.layout.addon_item_row, addonArrayList);
        addonItemList.setAdapter(addonAdapter);

        String URL = AddonApi.ADDON_API;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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

                            }

                            addonAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);

    }

    private void showAdminsItems()
    {
        adminsArrayList.clear();
        adminsItemList.setAdapter(null);

        AdminAdapter adminAdapter = new AdminAdapter(this, R.layout.admin_item_row, adminsArrayList);
        adminsItemList.setAdapter(adminAdapter);

        String URL = AdminApi.ADMIN_API + "/" + User.LOGGED_USER_EMAIL;

        RequestQueue requestQueue = Volley.newRequestQueue(AdminDashboardActivity.this);
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

                                String emailValue = jsonObject.getString("email");
                                String nameValue = jsonObject.getString("name");

                                adminsArrayList.add(new AdminItems(emailValue, nameValue));

                            }

                            adminAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AdminDashboardActivity.this, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }

        );

        requestQueue.add(jsonArrayRequest);


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

class AddonAdapter extends ArrayAdapter<AddonItems> {

    private Context mContext;
    private int mResource;

    public AddonAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AddonItems> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        name.setText(getItem(position).getName());
        price.setText(getItem(position).getPrice().toString());

        return convertView;
    }

}

class CategoryAdapter extends ArrayAdapter<CategoryItems> {

    private Context mContext;
    private int mResource;

    public CategoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CategoryItems> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        name.setText(getItem(position).getName());

        return convertView;
    }

}

class AdminAdapter extends ArrayAdapter<AdminItems> {

    private Context mContext;
    private int mResource;

    public AdminAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AdminItems> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        name.setText(getItem(position).getName());

        return convertView;
    }

}