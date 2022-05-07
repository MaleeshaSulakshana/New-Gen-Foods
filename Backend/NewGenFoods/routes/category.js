const express = require("express");
const router = express.Router();
const connection = require("../db");

// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT category FROM category WHERE category in (SELECT DISTINCT(category) 
                            FROM foods WHERE status = 'Available')`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

router.get("/all", (req, res) => {

    let details = [];
    connection.query(`SELECT category FROM category`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For get by id
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT category FROM category WHERE category = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {

    const { category } = req.body;

    connection.query(`SELECT COUNT(category) as category_count FROM category WHERE category = '${category}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        category_count = details[0]['category_count']

        if (category_count > 0) {
            res.json({ status: "error", msg: "Category exist!" });
        } else {

            connection.query(`INSERT INTO category (category) VALUES 
                ('${category}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Category not created!" });
                } else {
                    res.json({ status: "success", msg: "Category created successfully!" });
                }

            });

        }

    });

});


// For delete
router.delete("/:category", (req, res) => {

    const { category } = req.params;

    connection.query(`DELETE FROM category WHERE category = '${category}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Category not deleted. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Category delete successful!" });
        }

    });

});

module.exports = router;