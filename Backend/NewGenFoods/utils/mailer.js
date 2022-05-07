const nodemailer = require('nodemailer');
const fs = require('fs');

var senderEmail = "newgenfoodrestaurant@gmail.com";
var senderPsw = "newgen1234";

var transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: senderEmail,
        pass: senderPsw
    }
});


var sendEmail = function (email, subject, html) {

    var mailOptions = {
        from: senderEmail,
        to: email,
        subject: subject,
        html: html
    };

    transporter.sendMail(mailOptions, function (error, info) {
        if (error) {
            console.log(error);
        } else {
            console.log('Email sent');
        }
    });

}

var sendEmailWithAttachment = function (email, subject, html, filename, filePath) {

    var mailOptions = {
        from: senderEmail,
        to: email,
        subject: subject,
        html: html,
        attachments: [{
            filename: filename,
            path: filePath
        }]
    };

    transporter.sendMail(mailOptions, function (error, info) {
        if (error) {
            console.log(error.message);
        } else {
            console.log('Email sent');
            fs.unlinkSync(filePath);
        }
    });

}

module.exports = {
    sendEmail, sendEmailWithAttachment
}