package ru.hse.equeue.ui.search.search_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.hse.equeue.R
import ru.hse.equeue.databinding.ListItemQueueBinding
import ru.hse.equeue.databinding.SearchListItemQueueBinding
import ru.hse.equeue.model.Queue
import ru.hse.equeue.ui.queues.ActiveQueueViewModel
import ru.hse.equeue.ui.search.SearchViewModel

class SearchQueuesAdapter(
    private val navController: NavController,
    private val queueViewModel: ActiveQueueViewModel
) : RecyclerView.Adapter<SearchQueuesAdapter.QueueViewHolder>(), View.OnClickListener {

    var queus: List<Queue> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var prevUpdateSize = -1

    class QueueViewHolder(
        val binding: SearchListItemQueueBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SearchListItemQueueBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return QueueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val queue = queus[position]
        with(holder.binding) {
            holder.itemView.tag = queue
            queueNameTextView.text = queue.name
            addressQueueTextView.text = queue.address
            if (queue.photoUrl.isNotBlank()) {
                Picasso.get().load(queue.photoUrl).into(photoQueueImageView);
            } else {
                photoQueueImageView.setImageResource(R.drawable.queue_default_image_24)
            }
        }
    }

    override fun getItemCount(): Int = queus.size


    override fun onClick(v: View?) {
        val queue = v?.tag as Queue
        queueViewModel.selectedQueue.value = queue
        navController.navigate(R.id.action_listSearchFragment2_to_queueInfoFragment)
    }


}