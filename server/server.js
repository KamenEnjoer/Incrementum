require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const Card = require('./models/Card');
const GameState = require('./models/GameState');

const app = express();
app.use(express.json());

mongoose.connect(process.env.MONGODB_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => console.log('Успешное подключение к MongoDB Atlas'))
  .catch(err => console.error('Ошибка подключения:', err));

app.get('/cards', async (req, res) => {
  try {
    const cards = await Card.find();
    res.json(cards);
  } catch (err) {
    res.status(500).json({ error: 'Ошибка при получении данных' });
  }
});

app.get('/gamestates', async (req, res) => {
  try {
    const states = await GameState.find();
    res.json(states);
  } catch (err) {
    res.status(500).json({ error: 'Ошибка при получении состояний игры' });
  }
});

app.post('/gamestates', async (req, res) => {
  try {
    const gameState = new GameState(req.body);
    await gameState.save();
    res.status(201).json(gameState);
  } catch (err) {
    res.status(400).json({ error: 'Ошибка при создании новой партии', details: err.message });
  }
});

app.put('/gamestates/:id', async (req, res) => {
  try {
    const updatedGameState = await GameState.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!updatedGameState) {
      return res.status(404).json({ error: 'Партия не найдена' });
    }
    res.json(updatedGameState);
  } catch (err) {
    res.status(400).json({ error: 'Ошибка при обновлении партии', details: err.message });
  }
});

app.listen(3000, '0.0.0.0', () => {
  console.log('Server running on http://localhost:3000');
});
