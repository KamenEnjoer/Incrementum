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
}).then(() => console.log('CONNECTED TO MongoDB Atlas.'))
  .catch(err => console.error('CONNECTION ERROR:', err));

app.get('/cards', async (req, res) => {
  try {
    const cards = await Card.find();
    res.json(cards);
  } catch (err) {
    res.status(500).json({ error: 'CARDS GETTING ERROR.' });
  }
});

app.get('/gamestates', async (req, res) => {
  try {
    const states = await GameState.find();
    res.json(states);
  } catch (err) {
    console.error('Ошибка при получении всех GameState:', err);
    res.status(500).json({ error: 'GAMESTATES WAS NOT FOUND WHEN ATTEMPTING TO GET.' });
  }
});

app.get('/gamestates/:id', async (req, res) => {
  try {
    const gameStateId = req.params.id;
    const state = await GameState.findById(gameStateId);

    if (!state) {
      return res.status(404).json({ error: 'GAMESTATE BY ID WAS NOT FOUND WHEN ATTEMPTING TO GET.' });
    }

    res.json(state);
  } catch (err) {
    res.status(500).json({ error: 'GAMESTATE GETTING ERROR.' });
  }
});

app.post('/gamestates', async (req, res) => {
  try {
    const gameState = new GameState(req.body);
    await gameState.save();
    res.status(201).json(gameState);
  } catch (err) {
    res.status(400).json({ error: 'NEW GAMESTATE INSERTING ERROR', details: err.message });
  }
});

app.put('/gamestates/:id', async (req, res) => {
  try {
    const updatedGameState = await GameState.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!updatedGameState) {
      return res.status(404).json({ error: 'GAMESTATE BY ID WAS NOT FOUND WHEN ATTEMPTING TO UPDATE.' });
    }
    res.json(updatedGameState);
  } catch (err) {
    res.status(400).json({ error: 'GAMESTATE UPDATING ERROR', details: err.message });
  }
});

app.listen(3000, '0.0.0.0', () => {
  console.log('Server running on http://localhost:3000');
});
