package com.impecabel.tictactoeonline.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.impecabel.tictactoeonline.R
import com.impecabel.tictactoeonline.R.id.statusText
import com.impecabel.tictactoeonline.data.model.Game
import com.impecabel.tictactoeonline.data.model.User
import com.impecabel.tictactoeonline.data.source.remote.FirebaseGameService
import com.impecabel.tictactoeonline.data.source.remote.UserService
import com.impecabel.tictactoeonline.dummy.DummyContent
import com.impecabel.tictactoeonline.presenters.GameDetailPresenter
import com.impecabel.tictactoeonline.presenters.GameListPresenter
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.game_detail.*
import kotlinx.android.synthetic.main.game_detail.view.*

class GameDetailFragment : Fragment(), IGameDetailView, View.OnClickListener {

    private val TAG = "GAMEDETAIL"
    private lateinit var gameId: String
    private lateinit var user: User
    private var gameDetailPresenter: GameDetailPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            gameId = arguments.getString(ARG_ITEM_ID)
            user = arguments.getParcelable<User>("user") as User
            gameDetailPresenter = GameDetailPresenter(gameId, user, this, UserService(), FirebaseGameService(user))

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.game_detail, container, false)
        for(i in 0 until rootView.game_board.childCount) {
            for(j in 0 until (rootView.game_board.getChildAt(i) as ViewGroup).childCount){
                (rootView.game_board.getChildAt(i) as ViewGroup).getChildAt(j).setOnClickListener(this)
            }
        }
        return rootView
    }

    override fun onStart() {
        super.onStart()

        gameDetailPresenter?.subscribe()
    }

    override fun onStop() {
        super.onStop()
        gameDetailPresenter?.unsubscribe()
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "clicked ${v?.tag}")

        if (game_board.isEnabled && v is TextView && v.text == "") {
            val clickPosition = v?.tag.toString().split(",").map { it.toInt() }
            gameDetailPresenter?.processClick(clickPosition)
           // v.text = "X"
            game_board.isEnabled = false
            Log.d(TAG, "clicked  Coordinates $clickPosition")

        }


    }


    override fun updateTile(position: List<Int>, marker: String) {
        ((game_board.getChildAt(position[0]) as ViewGroup).getChildAt(position[1]) as TextView).text = marker
    }

    override fun showGame(game: Game) {
        activity.toolbar?.title = "${user.username} vs ${game.opponentUsername}"

        statusText.text = "${game.gameStatus} - ${game.gameBoard}"

        game_board.isEnabled = game.gameStatus == Game.GameStatus.PLAYING

        game.gameBoard.forEachIndexed { rowIndex, row -> row.forEachIndexed { columnIndex, column -> when(column) {
            0 -> ((game_board.getChildAt(rowIndex) as ViewGroup).getChildAt(columnIndex) as TextView).text = ""
            1 -> ((game_board.getChildAt(rowIndex) as ViewGroup).getChildAt(columnIndex) as TextView).text = "X"
            2 -> ((game_board.getChildAt(rowIndex) as ViewGroup).getChildAt(columnIndex) as TextView).text = "O"
        }} }

    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
