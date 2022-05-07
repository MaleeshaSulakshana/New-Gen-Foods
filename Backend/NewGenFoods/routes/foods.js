const express = require("express");
const router = express.Router();
const connection = require("../db");

// For get all
router.get("/:category", (req, res) => {

    const { category } = req.params;

    let details = [];
    connection.query(`SELECT id, title, price, category, ingredients, image, status FROM foods WHERE category = '${category}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

router.get("/available/:category", (req, res) => {

    const { category } = req.params;

    let details = [];
    connection.query(`SELECT id, title, price, category, ingredients, image, status FROM foods WHERE category = '${category}' and status = 'Available'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

router.get("/not_available/:category", (req, res) => {

    const { category } = req.params;

    let details = [];
    connection.query(`SELECT id, title, price, category, ingredients, image, status FROM foods 
                WHERE category = '${category}' and status = 'NotAvailable'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

router.get("/search/:value", (req, res) => {

    const { value } = req.params;

    let details = [];
    connection.query(`SELECT id, title, price, category, ingredients, image, status FROM foods 
                    WHERE title LIKE '%${value}%' or category LIKE '%${value}%' or ingredients LIKE '%${value}%'
                    AND status = 'Available'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For get by id
router.get("/details/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT id, title, price, category, ingredients, image, status FROM foods WHERE id = ${id}`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { title, price, category, ingredients, image, status } = req.body;

    connection.query(`SELECT COUNT(title) as food_count FROM foods WHERE title = '${title}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        food_count = details[0]['food_count']

        if (food_count > 0) {
            res.json({ status: "error", msg: "Food title exist!" });
        } else {

            connection.query(`INSERT INTO foods (title, price, category, ingredients, image, status) VALUES 
                ('${title}', '${price}', '${category}', '${ingredients}', '${image}', '${status}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Food item added not successfully!" });
                } else {
                    res.json({ status: "success", msg: "Food item added successfully!" });
                }

            });

        }

    });

});

// For update
router.put("/:id", (req, res) => {

    const { title, price, category, ingredients, image, status } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(id) as food_count FROM foods WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        food_count = details[0]['food_count']

        if (food_count < 1) {
            res.json({ status: "error", msg: "Food not exist!" });
        } else {

            connection.query(`UPDATE foods SET title = '${title}', 
                                price = '${price}', category = '${category}', ingredients = '${ingredients}', image = '${image}', 
                                status = '${status}' WHERE id = '${id}'`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Food item not updated!" });
                } else {
                    res.json({ status: "success", msg: "Food item updated!" });
                }

            });

        }

    });

});


// For update status
router.put("/status/:id", (req, res) => {

    const { status } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(id) as food_count FROM foods WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        food_count = details[0]['food_count']

        if (food_count < 1) {
            res.json({ status: "error", msg: "Food not exist!" });
        } else {

            connection.query(`UPDATE foods SET status = '${status}' WHERE id = ${id}`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Food status not updated!" });
                } else {
                    res.json({ status: "success", msg: "Food status updated!" });
                }

            });

        }

    });

});


// For delete
router.delete("/:id", (req, res) => {

    const { id } = req.params;

    connection.query(`DELETE FROM foods WHERE id = ${id}`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Food item removed not successfully. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Food item removed!" });
        }

    });

});

module.exports = router;