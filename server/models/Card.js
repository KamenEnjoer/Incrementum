const mongoose = require('mongoose');

const conditionSchema = new mongoose.Schema({
  condition: String,
  power: Number
}, { _id: false });

const cardSchema = new mongoose.Schema({
  name: String,
  description: String,
  type: String,
  level: Number,
  duration: Number,
  square: Number,
  favorableConditions: [conditionSchema],
  unfavorableConditions: [conditionSchema]
});

module.exports = mongoose.model('Card', cardSchema, 'incrementum_cards');
