const express = require("express");
const router = express.Router();
const connection = require("../db");

// For get all
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT name, comment FROM comment INNER JOIN users ON users.email = comment.email WHERE food_id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For insert
router.post("", (req, res) => {

    const { food_id, email, comment } = req.body;

    connection.query(`INSERT INTO comment (food_id, email, comment) VALUES 
                ('${food_id}', '${email}', '${comment}')`, (error, results, fields) => {
        if (error) {
            res.json({ status: "error", msg: "Comment not added!" });
        } else {
            res.json({ status: "success", msg: "Comment added successfully!" });
        }

    });

});


module.exports = router;