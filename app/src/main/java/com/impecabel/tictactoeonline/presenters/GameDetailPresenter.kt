package com.impecabel.tictactoeonline.presenters

import android.util.Log
import com.google.firebase.database.*
import com.impecabel.tictactoeonline.base.BasePresenter
import com.impecabel.tictactoeonline.data.model.Game
import com.impecabel.tictactoeonline.data.model.User
import com.impecabel.tictactoeonline.data.source.remote.FirebaseGameService
import com.impecabel.tictactoeonline.data.source.remote.UserService
import com.impecabel.tictactoeonline.views.IGameDetailView
import com.impecabel.tictactoeonline.views.IGameListView
import java.text.FieldPosition

class GameDetailPresenter(val gameId: String, val user: User, val iGameDetailView: IGameDetailView, val userService: UserService, val gameService: FirebaseGameService) : BasePresenter {
    private val TAG = "GameDetailPresenter"
    val databaseRef = FirebaseDatabase.getInstance().reference
    lateinit var gameRef : ChildEventListener

    var game: Game? = null

    override fun subscribe() {
        processGame()
    }

    override fun unsubscribe() {
        databaseRef.removeEventListener(gameRef)
    }

    fun processGame() {


        gameRef = gameService.getGame(gameId).addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        Log.d(TAG, "game :" + dataSnapshot.value!!)
                        game = dataSnapshot.getValue<Game>(Game::class.java)
                        game?.gameId = dataSnapshot.key
                        game?.let{ processGameOpponentUsername(it)}
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                        game = dataSnapshot.getValue<Game>(Game::class.java)
                        game?.gameId = dataSnapshot.key
                        Log.d(TAG, "onChildChanged :" + dataSnapshot.value!!)
                        game?.let{ processGameOpponentUsername(it)}
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        // TODO : remove
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
                        // TODO : moved
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // TODO : cancel
                    }


                }
        )
    }

    fun processGameOpponentUsername(game: Game) {
        userService.getUser(game.opponentId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue<User>(User::class.java)
                            game.opponentUsername = user?.username
                        }
                        iGameDetailView.showGame(game)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        iGameDetailView.showGame(game)
                    }
                }
        )
    }

    fun processClick(position: List<Int>) {

        game?.gameBoard?.get(position[0])?.set(position[1], game.let{ if (it!!.firstPlayer) 1 else 2})
        val move: String = (if (game!!.firstPlayer) "1" else "2") + game!!.symbol + position[0] + position[1] + ";"
        game?.gameLog = game?.gameLog.plus(move)
        iGameDetailView.updateTile(position, game!!.symbol)
        game?.let{
            it.gameStatus = Game.GameStatus.WAITING
            gameService.updateGame(it)
        }
    }

   /* fun addGame(oponent: String) {
        userService.getUserByUsername(oponent).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            iGameListView.showNotExistFriend(oponent)
                        } else {
                            val oponentUser = dataSnapshot.getValue<User>(User::class.java)
                            oponentUser?.let{gameService.addGame(Game(null, true, oponentUser?.uid, null))}
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        iGameListView.showNotExistFriend(oponent)
                    }
                }
        )
    }
*/
}