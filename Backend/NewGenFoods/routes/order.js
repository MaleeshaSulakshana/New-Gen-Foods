const express = require("express");
const fs = require('fs');
const pdf = require('pdf-creator-node');
const path = require('path');
const options = require('../helpers/options');
const connection = require("../db");
const utils = require("../utils/utils");
const sendMailer = require("../utils/mailer");

const router = express.Router();

// For get all
router.get("/offer/:email", (req, res) => {

    const { email } = req.params;

    let details = [];
    connection.query(`SELECT orders.id as id, orders.date as date FROM orders 
                    INNER JOIN order_details ON orders.id = order_details.id INNER JOIN foods 
                    ON foods.id = order_details.food_id WHERE email = '${email}' AND 
                    orders.id > IFNULL((
                SELECT orders.id FROM orders WHERE email = '${email}'
                AND discount = "yes" Order BY orders.id DESC LIMIT 1),0) GROUP BY orders.order_id ORDER BY orders.order_id DESC LIMIT 3`
        , (error, results, fields) => {
            if (error) throw error;
            details = results;

            res.json(details);
        });

});


router.get("/status/filer/:status", (req, res) => {

    const { status } = req.params;

    let details = [];
    var datetime = new Date().toLocaleDateString('en-CA');

    let query = ``;

    if (status != "Delivered") {

        query = `SELECT orders.id as id, orders.status as status
            FROM orders INNER JOIN order_details ON orders.id = order_details.id 
            INNER JOIN foods ON foods.id = order_details.food_id 
            WHERE orders.status = '${status}' GROUP BY orders.order_id ORDER BY orders.order_id ASC`

    } else {

        query = `SELECT orders.id as id, orders.status as status
            FROM orders INNER JOIN order_details ON orders.id = order_details.id 
            INNER JOIN foods ON foods.id = order_details.food_id WHERE orders.status = 'Delivered' AND 
            date = '${datetime}' GROUP BY orders.order_id ORDER BY orders.order_id ASC`

    }


    connection.query(query, (error, results, fields) => {
        if (error) throw error;
        details = results.reverse();

        res.json(details);
    });

});


router.get("/details/:id", (req, res) => {

    const { id } = req.params;
    let details = [];

    connection.query(`SELECT orders.id as id, orders.email as email, users.name, date, total, orders.status as status , order_details.food_id, 
                        qty, addon, notice , foods.title, foods.category FROM orders INNER JOIN order_details ON orders.id = order_details.id 
                        INNER JOIN foods ON foods.id = order_details.food_id
                        INNER JOIN users ON users.email = orders.email WHERE orders.id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results.reverse();

        res.json(details);
    });

});


router.get("/user/:email", (req, res) => {

    const { email } = req.params;

    let details = [];
    connection.query(`SELECT orders.id as id, orders.status as status
                    FROM  orders INNER JOIN order_details ON orders.id = order_details.id 
                    INNER JOIN foods ON foods.id = order_details.food_id WHERE email = '${email}' and
                    orders.status in ('Pending', 'Processing', 'Completed') GROUP BY orders.order_id ORDER BY orders.order_id ASC`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For insert
router.post("", (req, res) => {

    const { email, date, total, status_type, details, discount } = req.body;

    if (details.length > 0) {

        connection.query(`SELECT MAX(id) + 1 as id FROM orders`, (error, results, fields) => {
            if (error) {

                res.json({ status: "error", msg: "Your order not created!" });


            } else {


                dbDetails = results;
                id = 1;
                if (dbDetails[0]['id'] != null) {
                    id = parseInt(dbDetails[0]['id'])
                }

                connection.query(`INSERT INTO orders (id, email, date, total, status, discount) 
                        VALUES (${id}, '${email}', '${date}', '${total}', '${status_type}', '${discount}')`, (error, results, fields) => {
                    if (error) {

                        res.json({ status: "error", msg: "Your order not created!" });
                        console.log(error.message)

                    } else {


                        for (const val of details) {

                            connection.query(`INSERT INTO order_details (id, food_id, qty, addon, notice) VALUES  
                                    ('${id}', '${val['food_id']}', '${val['qty']}', '${val['addon']}', '${val['notice'].replace("'", "''")}')`, (error, results, fields) => {
                                if (error) {

                                    console.log("Error " + error.message);

                                }

                            });

                        }

                        connection.query(`SELECT orders.id as id, orders.email as email, users.name as name, date, total, orders.status as status , order_details.food_id, 
                                    qty, order_details.addon as addon, notice , foods.title as title, foods.category as category, foods.price as price,
                                    ((foods.price * qty) + (IFNULL((
                                        SELECT addon.price FROM addon WHERE addon.name = order_details.addon),0)) * qty)
                                        as tprice, orders.discount as discount FROM orders 
                                    INNER JOIN order_details ON orders.id = order_details.id 
                                    INNER JOIN foods ON foods.id = order_details.food_id
                                    INNER JOIN users ON users.email = orders.email WHERE orders.id = '${id}'`, (error, results, fields) => {
                            if (error) throw error;


                            let total_amount = 0.00;
                            if (results.length > 0) {

                                let array = [];
                                let bill_data = {};
                                let name = results[0].name;

                                results.forEach(d => {

                                    total_amount += d.tprice;

                                    const dataset = {
                                        qty: d.qty,
                                        addon: d.addon,
                                        notice: d.notice,
                                        title: d.title,
                                        category: d.category,
                                        price: d.price,
                                        tprice: d.tprice,
                                    }
                                    array.push(dataset);
                                });

                                bill_data = {
                                    id: results[0].id,
                                    email: results[0].email,
                                    name: results[0].name,
                                    date: results[0].date,
                                    total: results[0].total,
                                    discount: results[0].discount,
                                    total_amount: total_amount
                                }

                                const html = fs.readFileSync(path.join(__dirname, '../views/template.html'), 'utf-8');
                                const filename = 'Order_Receipt_' + id + '.pdf';
                                const filePath = "./docs/" + filename;

                                const document = {
                                    html: html,
                                    data: {
                                        bill_data: bill_data,
                                        details: array
                                    },
                                    path: './docs/' + filename
                                }
                                pdf.create(document, options)
                                    .then(() => {
                                        res.download(filePath);

                                        let msgBody = `Hi ${name},<br><br>
                                                Your order placed successfully. 
                                                Please find order receipt attached in this mail.
                                                <br>
                                                Order No - <b>${id}</b>.
                                                <br>
                                                Thank you. Come again.
                                    `
                                        sendMailer.sendEmailWithAttachment(email, "New Gen Foods Restaurant Order Receipt", msgBody, filename, filePath);

                                    }).catch(error => {
                                        console.log(error);
                                    });

                            }

                        });

                        console.log("success")
                        res.json({ status: "success", msg: "Your order created successfully!." });
                    }

                });


            }

        });

    } else {
        res.json({ status: "error", msg: "Your order has 0 item!" });
    }

});



// For update status
router.put("/status/:id", (req, res) => {

    const { status } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(id) as order_id FROM orders WHERE id = ${id}`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        order_id = details[0]['order_id']

        if (order_id < 1) {
            res.json({ status: "error", msg: "Order not exist!" });

        } else {

            connection.query(`UPDATE orders SET status = '${status}' WHERE id = ${id}`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Order status not updated!" });
                    } else {
                        res.json({ status: "success", msg: "Order status updated!" });
                    }

                });

        }

    });

});

module.exports = router;