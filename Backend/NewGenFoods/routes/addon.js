const express = require("express");
const router = express.Router();
const connection = require("../db");

// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT name, price FROM addon`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For get by id
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT name, price FROM addon WHERE name = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {

    const { name, price } = req.body;

    connection.query(`SELECT COUNT(name) as name_count FROM addon WHERE name = '${name}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        name_count = details[0]['name_count']

        if (name_count > 0) {
            res.json({ status: "error", msg: "Addon exist!" });
        } else {

            connection.query(`INSERT INTO addon (name, price) VALUES 
                ('${name}', '${price}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Addon not created!" });
                } else {
                    res.json({ status: "success", msg: "Addon created successfully!" });
                }

            });

        }

    });

});


// For delete
router.delete("/:name", (req, res) => {

    const { name } = req.params;

    connection.query(`DELETE FROM addon WHERE name = '${name}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Addon not removed. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Addon removed successful!" });
        }

    });

});

module.exports = router;