package com.impecabel.tictactoeonline.views

import com.impecabel.tictactoeonline.data.model.Game

interface IGameListView {

    fun showGameList()
    fun showAddedGame(game: Game)
    fun showChangedGame(game: Game)
    fun showNotExistFriend(username: String)
}