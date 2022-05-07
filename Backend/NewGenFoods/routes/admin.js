const express = require("express");
const router = express.Router();

const connection = require("../db");
const utils = require("../utils/utils");
const sendMailer = require("../utils/mailer");


// For get all
router.get("/:email", (req, res) => {

    const { email } = req.params;

    let details = [];
    connection.query(`SELECT email, name FROM admin WHERE email != '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For get by email
router.get("/profile/:email", (req, res) => {

    const { email } = req.params;

    let details = [];
    connection.query(`SELECT email, name FROM admin WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { email, name } = req.body;

    const psw = utils.generatePsw();

    connection.query(`SELECT COUNT(email) as admin_count FROM admin WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        admin_count = details[0]['admin_count']

        if (admin_count > 0) {
            res.json({ status: "error", msg: "Admin email exist!" });
        } else {

            connection.query(`INSERT INTO admin (email, psw, name) VALUES 
                            ('${email}', '${psw}', '${name}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Admin not created!" });
                } else {

                    let msgBody = `Hi ${name},<br><br>
                                    Email - ${email}<br>Password - ${psw}<br>
                                    This is your <b>New Gen Foods Restaurant</b> Admin credentials. 
                                    You can Use this credential to sign in with <b>New Gen Foods Restaurant</b> app.
                                    `
                    sendMailer.sendEmail(email, "New Gen Foods Restaurant Admin Credentials", msgBody);
                    res.json({ status: "success", msg: "Admin created successfully!" });
                }

            });

        }

    });

});

// For update
router.put("/:email", (req, res) => {

    const { name } = req.body;
    const { email } = req.params;

    connection.query(`SELECT COUNT(email) as admin_count FROM admin WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        admin_count = details[0]['admin_count']

        if (admin_count < 1) {
            res.json({ status: "error", msg: "Admin not exist!" });
        } else {

            connection.query(`UPDATE admin SET name = '${name}' WHERE email = '${email}'`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Admin profile not updated!" });
                } else {
                    res.json({ status: "success", msg: "Admin profile updated!" });
                }

            });

        }

    });

});

// For update psw
router.put("/psw_change/:email", (req, res) => {

    const { psw } = req.body;
    const { email } = req.params;

    connection.query(`SELECT COUNT(email) as admin_count FROM admin WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        admin_count = details[0]['admin_count']

        if (admin_count < 1) {
            res.json({ status: "error", msg: "Admin not exist!" });
        } else {

            connection.query(`UPDATE admin SET psw = '${psw}' WHERE email = '${email}'`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Admin password not updated!" });
                } else {
                    res.json({ status: "success", msg: "Admin password updated!" });
                }

            });

        }

    });

});

// For delete
router.delete("/:email", (req, res) => {

    const { email } = req.params;

    connection.query(`DELETE FROM admin WHERE email = '${email}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Admin not deleted. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Admin delete successful!" });
        }

    });

});

module.exports = router;