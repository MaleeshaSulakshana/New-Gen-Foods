const express = require("express");
const router = express.Router();

const connection = require("../db");
const utils = require("../utils/utils");
const sendMailer = require("../utils/mailer");


// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT email, name FROM users`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by email
router.get("/:email", (req, res) => {

    const { email } = req.params;

    let details = [];
    connection.query(`SELECT email, name, dob FROM users WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { email, name, dob } = req.body;

    const psw = utils.generatePsw();

    connection.query(`SELECT COUNT(email) as user_count FROM users WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        user_count = details[0]['user_count']

        if (user_count > 0) {
            res.json({ status: "error", msg: "User email exist!" });
        } else {

            connection.query(`INSERT INTO users (email, name, dob, psw) VALUES 
                        ('${email}', '${name}', '${dob}', '${psw}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "User not created!" });
                } else {

                    let msgBody = `Hi ${name},<br><br>
                                    Email - ${email}<br>Password - ${psw}<br>
                                    This is your <b>New Gen Foods Restaurant</b> credentials. 
                                    You can Use this credential to sign in with <b>New Gen Foods Restaurant</b> app.
                                    `
                    sendMailer.sendEmail(email, "New Gen Foods Restaurant Credentials", msgBody);
                    res.json({ status: "success", msg: "User created successfully!. Please sign in." });
                }

            });

        }

    });

});

// For update
router.put("/:email", (req, res) => {

    const { name, dob } = req.body;
    const { email } = req.params;

    connection.query(`SELECT COUNT(email) as user_count FROM users WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        user_count = details[0]['user_count']

        if (user_count < 1) {
            res.json({ status: "error", msg: "Profile not exist!" });
        } else {

            connection.query(`UPDATE users SET name = '${name}', dob = '${dob}' WHERE email = '${email}'`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Profile not updated!" });
                    } else {
                        res.json({ status: "success", msg: "Profile recode updated!" });
                    }

                });

        }

    });

});

// For update psw
router.put("/psw_change/:email", (req, res) => {

    const { psw } = req.body;
    const { email } = req.params;

    connection.query(`SELECT COUNT(email) as user_count FROM users 
    WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        user_count = details[0]['user_count']

        if (user_count < 1) {
            res.json({ status: "error", msg: "User not exist!" });
        } else {

            connection.query(`UPDATE users SET psw = '${psw}' WHERE email = '${email}'`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "User password not updated!" });
                    } else {
                        res.json({ status: "success", msg: "User password updated!" });
                    }

                });

        }

    });

});

// For delete
router.delete("/:email", (req, res) => {

    const { email } = req.params;

    connection.query(`DELETE FROM users WHERE email = '${email}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "User not deleted. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "User delete successful!" });
        }

    });

});

module.exports = router;