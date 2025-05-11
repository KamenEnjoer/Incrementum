require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');

const Card = require('./models/Card');
const GameState = require('./models/GameState');

const app = express();
app.use(express.json());

const http = require('http').createServer(app);
const { Server } = require('socket.io');
const io = new Server(http, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

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
    const statesWithPlayers = states.map(state => {
      const gameId = state._id.toString();
      const connectedPlayers = gameSessions.has(gameId) ? gameSessions.get(gameId).size : 0;
      return {
        ...state.toObject(),
        connectedPlayers
      };
    });
    res.json(statesWithPlayers);
  } catch (err) {
    console.error('ALL GAMESTATES GETTING ERROR', err);
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
    io.to(updatedGameState._id.toString()).emit('gameStateUpdated', updatedGameState);
    res.json(updatedGameState);
  } catch (err) {
    res.status(400).json({ error: 'GAMESTATE UPDATING ERROR', details: err.message });
  }
});

// === Socket.IO ===

const gameSessions = new Map();

io.on('connection', (socket) => {
  console.log('A user connected: ' + socket.id);

  socket.on('join_game', async (gameId) => {
    socket.join(gameId);
    console.log(`User ${socket.id} joined game ${gameId}`);

    if (!gameSessions.has(gameId)) {
      gameSessions.set(gameId, new Set());
    }
    gameSessions.get(gameId).add(socket.id);

    try {
      const gameState = await GameState.findById(gameId);
      if (gameState) {
        socket.emit('gameStateUpdated', gameState);
      }
    } catch (err) {
      console.error('Error fetching GameState on join:', err);
    }
  });

  socket.on('update_game_state', async (updatedGameState) => {
    const gameId = updatedGameState._id;
    if (!gameId) {
      console.error('update_game_state: No _id in GameState');
      return;
    }
    try {
      const newGameState = await GameState.findByIdAndUpdate(gameId, updatedGameState, { new: true, upsert: true });
      console.log(`GameState ${gameId} updated by ${socket.id}`);
      io.to(gameId).emit('gameStateUpdated', newGameState);
    } catch (err) {
      console.error('Error updating GameState:', err);
    }
  });


  function cleanupGameIfEmpty(gameId) {
    const socketsSet = gameSessions.get(gameId);
    if (socketsSet && socketsSet.size === 0) {
      console.log(`No players left in game ${gameId}. Deleting game state.`);

      GameState.deleteOne({ _id: gameId })
        .then(() => console.log(`GameState ${gameId} deleted from DB`))
        .catch(err => console.error(`Error deleting GameState ${gameId}:`, err));

      gameSessions.delete(gameId);
    }
  }

  socket.on('disconnect', () => {
    console.log('A user disconnected: ' + socket.id);

    for (const [gameId, socketsSet] of gameSessions.entries()) {
      if (socketsSet.has(socket.id)) {
        socketsSet.delete(socket.id);
        console.log(`User ${socket.id} left game ${gameId}`);
        cleanupGameIfEmpty(gameId);
      }
    }
  });

  socket.on('leave_game', (gameId) => {
    if (!gameId) return;
    if (gameSessions.has(gameId)) {
      const socketsSet = gameSessions.get(gameId);
      socketsSet.delete(socket.id);
      console.log(`User ${socket.id} left game ${gameId} (via leave_game)`);
      cleanupGameIfEmpty(gameId);
    }
  });
});

http.listen(3000, '0.0.0.0', () => {
  console.log('Server running with Socket.IO on http://localhost:3000');
});
