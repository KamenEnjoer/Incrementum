const mongoose = require('mongoose');

const cellDataSchema = new mongoose.Schema({
  playerName: { type: String, default: "" },
  plantCardId: { type: String, default: "" },
  plantLevel: { type: Number, default: 0 },
  plantProgress: { type: Number, default: 0 },
  weatherCardId: { type: String, default: "" },
  weatherDuration: { type: Number, default: 0 }
}, { _id: false });

const playerSchema = new mongoose.Schema({
  name: String,
  points: Number,
  hand: [String]
}, { _id: false });


const gameStateSchema = new mongoose.Schema({
  board: {
    row1: {
      column1: cellDataSchema, column2: cellDataSchema, column3: cellDataSchema,
      column4: cellDataSchema, column5: cellDataSchema, column6: cellDataSchema
    },
    row2: {
      column1: cellDataSchema, column2: cellDataSchema, column3: cellDataSchema,
      column4: cellDataSchema, column5: cellDataSchema, column6: cellDataSchema
    },
    row3: {
      column1: cellDataSchema, column2: cellDataSchema, column3: cellDataSchema,
      column4: cellDataSchema, column5: cellDataSchema, column6: cellDataSchema
    },
    row4: {
      column1: cellDataSchema, column2: cellDataSchema, column3: cellDataSchema,
      column4: cellDataSchema, column5: cellDataSchema, column6: cellDataSchema
    },
    row5: {
      column1: cellDataSchema, column2: cellDataSchema, column3: cellDataSchema,
      column4: cellDataSchema, column5: cellDataSchema, column6: cellDataSchema
    },
    row6: {
      column1: cellDataSchema, column2: cellDataSchema, column3: cellDataSchema,
      column4: cellDataSchema, column5: cellDataSchema, column6: cellDataSchema
    }
  },
  players: [playerSchema],
  currentTurn: String
});

module.exports = mongoose.model('GameState', gameStateSchema, 'incrementum_board');
