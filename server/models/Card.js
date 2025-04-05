const mongoose = require('mongoose');

const cardSchema = new mongoose.Schema({
  name: String,
  type: String,
  color: String,
  // Добавь здесь другие поля, если они есть в коллекции
}, { collection: 'incrementum_cards' }); // явно указываем коллекцию

module.exports = mongoose.model('Card', cardSchema);
