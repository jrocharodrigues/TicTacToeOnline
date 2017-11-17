package com.impecabel.tictactoeonline.data.model

/**
 * Created by x00881 on 15/11/2017.
 */
class Game(var gameId: String?, var firstPlayer: Boolean = false, var symbol: String, var opponentId: String, var opponentUsername: String?) {

    var gameBoard: ArrayList<ArrayList<Int>> = arrayListOf(arrayListOf(0, 0, 0), arrayListOf(0, 0, 0), arrayListOf(0, 0, 0))

    var gameLog: String = ""

    var gameStatus: GameStatus = GameStatus.WAITING

    constructor() : this(null, false, "", "", "")

    enum class GameStatus {
        PLAYING, WAITING, WINNER, LOOSER, TIE
    }

}


