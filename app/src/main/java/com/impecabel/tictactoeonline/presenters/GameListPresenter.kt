package com.impecabel.tictactoeonline.presenters

import android.util.Log
import com.google.firebase.database.*
import com.impecabel.tictactoeonline.base.BasePresenter
import com.impecabel.tictactoeonline.data.model.Game
import com.impecabel.tictactoeonline.data.model.User
import com.impecabel.tictactoeonline.data.source.remote.FirebaseGameService
import com.impecabel.tictactoeonline.data.source.remote.UserService
import com.impecabel.tictactoeonline.views.IGameListView

class GameListPresenter(val user: User, val iGameListView: IGameListView, val userService: UserService, val gameService: FirebaseGameService) : BasePresenter {
    private val TAG = "GameListPresenter"
    val databaseRef = FirebaseDatabase.getInstance().reference
    lateinit var gameListRef : ChildEventListener

    override fun subscribe() {
        iGameListView.showGameList()
        processGames()
    }

    override fun unsubscribe() {
        databaseRef.removeEventListener(gameListRef)
    }

    fun processGames() {


        gameListRef = gameService.getGames().addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        Log.d(TAG, "friend :" + dataSnapshot.value!!)
                        val game = dataSnapshot.getValue<Game>(Game::class.java)
                        game?.gameId = dataSnapshot.key
                        game?.let{ processGameOpponentUsername(game, true)}
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                        val game = dataSnapshot.getValue<Game>(Game::class.java)
                        game?.gameId = dataSnapshot.key
                        Log.d(TAG, "onChildChanged :" + dataSnapshot.value!!)
                        game?.let{ processGameOpponentUsername(game,false)}
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

    fun processGameOpponentUsername(game: Game, newGame: Boolean) {
        userService.getUser(game.opponentId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue<User>(User::class.java)
                            game.opponentUsername = user?.username
                        }
                        if (newGame) {
                            iGameListView.showAddedGame(game)
                        } else {
                            iGameListView.showChangedGame(game)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        if (newGame) {
                            iGameListView.showAddedGame(game)
                        } else {
                            iGameListView.showChangedGame(game)
                        }
                    }
                }
        )
    }

    fun addGame(oponent: String) {
        userService.getUserByUsername(oponent).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            iGameListView.showNotExistFriend(oponent)
                        } else {
                            val oponentUser = dataSnapshot.getValue<User>(User::class.java)
                            oponentUser?.let{gameService.addGame(Game(null, true, "X", oponentUser?.uid, null))}
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        iGameListView.showNotExistFriend(oponent)
                    }
                }
        )
    }

}