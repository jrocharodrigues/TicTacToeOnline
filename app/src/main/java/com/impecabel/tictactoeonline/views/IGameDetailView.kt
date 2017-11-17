package com.impecabel.tictactoeonline.views

import com.impecabel.tictactoeonline.data.model.Game

interface IGameDetailView {

    fun showGame(game: Game)
    fun updateTile(position: List<Int>, marker: String)
}