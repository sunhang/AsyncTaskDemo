package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.sunhang.asynctaskdemo.coroutines.CoroutineViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI

class MainListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = MainListAdapter()
            }
        }.view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(CoroutineViewModel::class.java)
        viewModel.allCourses.observe(viewLifecycleOwner, Observer {

        })
    }

    class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListViewHolder>() {
        companion object {
            val textViewId = View.generateViewId()
        }

        class MainListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(textViewId)!!
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
            val context = parent.context

            val view = AnkoContext.create(context).apply {
                frameLayout {
                    lparams(matchParent, wrapContent)
                    textView {
                        id = textViewId
                        layoutParams = ViewGroup.LayoutParams(matchParent, context.dp2Px(50))
                    }
                }
            }.view

            return MainListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 100
        }

        override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
            holder.textView.text = "position: $position"
        }
    }

}
