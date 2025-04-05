const mongoose = require('mongoose');

const cardSchema = new mongoose.Schema({
    name: String,
    description: String,
    type: String,
    level: Number,
    duration: Number,
    square: Number,
}, { collection: "incrementum_cards" });

module.exports = mongoose.model('Card', cardSchema);
