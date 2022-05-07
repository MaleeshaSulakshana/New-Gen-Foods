const express = require("express");
const router = express.Router();
const connection = require("../db");

// For insert to history recode
router.post("/login", (req, res) => {
    const { email, psw } = req.body;


    connection.query(`SELECT COUNT(email) as user_count FROM users WHERE email = '${email}' AND psw = '${psw}'`, (error, results, fields) => {
        if (error) {
            res.json({ status: "error", msg: "Some error occur.Try again!" });

        } else {

            details = results;
            user_count = details[0]['user_count']

            if (user_count < 1) {

                connection.query(`SELECT COUNT(email) as admin_count FROM admin WHERE email = '${email}' AND psw = '${psw}'`, (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Some error occur.Try again!" });

                    } else {

                        details = results;
                        admin_count = details[0]['admin_count']

                        if (admin_count < 1) {
                            res.json({ status: "error", msg: "Please check your email and password!" });

                        } else {
                            res.json({ status: "success", msg: "Admin login successfully!", user_type: "admin", email: email });
                        }

                    }

                });

            } else {
                res.json({ status: "success", msg: "User login successfully!", user_type: "user", email: email });
            }

        }

    });

});


module.exports = router;