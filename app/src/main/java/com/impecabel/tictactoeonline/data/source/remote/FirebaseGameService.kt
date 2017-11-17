package com.impecabel.tictactoeonline.data.source.remote

import com.impecabel.tictactoeonline.data.model.Game
import com.impecabel.tictactoeonline.data.model.User
import android.R.attr.key
import com.google.firebase.database.*


/**
 * Created by x00881 on 15/11/2017.
 */
class FirebaseGameService(val user: User){

    private val GAMES_KEY = "games"

    val databaseRef = FirebaseDatabase.getInstance().reference

    fun getGames() : DatabaseReference {
        return databaseRef.child(GAMES_KEY).child(user.uid)
    }

    fun getGame(gameId: String) : Query {
        return getGame(user.uid, gameId)
    }

    fun getGame(userId: String, gameId: String?) : Query {
        return databaseRef.child(GAMES_KEY).child(userId).orderByKey().equalTo(gameId)
    }

    fun addGame(game: Game) {
        val ref = databaseRef.child(GAMES_KEY).child(user.uid).push()
        game.gameStatus = Game.GameStatus.PLAYING
        ref.setValue(game)
        databaseRef.child(GAMES_KEY).child(game.opponentId).child(ref.key).setValue(Game(null, false, "O", user.uid, null))
    }

    fun updateGame(game: Game) {
        databaseRef.child(GAMES_KEY).child(user.uid).child(game.gameId).setValue(game)
        getGame(game.opponentId, game.gameId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.exists()) {
                            val opponentGame = dataSnapshot.child(game.gameId).getValue<Game>(Game::class.java)

                            opponentGame?.let {
                                opponentGame.gameStatus = Game.GameStatus.PLAYING
                                opponentGame.gameLog = game.gameLog
                                opponentGame.gameBoard = game.gameBoard
                                databaseRef.child(GAMES_KEY).child(game.opponentId).child(game.gameId).setValue(opponentGame)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                }
        )



    }
}