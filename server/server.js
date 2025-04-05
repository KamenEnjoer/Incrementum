require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const Card = require('./models/Card');

const app = express();
app.use(express.json());

// Подключаемся к Atlas
mongoose.connect(process.env.MONGODB_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => console.log('Успешное подключение к MongoDB Atlas'))
  .catch(err => console.error('Ошибка подключения:', err));

// Получить все карточки
app.get('/cards', async (req, res) => {
  try {
    const cards = await Card.find();
    res.json(cards);
  } catch (err) {
    res.status(500).json({ error: 'Ошибка при получении данных' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Сервер работает на http://localhost:${PORT}`);
});
