package com.impecabel.tictactoeonline.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.impecabel.tictactoeonline.R
import com.impecabel.tictactoeonline.data.model.Game
import com.impecabel.tictactoeonline.data.model.User
import com.impecabel.tictactoeonline.data.source.remote.FirebaseGameService
import com.impecabel.tictactoeonline.data.source.remote.UserService

import com.impecabel.tictactoeonline.presenters.GameListPresenter
import kotlinx.android.synthetic.main.activity_game_list.*
import kotlinx.android.synthetic.main.game_list_content.view.*

import kotlinx.android.synthetic.main.game_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [GameDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class GameListActivity : AppCompatActivity(), IGameListView {
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false

    private var gameListPresenter: GameListPresenter? = null

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        setSupportActionBar(toolbar)
        toolbar.title = title
        val bundle = intent.getBundleExtra("bundle")
        user = bundle.getParcelable<User>("user") as User
        gameListPresenter = GameListPresenter(user, this, UserService(), FirebaseGameService(user))

        fab.setOnClickListener { view ->
           val builder = AlertDialog.Builder(this)

            builder.setTitle("Insert your friend name")
            builder.setMessage("Be sure to enter")

            val etLogin = EditText(this)
            etLogin.setSingleLine()
            builder.setView(etLogin)

            builder.setPositiveButton("OK") { dialog, whichButton -> gameListPresenter?.addGame(etLogin.text.toString()) }

            builder.setNegativeButton("Cancel") { dialog, whichButton -> }
            builder.show()

        }

        if (game_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

    }

    override fun showNotExistFriend(username: String) {
        Toast.makeText(this, username + " ", Toast.LENGTH_LONG).show()
       /* Snackbar.make(view, "$username does not exist", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
    }

    override fun onStart() {
        super.onStart()
        gameListPresenter?.subscribe()
    }

    override fun onStop() {
        super.onStop()
        gameListPresenter?.unsubscribe()
    }

    override fun showGameList() {
        game_list.adapter = SimpleItemRecyclerViewAdapter(this, mutableListOf<Game>(), mTwoPane, user)
    }

    override fun showAddedGame(game: Game) {
        val adapter = game_list.adapter as SimpleItemRecyclerViewAdapter
        adapter.onGameAdded(game)
    }

    override fun showChangedGame(game: Game) {
        val adapter = game_list.adapter as SimpleItemRecyclerViewAdapter
        adapter.onGameChanged(game)
    }

    class SimpleItemRecyclerViewAdapter(private val mParentActivity: GameListActivity,
                                        private val mValues: MutableList <Game>,
                                        private val mTwoPane: Boolean,
                                        private val user: User?) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as Game
                if (mTwoPane) {
                    val fragment = GameDetailFragment().apply {
                        arguments = Bundle()
                        arguments.putParcelable("user", user)
                        arguments.putString(GameDetailFragment.ARG_ITEM_ID, item.gameId)
                    }
                    mParentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.game_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, GameDetailActivity::class.java).apply {
                        var bundle = Bundle()
                        bundle.putParcelable("user", user)
                        putExtra("bundle", bundle)
                        putExtra(GameDetailFragment.ARG_ITEM_ID, item.gameId)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.game_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mValues[position]
            holder.mIdView.text = "${item.opponentUsername} - ${item.firstPlayer}"
            //holder.mContentView.text = item.gameBoard

            with(holder.itemView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        fun onGameAdded(game: Game) {

            mValues.add(game)
            notifyItemChanged(mValues.size - 1)
        }

        fun onGameChanged( game: Game) {
            val index = mValues.indexOfFirst { it.gameId == game.gameId }
            if (index > -1) {
                mValues[index] = game
                notifyItemChanged(index)
            } else {
                // TODO : wrong friend
                Log.d("fisache", "onGameChanged null")
            }
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.id_text
            val mContentView: TextView = mView.content
        }
    }
}
