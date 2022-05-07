require("dotenv").config();
const cors = require("cors");
const express = require("express");

const connection = require("./db");
connection.connect();
const app = express();

const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Import routes
const auth_router = require("./routes/auth");
const users_router = require("./routes/users");
const admin_router = require("./routes/admin");
const foods_router = require("./routes/foods");
const category_router = require("./routes/category");
const addon_router = require("./routes/addon");
const comment_router = require("./routes/comment");
const order_router = require("./routes/order");

// Use routes
app.use("/api/v1/auth", auth_router);
app.use("/api/v1/users", users_router);
app.use("/api/v1/admin", admin_router);
app.use("/api/v1/foods", foods_router);
app.use("/api/v1/category", category_router);
app.use("/api/v1/addon", addon_router);
app.use("/api/v1/comment", comment_router);
app.use("/api/v1/order", order_router);

// Server run
app.listen(PORT, () => {
    console.log("server is running");
});