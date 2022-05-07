var generatePsw = function () {

    var randomKey = Math.random().toString(36).slice(-8);
    return randomKey;
}

module.exports = {
    generatePsw
}